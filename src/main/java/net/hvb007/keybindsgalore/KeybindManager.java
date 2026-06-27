package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.fabricmc.loader.api.FabricLoader;

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

    /**
     * Safely gets the translation key (ID) of a keybinding.
     */
    public static String safeGetTranslationKey(KeyMapping binding) {
        return binding.getName();
    }

    /**
     * Safely gets the display name of a keybinding's category.
     * Uses getPath() to strip the namespace (e.g. "minecraft:debug" -> "debug")
     * so that config values like "Debug" match correctly.
     */
    public static String safeGetCategory(KeyMapping binding) {
        return binding.getCategory().id().getPath();
    }

    /**
     * Scans all registered keybindings and populates the conflictTable.
     * This is the core of the conflict detection system.
     */
    public static void findAllConflicts() {
        // If Amecs (or a fork) is loaded, skip all conflict detection — Amecs
        // intentionally allows multiple keybindings per key and handles them itself.
        if (isAmecsLoaded()) {
            conflictTable.clear();
            shownConflictWarnings.clear();
            return;
        }

        KeybindsGalore.LOGGER.info("Scanning for conflicting keybinds...");
        Minecraft client = Minecraft.getInstance();
        
        validateAndMigratePriorityKeybinds(client);

        conflictTable.clear();
        shownConflictWarnings.clear(); // Clear previous warnings

        for (KeyMapping keybinding : client.options.keyMappings) { // Use client.options.allKeys for Yarn
            // 1. Filter out keybinds from configured categories.
            if (Configurations.FILTERED_CATEGORY_KEYS.stream().anyMatch(s -> s.equalsIgnoreCase(safeGetCategory(keybinding)))) {
                continue;
            }

            InputConstants.Key physicalKey = ((KeyMappingAccessor) keybinding).getKey();
            if (physicalKey.getValue() == GLFW.GLFW_KEY_UNKNOWN) {
                continue; // Ignore unbound keys.
            }

            conflictTable.computeIfAbsent(physicalKey, k -> new ArrayList<>()).add(keybinding);
        }

        // Clean up the table by removing entries with no actual conflicts.
        conflictTable.keySet().removeIf(key -> conflictTable.get(key).size() < 2);
    }

    public static boolean isAmecsLoaded() {
        return FabricLoader.getInstance().isModLoaded("amecs")
            || FabricLoader.getInstance().isModLoaded("amecsapi")
            || FabricLoader.getInstance().isModLoaded("amecs-fork");
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
        // Let Amecs handle its own key logic when present
        if (isAmecsLoaded()) return;

        if (hasConflicts(key) && !isClickHoldKey(key)) {
            // ALWAYS cancel vanilla click for conflicting keys.
            // Vanilla's `click` method blindly increments the clickCount of whichever KeyMapping 
            // happens to be stored in its internal `MAP` for this physical key (ignoring aliases).
            // We manually increment the correct priority key's clickCount in handleKeyPress.
            ci.cancel();
        }
    }

    /**
     * Determines which keybinding, if any, has priority for a given physical key.
     */
    public static KeyMapping getPriorityKey(InputConstants.Key key) {
        if (!hasConflicts(key)) return null;

        List<KeyMapping> conflicts = getConflicts(key);
        if (conflicts == null) return null;

        KeyMapping categoryPriority = null;

        // 1. Check for individually prioritized keybinds (EXCLUSIVE to this physical key)
        for (KeyMapping kb : conflicts) {
            String priorityPair = safeGetTranslationKey(kb) + ":" + key.getName();
            if (Configurations.PRIORITY_KEYBINDS.stream().anyMatch(s -> s.equalsIgnoreCase(priorityPair))) {
                return kb; // Direct match always wins immediately
            }
            
            // Check category fallback while we loop, but don't return immediately
            if (categoryPriority == null) {
                if (Configurations.PRIORITY_CATEGORIES.stream().anyMatch(s -> s.equalsIgnoreCase(safeGetCategory(kb)))) {
                    categoryPriority = kb;
                }
            }
        }

        // 2. If no individual priority, return the category priority if found
        return categoryPriority;
    }

    /**
     * The main entry point for intercepting key presses.
     * This method decides whether to execute a priority action, open the conflict menu, or do nothing.
     */
    public static void handleKeyPress(InputConstants.Key key, boolean pressed, CallbackInfo ci) {
        // Let Amecs handle its own key logic when present
        if (isAmecsLoaded()) return;

        if (Configurations.DEBUG) {
            KeybindsGalore.LOGGER.info("[KBG DEBUG] Key Input: {} | Pressed: {}", key.getName(), pressed);
        }

        boolean wasSelectorScreenOpen = Minecraft.getInstance().gui.screen() instanceof KeybindSelectorScreen || Minecraft.getInstance().gui.screen() instanceof KeybindCircularScreen;

        if (hasConflicts(key)) {
            if (Configurations.DEBUG) {
                KeybindsGalore.LOGGER.info("[KBG DEBUG] Conflict detected for key: {}", key.getName());
            }

            if (!isClickHoldKey(key)) {
                KeyMapping priorityKey = getPriorityKey(key);

                if (priorityKey != null) {
                    if (Configurations.DEBUG) {
                        KeybindsGalore.LOGGER.info("[KBG DEBUG] Executing priority action: {} | State: {}", priorityKey.getName(), pressed ? "PRESSED" : "RELEASED");
                    }
                    
                    // Manually force the pressed state for hold actions (like moving)
                    ((KeyMappingAccessor) priorityKey).setIsDown(pressed);

                    // Force all non-priority conflicting keys to unpressed state so they
                    // never activate even if a different code path tries to set them.
                    List<KeyMapping> conflicts = getConflicts(key);
                    if (conflicts != null) {
                        for (KeyMapping kb : conflicts) {
                            if (!kb.getName().equals(priorityKey.getName())) {
                                ((KeyMappingAccessor) kb).setIsDown(false);
                                ((KeyMappingAccessor) kb).setClickCount(0);
                            }
                        }
                    }

                    if (pressed) {
                        // Manually increment the click count for click-based actions (like hotbar slots)
                        int currentClicks = ((KeyMappingAccessor) priorityKey).getClickCount();
                        ((KeyMappingAccessor) priorityKey).setClickCount(currentClicks + 1);
                        
                        // Set it as the pulse target so the mixin allows it through vanilla polling
                        KeybindsGalore.activePulseTarget = priorityKey;
                    } else {
                        // If it's released, clear the target
                        if (KeybindsGalore.activePulseTarget == priorityKey) {
                            KeybindsGalore.activePulseTarget = null;
                        }
                    }

                    // Cancel the original event so we don't accidentally trigger the non-priority conflicting keys
                    ci.cancel();

                    if (pressed && !shownConflictWarnings.contains(key)) {
                        if (Configurations.SHOW_CONFLICT_WARNINGS) {
                            Minecraft client = Minecraft.getInstance();
                            if (client.player != null) {
                                MutableComponent warningHeader = Component.literal("KeybindsGalore Warning: Key '")
                                    .append(Component.literal(key.getDisplayName().getString()).withStyle(ChatFormatting.GOLD))
                                    .append(Component.literal("' has conflicts. Prioritizing '"))
                                    .append(Component.translatable(priorityKey.getName()).withStyle(ChatFormatting.AQUA))
                                    .append(Component.literal("'."))
                                    .withStyle(ChatFormatting.RED);
                                client.player.sendSystemMessage(warningHeader);

                                // ADDED: Display other conflicting keybinds
                                MutableComponent otherKeys = Component.literal("");
                                boolean first = true;
                                for (KeyMapping otherKb : getConflicts(key)) {
                                    if (otherKb == priorityKey) continue;
                                    if (!first) {
                                        otherKeys.append(Component.literal(", ").withStyle(ChatFormatting.GRAY));
                                    }
                                    otherKeys.append(Component.translatable(otherKb.getName()).withStyle(ChatFormatting.YELLOW));
                                    first = false;
                                }

                                if (!otherKeys.getString().isEmpty()) {
                                     client.player.sendSystemMessage(
                                        Component.literal("Other conflicting keybinds: ").withStyle(ChatFormatting.GRAY)
                                        .append(otherKeys)
                                        .append(Component.literal(". Please rebind them in your controls! If you do not want to see these error Messages in Chat, Set SHOW_CONFLICT_WARNINGS=false in keybindsgalore.properties file in your config folder.").withStyle(ChatFormatting.GRAY))
                                    );
                                }
                            }
                        }
                        shownConflictWarnings.add(key);
                    }
                    return; // Return immediately, letting vanilla take over
                } else {
                    // No priority key found.
                    if (pressed) {
                        if (Configurations.DEBUG) {
                            KeybindsGalore.LOGGER.info("[KBG DEBUG] No priority found, opening conflict menu for key: {}", key.getName());
                        }
                        // Open the conflict resolution menu.
                        ci.cancel();
                        openConflictMenu(key);
                    } else {
                        // Release logic for menu
                        if (wasSelectorScreenOpen) {
                            Screen currentScreen = Minecraft.getInstance().gui.screen();
                            if (currentScreen instanceof KeybindSelectorScreen) {
                                ((KeybindSelectorScreen) currentScreen).onKeyRelease();
                            } else if (currentScreen instanceof KeybindCircularScreen) {
                                ((KeybindCircularScreen) currentScreen).onKeyRelease();
                            }
                        }
                        
                        // Also ensure all conflicting keys are released
                        List<KeyMapping> conflicts = getConflicts(key);
                        if (conflicts != null) {
                            for (KeyMapping kb : conflicts) {
                                ((KeyMappingAccessor) kb).setIsDown(false);
                            }
                        }
                        ci.cancel();
                    }
                }
                return;
            }
        } else if (Configurations.DEBUG) {
            KeybindsGalore.LOGGER.info("[KBG DEBUG] No conflicts for key: {}", key.getName());
        }

        // --- RELEASE LOGIC FOR PULSE ---
        if (!pressed) {
            if (KeybindsGalore.pulseTimer > 0 && KeybindsGalore.activePulseTarget != null) {
                InputConstants.Key targetKey = ((KeyMappingAccessor) KeybindsGalore.activePulseTarget).getKey();
                if (key.equals(targetKey)) {
                    ((KeyMappingAccessor) KeybindsGalore.activePulseTarget).setIsDown(true);
                    ci.cancel();
                }
            }
        }
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
        KeybindsGalore.configManager.saveConfigFile();
        findAllConflicts();
        
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
            KeybindsGalore.configManager.saveConfigFile();
            findAllConflicts();
            
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendSystemMessage(
                    Component.translatable("text.keybindsgalore.priority_removed", 
                    Component.translatable(key.getName()))
                );
            }
        }
    }
}
