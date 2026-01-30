package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.mixin.KeyBindingAccessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.KeyBinding.Category; // Import KeyBinding.Category
import net.minecraft.client.util.InputUtil;

import java.util.*;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class KeybindManager
{
    public static final Hashtable<InputUtil.Key, List<KeyBinding>> conflictTable = new Hashtable<>();
    public static final HashMap<Integer, KeyBinding> clickHoldKeys = new HashMap<>();

    public static String safeGetTranslationKey(KeyBinding binding) {
        return binding.getId();
    }

    public static String safeGetCategory(KeyBinding binding) {
        return binding.getCategory().getLabel().getString();
    }

    public static void findAllConflicts() {
        KeybindsGalore.LOGGER.info("Performing lazy conflict check");
        MinecraftClient client = MinecraftClient.getInstance();
        conflictTable.clear();
        for (KeyBinding keybinding : client.options.allKeys) {
            String id = keybinding.getId();

            // Filter out debug keys to prevent them from cluttering the menu
            if (Configurations.FILTER_DEBUG_KEYS) {
                String categoryName = safeGetCategory(keybinding);
                if (categoryName.toLowerCase().contains("debug") || id.toLowerCase().contains("debug")) {
                    continue;
                }
            }

            InputUtil.Key physicalKey = ((KeyBindingAccessor) keybinding).getBoundKey();
            if (physicalKey.getCode() == GLFW.GLFW_KEY_UNKNOWN) continue;
            conflictTable.computeIfAbsent(physicalKey, key -> new ArrayList<>());
            conflictTable.get(physicalKey).add(keybinding);
        }
        new HashSet<>(conflictTable.keySet()).forEach(key -> {
            if (conflictTable.get(key).size() < 2) conflictTable.remove(key);
        });
    }

    public static boolean isIgnoredKey(InputUtil.Key key) {
        return Configurations.IGNORED_KEYS.contains(key.getCode()) ^ Configurations.INVERT_IGNORED_KEYS_LIST;
    }

    public static boolean isClickHoldKey(InputUtil.Key key) {
        return clickHoldKeys.containsKey(key.getCode());
    }

    public static boolean hasConflicts(InputUtil.Key key) {
        return conflictTable.containsKey(key);
    }

    public static void openConflictMenu(InputUtil.Key key) {
        KeybindsGalore.debugLog("Opening conflict menu for key: {}", key);
        KeybindSelectorScreen screen = new KeybindSelectorScreen(key);
        MinecraftClient.getInstance().setScreen(screen);
    }

    public static List<KeyBinding> getConflicts(InputUtil.Key key) {
        return conflictTable.get(key);
    }

    public static void handleOnKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        if (hasConflicts(key) && !isIgnoredKey(key) && !isClickHoldKey(key)) {
            // We cancel onKeyPressed here to prevent timesPressed from incrementing for any conflicting key.
            // This is crucial for both menu opening and priority key execution.
            KeybindsGalore.debugLog("handleOnKeyPressed: Cancelling vanilla timesPressed increment for conflict key: {}", key);
            ci.cancel();
        }
    }

    public static void handleKeyPress(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        
        boolean wasSelectorScreenOpen = MinecraftClient.getInstance().currentScreen instanceof KeybindSelectorScreen;

        // --- PRESS LOGIC ---
        if (pressed && hasConflicts(key) && !isIgnoredKey(key) && !isClickHoldKey(key)) {
            List<KeyBinding> conflicts = getConflicts(key);
            KeyBinding priorityKey = null;

            // Check for priority keys (e.g., Movement category)
            if (conflicts != null) {
                for (KeyBinding kb : conflicts) {
                    if (kb.getCategory() == Category.MOVEMENT) { // Check if it's a Movement key
                        priorityKey = kb;
                        break;
                    }
                }
            }

            if (priorityKey != null) {
                // --- PRIORITY KEY DETECTED ---
                KeybindsGalore.debugLog("handleKeyPress (PRESS): Priority key '{}' detected for key: {}. Executing priority action.", priorityKey.getId(), key);
                ci.cancel(); // Cancel the vanilla event to prevent other conflicts from firing

                // Manually "press" the priority key
                ((KeyBindingAccessor) priorityKey).setPressed(true);
                ((KeyBindingAccessor) priorityKey).setTimesPressed(1);

                // Activate Nuclear Pulse protection for the priority key
                KeybindsGalore.activePulseTarget = priorityKey;
                KeybindsGalore.pulseTimer = 5;

                return; // Do NOT open the conflict menu
            } else {
                // --- NO PRIORITY KEY, OPEN MENU ---
                KeybindsGalore.debugLog("handleKeyPress (PRESS): Detected conflict for key: {}. Opening menu.", key);
                ci.cancel(); // Cancel the vanilla event
                openConflictMenu(key); // Open our menu
                return;
            }
        }

        // --- RELEASE LOGIC ---
        if (!pressed) {
            if (wasSelectorScreenOpen) {
                KeybindsGalore.debugLog("handleKeyPress (RELEASE): Detected release while selector screen was open.");
                Screen currentScreen = MinecraftClient.getInstance().currentScreen;
                if (currentScreen instanceof KeybindSelectorScreen) {
                     ((KeybindSelectorScreen) currentScreen).onKeyRelease();
                }
            }

            if (KeybindsGalore.pulseTimer > 0 && KeybindsGalore.activePulseTarget != null) {
                InputUtil.Key targetKey = ((KeyBindingAccessor)KeybindsGalore.activePulseTarget).getBoundKey();
                if (key.equals(targetKey)) {
                    KeybindsGalore.debugLog("handleKeyPress (RELEASE): Intercepting for Nuclear Pulse (Selection Made).");
                    ((KeyBindingAccessor)KeybindsGalore.activePulseTarget).setPressed(true);
                    
                    List<KeyBinding> conflicts = getConflicts(key);
                    if (conflicts != null) {
                        for (KeyBinding kb : conflicts) {
                            if (kb != KeybindsGalore.activePulseTarget) {
                                ((KeyBindingAccessor)kb).setPressed(false);
                                ((KeyBindingAccessor)kb).setTimesPressed(0);
                            }
                        }
                    }
                    ci.cancel();
                    return;
                }
            }
            
            if (wasSelectorScreenOpen && KeybindsGalore.pulseTimer == 0) {
                 KeybindsGalore.debugLog("handleKeyPress (RELEASE): Intercepting for Explicit Cancel (No Selection).");
                 List<KeyBinding> conflicts = getConflicts(key);
                 if (conflicts != null) {
                     for (KeyBinding kb : conflicts) {
                         ((KeyBindingAccessor)kb).setPressed(false);
                         ((KeyBindingAccessor)kb).setTimesPressed(0);
                     }
                 }
                 ci.cancel();
                 return;
            }
        }
    }
}