package net.hvb007.keybindsgalore.input.minecraft;

import net.hvb007.keybindsgalore.Configurations;
import net.hvb007.keybindsgalore.KeybindsGalore;
import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;
import net.hvb007.keybindsgalore.mixin.MinecraftAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.List;

public final class SelectionActivationService {
    private SelectionActivationService() {
    }

    public static void activate(List<KeyMapping> conflicts, KeyMapping selected) {
        if (selected == null) {
            releaseAll(conflicts);
            return;
        }

        releaseAllExcept(conflicts, selected);
        KeybindsGalore.startPulse(selected);
        ((KeyMappingAccessor) selected).setIsDown(true);
        ((KeyMappingAccessor) selected).setClickCount(1);

        Minecraft client = Minecraft.getInstance();
        if (selected.same(client.options.keyAttack) && Configurations.ENABLE_ATTACK_WORKAROUND) {
            ((MinecraftAccessor) client).setMissTime(0);
        }
    }

    public static void cancel(List<KeyMapping> conflicts) {
        releaseAll(conflicts);
    }

    private static void releaseAll(List<KeyMapping> conflicts) {
        for (KeyMapping conflict : conflicts) {
            ((KeyMappingAccessor) conflict).setIsDown(false);
        }
    }

    private static void releaseAllExcept(List<KeyMapping> conflicts, KeyMapping selected) {
        for (KeyMapping conflict : conflicts) {
            if (conflict != selected) {
                ((KeyMappingAccessor) conflict).setIsDown(false);
            }
        }
    }
}
