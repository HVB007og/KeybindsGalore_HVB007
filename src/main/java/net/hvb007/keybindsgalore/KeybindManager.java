package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.mixin.KeyBindingAccessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
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
        KeybindSelectorScreen screen = new KeybindSelectorScreen(key);
        MinecraftClient.getInstance().setScreen(screen);
    }

    public static List<KeyBinding> getConflicts(InputUtil.Key key) {
        return conflictTable.get(key);
    }

    public static void handleKeyPress(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        
        // 1. Check if the Selector Screen is open and needs to handle the release
        if (!pressed) {
            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            if (currentScreen instanceof KeybindSelectorScreen) {
                KeybindsGalore.LOGGER.info("handleKeyPress: Delegating RELEASE to KeybindSelectorScreen");
                ((KeybindSelectorScreen) currentScreen).onKeyRelease();
                // After this call, pulseTimer should be set if a selection was made.
            }
        }

        // 2. Nuclear Pulse Protection
        if (!pressed && KeybindsGalore.pulseTimer > 0 && KeybindsGalore.activePulseTarget != null) {
            InputUtil.Key targetKey = ((KeyBindingAccessor)KeybindsGalore.activePulseTarget).getBoundKey();
            if (key.equals(targetKey)) {
                KeybindsGalore.LOGGER.info("handleKeyPress: Intercepting RELEASE for Nuclear Pulse. Key={}", key);
                
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

        if (hasConflicts(key)) {
            if (isClickHoldKey(key)) {
                KeyBinding clickHoldBinding = clickHoldKeys.get(key.getCode());
                if (clickHoldBinding == null) {
                    if (!pressed) clickHoldKeys.remove(key.getCode());
                    ci.cancel();
                    return;
                }
                ci.cancel();
                if (pressed) {
                    ((KeyBindingAccessor) clickHoldBinding).setPressed(true);
                    ((KeyBindingAccessor) clickHoldBinding).setTimesPressed(1);
                } else {
                    ((KeyBindingAccessor) clickHoldBinding).setPressed(false);
                    clickHoldKeys.remove(key.getCode());
                }
            } else if (!isIgnoredKey(key)) {
                if (pressed) {
                    KeybindsGalore.LOGGER.info("Cancelling PRESS event for conflict menu");
                    ci.cancel();
                    openConflictMenu(key);
                } else {
                    KeybindsGalore.LOGGER.info("Allowing normal RELEASE event to propagate");
                }
            } else if (Configurations.USE_KEYBIND_FIX) {
                ci.cancel();
                getConflicts(key).forEach(binding -> {
                    if (pressed) {
                        ((KeyBindingAccessor) binding).setPressed(true);
                        ((KeyBindingAccessor) binding).setTimesPressed(1);
                    } else ((KeyBindingAccessor) binding).invokeReset();
                });
            }
        }
    }
}