package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.core.PriorityCandidate;
import net.hvb007.keybindsgalore.core.PriorityResolver;
import net.hvb007.keybindsgalore.input.minecraft.MinecraftBindingCatalog;
import net.hvb007.keybindsgalore.input.minecraft.MinecraftConflictIndex;
import net.hvb007.keybindsgalore.input.minecraft.MinecraftInputController;
import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

/**
 * Manages the detection and resolution of conflicting keybinds.
 */
public class KeybindManager {
    // Maps a physical key to a list of all KeyBinding objects bound to it.
    public static final Map<InputConstants.Key, List<KeyMapping>> conflictTable = new HashMap<>();
    // Tracks keys that are in "click and hold" mode. This is a placeholder for a future feature.
    public static final HashMap<Integer, KeyMapping> clickHoldKeys = new HashMap<>();
    // Tracks which conflict warnings have been shown to the player in this session.
    public static final HashSet<InputConstants.Key> shownConflictWarnings = new HashSet<>();
    private static final PriorityResolver PRIORITY_RESOLVER = new PriorityResolver();
    private static final MinecraftConflictIndex CONFLICT_INDEX = new MinecraftConflictIndex(conflictTable);

    /**
     * Safely gets the translation key (ID) of a keybinding.
     */
    public static String safeGetTranslationKey(KeyMapping binding) {
        return MinecraftBindingCatalog.actionId(binding);
    }

    /**
     * Safely gets the display name of a keybinding's category.
     */
    public static String safeGetCategory(KeyMapping binding) {
        return MinecraftBindingCatalog.categoryId(binding);
    }

    public static String safeGetCategoryLabel(KeyMapping binding) {
        return MinecraftBindingCatalog.categoryLabel(binding);
    }

    /**
     * Scans all registered keybindings and populates the conflictTable.
     * This is the core of the conflict detection system.
     */
    public static void findAllConflicts() {
        refreshConflicts(MinecraftConflictIndex.RefreshReason.MANUAL);
    }

    public static void refreshConflicts(MinecraftConflictIndex.RefreshReason reason) {
        KeybindsGalore.LOGGER.info("Scanning for conflicting keybinds ({})...", reason);
        Minecraft client = Minecraft.getInstance();
        if (client.options == null) {
            return;
        }

        validateAndMigratePriorityKeybinds(client);
        shownConflictWarnings.clear();
        CONFLICT_INDEX.refresh(
                client.options.keyMappings,
                Configurations.FILTERED_CATEGORY_KEYS,
                reason
        );
    }

    /**
     * Migrates old priority entries (ActionID) to the new format (ActionID:KeyName).
     */
    private static void validateAndMigratePriorityKeybinds(Minecraft client) {
        boolean changed = false;
        List<String> newPriorities = new ArrayList<>();
        HashSet<String> uniqueKeys = new HashSet<>();

        for (String entry : Configurations.PRIORITY_KEYBINDS) {
            if (!entry.contains(":")) {
                // Old format. Try to find the key for this action.
                for (KeyMapping kb : client.options.keyMappings) {
                    if (kb.getName().equals(entry)) {
                        String keyName = ((KeyMappingAccessor) kb).getKey().getName();
                        String newEntry = entry + ":" + keyName;
                        if (!uniqueKeys.contains(keyName)) {
                            newPriorities.add(newEntry);
                            uniqueKeys.add(keyName);
                            changed = true;
                        }
                        break;
                    }
                }
            } else {
                String keyName = entry.split(":", 2)[1];
                if (!uniqueKeys.contains(keyName)) {
                    newPriorities.add(entry);
                    uniqueKeys.add(keyName);
                } else {
                    // Duplicate priority for the same key. Remove it.
                    changed = true;
                }
            }
        }

        if (changed) {
            Configurations.PRIORITY_KEYBINDS.clear();
            Configurations.PRIORITY_KEYBINDS.addAll(newPriorities);
            KeybindsGalore.configManager.saveConfigFile();
        }
    }

    /**
     * Checks if a key is currently in "click and hold" mode.
     */
    public static boolean isClickHoldKey(InputConstants.Key key) {
        return clickHoldKeys.containsKey(key.getValue());
    }

