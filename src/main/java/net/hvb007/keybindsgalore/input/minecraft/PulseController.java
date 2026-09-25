package net.hvb007.keybindsgalore.input.minecraft;

import net.hvb007.keybindsgalore.KeybindsGalore;
import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;

public final class PulseController {
    public void start(KeyMapping target, int durationTicks) {
        release(KeybindsGalore.activePulseTarget);
        KeybindsGalore.activePulseTarget = target;
        KeybindsGalore.pulseTimer = Math.max(0, durationTicks);
    }

    public void tick() {
        if (KeybindsGalore.pulseTimer > 0) {
            KeybindsGalore.pulseTimer--;
        }
        if (KeybindsGalore.pulseTimer == 0) {
            release(KeybindsGalore.activePulseTarget);
            KeybindsGalore.activePulseTarget = null;
        }
    }

    public void clearIf(KeyMapping target) {
        if (KeybindsGalore.activePulseTarget == target) {
            release(target);
            KeybindsGalore.activePulseTarget = null;
            KeybindsGalore.pulseTimer = 0;
        }
    }

    public void reset() {
        release(KeybindsGalore.activePulseTarget);
        KeybindsGalore.activePulseTarget = null;
        KeybindsGalore.pulseTimer = 0;
        release(KeybindsGalore.activePriorityTarget);
        KeybindsGalore.activePriorityTarget = null;
    }

    private void release(KeyMapping target) {
        if (target != null) {
            ((KeyMappingAccessor) target).setIsDown(false);
        }
    }
}
