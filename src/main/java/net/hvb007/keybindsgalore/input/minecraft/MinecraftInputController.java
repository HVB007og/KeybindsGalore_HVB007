package net.hvb007.keybindsgalore.input.minecraft;

import com.mojang.blaze3d.platform.InputConstants;
import net.hvb007.keybindsgalore.Configurations;
import net.hvb007.keybindsgalore.KeybindCircularScreen;
import net.hvb007.keybindsgalore.KeybindManager;
import net.hvb007.keybindsgalore.KeybindSelectorScreen;
import net.hvb007.keybindsgalore.KeybindsGalore;
import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public final class MinecraftInputController {
    public static boolean shouldBlockSetDown(KeyMapping self, InputConstants.Key key, boolean pressed) {
        if (!pressed || !KeybindManager.hasConflicts(key)) {
            return false;
        }
        if (KeybindsGalore.shouldBlockCapturedKey(key)) {
            return true;
        }

        KeyMapping priority = KeybindManager.getPriorityKey(key);
        if (priority == self) {
            return false;
        }
        if (KeybindsGalore.activePulseTarget == self) {
            return false;
        }
        if (KeybindsGalore.isPriorityTarget(self)) {
            return false;
        }
        return !self.getName().equals("key.toggleGui");
    }

    public static void handleOnKeyPressed(InputConstants.Key key, CancellationPort cancellation) {
        if (KeybindsGalore.shouldBlockCapturedKey(key)) {
            cancellation.cancel();
            return;
        }
        if (KeybindManager.hasConflicts(key)) {
            cancellation.cancel();
        }
    }

    public static void handleKeyPress(InputConstants.Key key, boolean pressed, CancellationPort cancellation) {
        if (pressed && KeybindsGalore.isCaptureKey(key)) {
            if (Configurations.DEBUG) {
                KeybindsGalore.LOGGER.info("[KBG DEBUG] Capture key pressed: {}", key.getName());
            }
            if (KeybindsGalore.tryOpenCaptureScreen(key)) {
                cancellation.cancel();
                return;
            }
        }

        if (Configurations.DEBUG) {
            KeybindsGalore.LOGGER.info("[KBG DEBUG] Key Input: {} | Pressed: {}", key.getName(), pressed);
        }

        if (pressed && KeybindsGalore.shouldBlockCapturedKey(key)) {
            cancellation.cancel();
            return;
        }

        if (KeybindManager.hasConflicts(key)) {
            if (Configurations.DEBUG) {
                KeybindsGalore.LOGGER.info("[KBG DEBUG] Conflict detected for key: {}", key.getName());
            }

            KeyMapping priorityKey = KeybindManager.getPriorityKey(key);

            if (priorityKey != null) {
                if (Configurations.DEBUG) {
                    KeybindsGalore.LOGGER.info("[KBG DEBUG] Executing priority action: {} | State: {}", priorityKey.getName(), pressed ? "PRESSED" : "RELEASED");
                }

                ((KeyMappingAccessor) priorityKey).setIsDown(pressed);

                if (pressed && !KeybindsGalore.isPriorityTarget(priorityKey)) {
                    KeybindsGalore.clearPulseIf(priorityKey);
                    int currentClicks = ((KeyMappingAccessor) priorityKey).getClickCount();
                    ((KeyMappingAccessor) priorityKey).setClickCount(currentClicks + 1);
                    KeybindsGalore.addPriorityTarget(priorityKey);
                } else if (!pressed) {
                    KeybindsGalore.removePriorityTarget(priorityKey);
                }

                cancellation.cancel();

                if (pressed && !KeybindManager.shownConflictWarnings.contains(key)) {
                    LocalPlayer player = Minecraft.getInstance().player;
                    if (Configurations.SHOW_CONFLICT_WARNINGS && player != null) {
                        sendConflictWarning(player, key, priorityKey);
                    }
                    KeybindManager.shownConflictWarnings.add(key);
                }
                return;
            }

            if (pressed) {
                if (Configurations.DEBUG) {
                    KeybindsGalore.LOGGER.info("[KBG DEBUG] No priority found, opening conflict menu for key: {}", key.getName());
                }
                KeybindsGalore.resetInputOwnership();
                cancellation.cancel();
                KeybindManager.openConflictMenu(key);
            } else {
                releaseConflictingBindings(key);
                cancellation.cancel();
            }
            return;
        } else if (Configurations.DEBUG) {
            KeybindsGalore.LOGGER.info("[KBG DEBUG] No conflicts for key: {}", key.getName());
        }

        if (!pressed && KeybindsGalore.pulseTimer > 0 && KeybindsGalore.activePulseTarget != null) {
            InputConstants.Key targetKey = ((KeyMappingAccessor) KeybindsGalore.activePulseTarget).getKey();
            if (key.equals(targetKey)) {
                ((KeyMappingAccessor) KeybindsGalore.activePulseTarget).setIsDown(true);
                cancellation.cancel();
            }
        }
    }

    private static void releaseConflictingBindings(InputConstants.Key key) {
        Screen currentScreen = Minecraft.getInstance().gui.screen();
        if (currentScreen instanceof KeybindSelectorScreen selector && selector.ownsKey(key)) {
            selector.onKeyRelease(key);
            return;
        }
        if (currentScreen instanceof KeybindCircularScreen circular && circular.ownsKey(key)) {
            circular.onKeyRelease(key);
            return;
        }

        List<KeyMapping> conflicts = KeybindManager.getConflicts(key);
        if (conflicts == null) {
            return;
        }
        for (KeyMapping binding : conflicts) {
            ((KeyMappingAccessor) binding).setIsDown(false);
        }
    }

    private static void sendConflictWarning(LocalPlayer target, InputConstants.Key key, KeyMapping priorityKey) {
        MutableComponent header = Component.literal("KeybindsGalore Warning: Key '")
                .append(Component.literal(key.getDisplayName().getString()).withStyle(ChatFormatting.GOLD))
                .append(Component.literal("' has conflicts. Prioritizing '"))
                .append(Component.translatable(priorityKey.getName()).withStyle(ChatFormatting.AQUA))
                .append(Component.literal("'."))
                .withStyle(ChatFormatting.RED);
        target.sendSystemMessage(header);

        MutableComponent others = Component.literal("");
        boolean first = true;
        for (KeyMapping other : KeybindManager.getConflicts(key)) {
            if (other == priorityKey) {
                continue;
            }
            if (!first) {
                others.append(Component.literal(", ").withStyle(ChatFormatting.GRAY));
            }
            others.append(Component.translatable(other.getName()).withStyle(ChatFormatting.YELLOW));
            first = false;
        }
        if (others.getString().isEmpty()) {
            return;
        }
        target.sendSystemMessage(
                Component.literal("Other conflicting keybinds: ").withStyle(ChatFormatting.GRAY)
                        .append(others)
                        .append(Component.literal(". Please rebind them in your controls! If you do not want to see these error Messages in Chat, Set SHOW_CONFLICT_WARNINGS=false in keybindsgalore.properties file in your config folder.").withStyle(ChatFormatting.GRAY))
        );
    }

    @FunctionalInterface
    public interface CancellationPort {
        void cancel();
    }
}