    /**
     * Checks if a physical key has multiple keybindings assigned to it.
     */
    public static boolean hasConflicts(InputConstants.Key key) {
        return conflictTable.containsKey(key);
    }

    /**
     * Opens the conflict resolution screen (the selection menu).
     */
    public static void openConflictMenu(InputConstants.Key key) {
        KeybindsGalore.inputState().selectorOpened();
        Screen screen;
        if (Configurations.USE_CIRCULAR_MENU) {
            screen = new KeybindCircularScreen(key);
        } else {
            screen = new KeybindSelectorScreen(key);
        }
        Minecraft.getInstance().gui.setScreen(screen);
    }

    /**
     * Returns the list of conflicting keybindings for a given physical key.
     */
    public static List<KeyMapping> getConflicts(InputConstants.Key key) {
        return conflictTable.get(key);
    }

    /**
     * Intercepts the onKeyPressed event to prevent `timesPressed` from incrementing on conflicting keys.
     * This stops the game from thinking a "click" happened when we are just opening the menu.
     */
    public static void handleOnKeyPressed(InputConstants.Key key, CallbackInfo ci) {
        MinecraftInputController.handleOnKeyPressed(key, ci::cancel);
    }

    /**
     * Determines which keybinding, if any, has priority for a given physical key.
     */
    public static KeyMapping getPriorityKey(InputConstants.Key key) {
        if (!hasConflicts(key)) return null;

        List<KeyMapping> conflicts = getConflicts(key);
        if (conflicts == null) return null;

        List<PriorityCandidate> candidates = new ArrayList<>(conflicts.size());
        for (KeyMapping binding : conflicts) {
            candidates.add(new PriorityCandidate(
                    safeGetTranslationKey(binding),
                    safeGetCategory(binding),
                    safeGetCategoryLabel(binding)
            ));
        }

        OptionalInt resolvedIndex = PRIORITY_RESOLVER.resolve(
                candidates,
                key.getName(),
                Configurations.PRIORITY_KEYBINDS,
                Configurations.PRIORITY_CATEGORIES
        );

        return resolvedIndex.isPresent() ? conflicts.get(resolvedIndex.getAsInt()) : null;
    }

    /**
     * The main entry point for intercepting key presses.
     * This method decides whether to execute a priority action, open the conflict menu, or do nothing.
     */
    public static void handleKeyPress(InputConstants.Key key, boolean pressed, CallbackInfo ci) {
        MinecraftInputController.handleKeyPress(key, pressed, ci::cancel);
    }

    /**
     * Adds a prioritized action for a specific key, replacing any existing priority for that same key.
     */
    public static void prioritizeAction(KeyMapping selected, InputConstants.Key key) {
        String newPair = selected.getName() + ":" + key.getName();
        
        // Exclusivity Check: Remove any existing priority entry that uses this same physical key
        Configurations.PRIORITY_KEYBINDS.removeIf(entry -> {
            if (entry.contains(":")) {
                String existingKeyName = entry.split(":", 2)[1];
                return existingKeyName.equalsIgnoreCase(key.getName());
            }
            return false;
        });

        Configurations.PRIORITY_KEYBINDS.add(newPair);
        KeybindsGalore.saveConfigAndRefresh();
        
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendSystemMessage(
                Component.translatable("text.keybindsgalore.action_prioritized", 
                Component.translatable(selected.getName()), 
                Component.translatable(key.getName()))
            );
        }
    }

    /**
     * Removes the prioritized action for a specific key.
     */
    public static void removePriority(InputConstants.Key key) {
        boolean removed = Configurations.PRIORITY_KEYBINDS.removeIf(entry -> {
            if (entry.contains(":")) {
                String existingKeyName = entry.split(":", 2)[1];
                return existingKeyName.equalsIgnoreCase(key.getName());
            }
            return false;
        });

        if (removed) {
            KeybindsGalore.saveConfigAndRefresh();
            
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendSystemMessage(
                    Component.translatable("text.keybindsgalore.priority_removed", 
                    Component.translatable(key.getName()))
                );
            }
        }
    }
}
