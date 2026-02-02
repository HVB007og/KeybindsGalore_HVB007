package net.hvb007.keybindsgalore.mixin;

import net.hvb007.keybindsgalore.KeybindManager;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyMapping.class, priority = -5000)
public abstract class MixinKeyMapping {

    @Shadow private InputConstants.Key key;

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(InputConstants.Key key, boolean pressed, CallbackInfo ci) {
        KeybindManager.handleKeyPress(key, pressed, ci);
    }

    @Inject(method = "click", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(InputConstants.Key key, CallbackInfo ci) {
        KeybindManager.handleOnKeyPressed(key, ci);
    }

    @Inject(method = "setDown", at = @At("HEAD"), cancellable = true)
    private void setPressed(boolean pressed, CallbackInfo ci) {
        // This mixin is intended to prevent vanilla keybinding logic from interfering
        // when our custom conflict resolution is active.
        // If the key is conflicting and we are handling it, cancel the vanilla setPressed.
        if (KeybindManager.hasConflicts(this.key)) {
            ci.cancel();
        }
    }
}
