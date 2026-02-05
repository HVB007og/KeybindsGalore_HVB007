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
     */
    public static String safeGetCategory(KeyMapping binding) {
        return binding.getCategory().label().getString();
    }

    /**
     * Scans all registered keybindings and populates the conflictTable.
     * This is the core of the conflict detection system.
     */
    public static void findAllConflicts() {
        KeybindsGalore.LOGGER.info("Scanning for conflicting keybinds...");
        Minecraft client = Minecraft.getInstance();
        conflictTable.clear();
        shownConflictWarnings.clear(); // Clear previous warnings

        for (KeyMapping keybinding : client.options.keyMappings) { // Use client.options.allKeys for Yarn
            // Filter out keybinds from configured categories.
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

    /**
     * Checks if a key is configured to be ignored by this mod.
     */
    public static boolean isIgnoredKey(InputConstants.Key key) {
        return Configurations.IGNORED_KEYS.contains(key.getValue()) ^ Configurations.INVERT_IGNORED_KEYS_LIST;
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
        Minecraft.getInstance().setScreen(screen);
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
        if (hasConflicts(key) && !isIgnoredKey(key) && !isClickHoldKey(key)) {
            ci.cancel();
        }
    }

    /**
     * The main entry point for intercepting key presses.
     * This method decides whether to execute a priority action, open the conflict menu, or do nothing.
     */
    public static void handleKeyPress(InputConstants.Key key, boolean pressed, CallbackInfo ci) {
        boolean wasSelectorScreenOpen = Minecraft.getInstance().screen instanceof KeybindSelectorScreen || Minecraft.getInstance().screen instanceof KeybindCircularScreen;

        if (hasConflicts(key)) {
            // Handle ignored keys: show a warning but don't interfere.
            if (isIgnoredKey(key)) {
                if (pressed && !shownConflictWarnings.contains(key)) {
                    Minecraft client = Minecraft.getInstance();
                    if (client.player != null) {
                        MutableComponent warningHeader = Component.literal("KeybindsGalore Warning: Ignored key '")
                            .append(Component.literal(key.getDisplayName().getString()).withStyle(ChatFormatting.GOLD))
                            .append(Component.literal("' has conflicts. Letting Minecraft handle it."))
                            .withStyle(ChatFormatting.RED);
                        client.player.displayClientMessage(warningHeader, false);

                        MutableComponent otherKeys = Component.literal("");
                        boolean first = true;
                        for (KeyMapping otherKb : getConflicts(key)) {
                            if (!first) {
                                otherKeys.append(Component.literal(", ").withStyle(ChatFormatting.GRAY));
                            }
                            otherKeys.append(Component.translatable(otherKb.getName()).withStyle(ChatFormatting.YELLOW));
                            first = false;
                        }

                        if (!otherKeys.getString().isEmpty()) {
                             client.player.displayClientMessage(
                                Component.literal("Conflicting keybinds: ").withStyle(ChatFormatting.GRAY)
                                .append(otherKeys)
                                .append(Component.literal(". Please rebind them in your controls!").withStyle(ChatFormatting.GRAY)),
                                false
                            );
                        }
                        shownConflictWarnings.add(key);
                    }
                }
                // Do not cancel the event. Let Minecraft handle the key press.
                return;
            }

            // Handle non-ignored keys with the priority system.
            if (!isClickHoldKey(key)) {
                List<KeyMapping> conflicts = getConflicts(key);
                KeyMapping priorityKey = null;

                if (conflicts != null) {
                    // 1. Check for individually whitelisted keybinds
                    for (KeyMapping kb : conflicts) {
                        if (Configurations.PRIORITY_KEYBINDS.stream().anyMatch(s -> s.equalsIgnoreCase(safeGetTranslationKey(kb)))) {
                            priorityKey = kb;
                            break;
                        }
                    }

                    // 2. If no individual priority, check for priority categories
                    if (priorityKey == null) {
                        for (KeyMapping kb : conflicts) {
                            if (Configurations.PRIORITY_CATEGORIES.stream().anyMatch(s -> s.equalsIgnoreCase(safeGetCategory(kb)))) {
                                priorityKey = kb;
                                break;
                            }
                        }
                    }
                }

                if (priorityKey != null) {
                    // A priority key was found.
                    ci.cancel(); // Cancel vanilla processing for ALL keys on this bind

                    // Manually update the priority key state
                    ((KeyMappingAccessor) priorityKey).setIsDown(pressed);
                    if (pressed) {
                        ((KeyMappingAccessor) priorityKey).setClickCount(((KeyMappingAccessor) priorityKey).getKey().getValue() + 1);
                    }

                    if (pressed && !shownConflictWarnings.contains(key)) {
                        Minecraft client = Minecraft.getInstance();
                        if (client.player != null) {
                            MutableComponent warningHeader = Component.literal("KeybindsGalore Warning: Key '")
                                .append(Component.literal(key.getDisplayName().getString()).withStyle(ChatFormatting.GOLD))
                                .append(Component.literal("' has conflicts. Prioritizing '"))
                                .append(Component.translatable(priorityKey.getName()).withStyle(ChatFormatting.AQUA))
                                .append(Component.literal("'."))
                                .withStyle(ChatFormatting.RED);
                            client.player.displayClientMessage(warningHeader, false);

                            // ADDED: Display other conflicting keybinds
                            MutableComponent otherKeys = Component.literal("");
                            boolean first = true;
                            for (KeyMapping otherKb : conflicts) {
                                if (otherKb == priorityKey) continue;
                                if (!first) {
                                    otherKeys.append(Component.literal(", ").withStyle(ChatFormatting.GRAY));
                                }
                                otherKeys.append(Component.translatable(otherKb.getName()).withStyle(ChatFormatting.YELLOW));
                                first = false;
                            }

                            if (!otherKeys.getString().isEmpty()) {
                                 client.player.displayClientMessage(
                                    Component.literal("Other conflicting keybinds: ").withStyle(ChatFormatting.GRAY)
                                    .append(otherKeys)
                                    .append(Component.literal(". Please rebind them in your controls!").withStyle(ChatFormatting.GRAY)),
                                    false
                                );
                            }
                            shownConflictWarnings.add(key);
                        }
                    }
                    return;
                } else {
                    // No priority key found.
                    if (pressed) {
                        // Open the conflict resolution menu.
                        ci.cancel();
                        openConflictMenu(key);
                    } else {
                        // Release logic for menu
                        if (wasSelectorScreenOpen) {
                            Screen currentScreen = Minecraft.getInstance().screen;
                            if (currentScreen instanceof KeybindSelectorScreen) {
                                ((KeybindSelectorScreen) currentScreen).onKeyRelease();
                            } else if (currentScreen instanceof KeybindCircularScreen) {
                                ((KeybindCircularScreen) currentScreen).onKeyRelease();
                            }
                        }
                        
                        // Also ensure all conflicting keys are released
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
}
