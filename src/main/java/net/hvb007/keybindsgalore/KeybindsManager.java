package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.KeyBinding.Category;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.HashSet;

/**
 * Manages the detection and resolution of conflicting keybinds.
 */
public class KeybindsManager {
    // Maps a physical key to a list of all KeyBinding objects bound to it.
    public static final Hashtable<InputUtil.Key, List<KeyBinding>> conflictTable = new Hashtable<>();
    // Tracks keys that are in "click and hold" mode.
    public static final HashMap<Integer, KeyBinding> clickHoldKeys = new HashMap<>();

    /**
     * Safely gets the translation key (ID) of a keybinding.
     */
    public static String safeGetTranslationKey(KeyBinding binding) {
        return binding.getId();
    }

    /**
     * Safely gets the display name of a keybinding's category.
     */
    public static String safeGetCategory(KeyBinding binding) {
        return binding.getCategory().getLabel().getString();
    }

    /**
     * Scans all registered keybindings and populates the conflictTable.
     * This is the core of the conflict detection system.
     */
    public static void findAllConflicts() {
        KeybindsGalore.LOGGER.info("Scanning for conflicting keybinds...");
        MinecraftClient client = MinecraftClient.getInstance();
        conflictTable.clear();

        for (KeyBinding keybinding : client.options.allKeys) {
            String id = keybinding.getId();

            // Optionally filter out keybinds from the "debug" category.
            if (Configurations.FILTER_DEBUG_KEYS) {
                String categoryName = safeGetCategory(keybinding);
                if (categoryName.toLowerCase().contains("debug") || id.toLowerCase().contains("debug")) {
                    continue;
                }
            }

            InputUtil.Key physicalKey = ((KeyBindingAccessor) keybinding).getBoundKey();
            if (physicalKey.getCode() == GLFW.GLFW_KEY_UNKNOWN) {
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
    public static boolean isIgnoredKey(InputUtil.Key key) {
        return Configurations.IGNORED_KEYS.contains(key.getCode()) ^ Configurations.INVERT_IGNORED_KEYS_LIST;
    }

    /**
     * Checks if a key is currently in "click and hold" mode.
     */
    public static boolean isClickHoldKey(InputUtil.Key key) {
        return clickHoldKeys.containsKey(key.getCode());
    }

    /**
     * Checks if a physical key has multiple keybindings assigned to it.
     */
    public static boolean hasConflicts(InputUtil.Key key) {
        return conflictTable.containsKey(key);
    }

    /**
     * Opens the conflict resolution screen (the selection menu).
     */
    public static void openConflictMenu(InputUtil.Key key) {
        Screen screen;
        if (Configurations.USE_CIRCULAR_MENU) {
            screen = new KeybindCircularScreen(key);
        } else {
            screen = new KeybindSelectorScreen(key);
        }
        MinecraftClient.getInstance().setScreen(screen);
    }

    /**
     * Returns the list of conflicting keybindings for a given physical key.
     */
    public static List<KeyBinding> getConflicts(InputUtil.Key key) {
        return conflictTable.get(key);
    }

    /**
     * Intercepts the onKeyPressed event to prevent `timesPressed` from incrementing on conflicting keys.
     * This stops the game from thinking a "click" happened when we are just opening the menu.
     */
    public static void handleOnKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        if (hasConflicts(key) && !isIgnoredKey(key) && !isClickHoldKey(key)) {
            ci.cancel();
        }
    }

    /**
     * The main entry point for intercepting key presses.
     * This method decides whether to execute a priority action, open the conflict menu, or do nothing.
     */
    public static void handleKeyPress(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        boolean wasSelectorScreenOpen = MinecraftClient.getInstance().currentScreen instanceof KeybindSelectorScreen || MinecraftClient.getInstance().currentScreen instanceof KeybindCircularScreen;

        // --- PRESS LOGIC ---
        if (pressed && hasConflicts(key) && !isIgnoredKey(key) && !isClickHoldKey(key)) {
            List<KeyBinding> conflicts = getConflicts(key);
            KeyBinding priorityKey = null;

            // Check if any of the conflicting keys are priority keys (e.g., from the MOVEMENT category).
            if (conflicts != null) {
                for (KeyBinding kb : conflicts) {
                    if (kb.getCategory() == Category.MOVEMENT) {
                        priorityKey = kb;
                        break;
                    }
                }
            }

            if (priorityKey != null) {
                // A priority key was found. Execute its action immediately and skip the menu.
                ci.cancel();
                ((KeyBindingAccessor) priorityKey).setPressed(true);
                ((KeyBindingAccessor) priorityKey).setTimesPressed(1);
                KeybindsGalore.activePulseTarget = priorityKey;
                KeybindsGalore.pulseTimer = 5; // Use pulse to ensure it stays pressed.
            } else {
                // No priority key found. Open the conflict resolution menu.
                ci.cancel();
                openConflictMenu(key);
            }
            return;
        }

        // --- RELEASE LOGIC ---
        if (!pressed) {
            // If our menu was open, let it handle the release event first.
            if (wasSelectorScreenOpen) {
                Screen currentScreen = MinecraftClient.getInstance().currentScreen;
                if (currentScreen instanceof KeybindSelectorScreen) {
                    ((KeybindSelectorScreen) currentScreen).onKeyRelease();
                } else if (currentScreen instanceof KeybindCircularScreen) {
                    ((KeybindCircularScreen) currentScreen).onKeyRelease();
                }
            }

            // If a selection was made from the menu, the pulse timer will be active.
            if (KeybindsGalore.pulseTimer > 0 && KeybindsGalore.activePulseTarget != null) {
                InputUtil.Key targetKey = ((KeyBindingAccessor) KeybindsGalore.activePulseTarget).getBoundKey();
                if (key.equals(targetKey)) {
                    // This is the release event for the key we just made a selection for.
                    // Keep the selected key pressed and cancel the vanilla release logic.
                    ((KeyBindingAccessor) KeybindsGalore.activePulseTarget).setPressed(true);
                    ci.cancel();
                }
            }
            // If the menu was closed without a selection, we need to explicitly reset all conflicting keys.
            else if (wasSelectorScreenOpen) {
                List<KeyBinding> conflicts = getConflicts(key);
                if (conflicts != null) {
                    for (KeyBinding kb : conflicts) {
                        ((KeyBindingAccessor) kb).setPressed(false);
                        ((KeyBindingAccessor) kb).setTimesPressed(0);
                    }
                }
                ci.cancel();
            }
        }
    }
}