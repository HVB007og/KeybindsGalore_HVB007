package net.hvb007.keybindsgalore.input.minecraft;

import net.hvb007.keybindsgalore.KeybindsGalore;
import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;

public final class PulseController {
    private KeyMapping target;
    private int ticksRemaining;

    public void start(KeyMapping target, int durationTicks) {
        release(this.target);
        this.target = target;
        this.ticksRemaining = Math.max(0, durationTicks);
        syncLegacyState();
    }

    public void tick() {
        if (ticksRemaining > 0) {
            ticksRemaining--;
        }
        if (ticksRemaining == 0) {
            release(target);
            target = null;
            KeybindsGalore.inputState().released();
        }
        syncLegacyState();
    }

    public void clearIf(KeyMapping candidate) {
        if (target == candidate) {
            release(target);
            target = null;
            ticksRemaining = 0;
            KeybindsGalore.inputState().released();
            syncLegacyState();
        }
    }

    public void reset() {
        release(target);
        target = null;
        ticksRemaining = 0;
        release(KeybindsGalore.activePriorityTarget);
        KeybindsGalore.activePriorityTarget = null;
        syncLegacyState();
    }

    public KeyMapping target() {
        return target;
    }

    public int ticksRemaining() {
        return ticksRemaining;
    }

    private void syncLegacyState() {
        KeybindsGalore.activePulseTarget = target;
        KeybindsGalore.pulseTimer = ticksRemaining;
    }

    private void release(KeyMapping mapping) {
        if (mapping != null) {
            ((KeyMappingAccessor) mapping).setIsDown(false);
        }
    }
}
