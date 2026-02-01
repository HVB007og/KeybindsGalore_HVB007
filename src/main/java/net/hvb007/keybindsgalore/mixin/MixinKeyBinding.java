package net.hvb007.keybindsgalore.mixin;

import net.hvb007.keybindsgalore.KeybindManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyBinding.class, priority = -5000)
public abstract class MixinKeyBinding {

    @Shadow private InputUtil.Key boundKey;

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        KeybindManager.handleKeyPress(key, pressed, ci);
    }

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        KeybindManager.handleOnKeyPressed(key, ci);
    }

    @Inject(method = "setPressed", at = @At("HEAD"), cancellable = true)
    private void setPressed(boolean pressed, CallbackInfo ci) {
        // This mixin is intended to prevent vanilla keybinding logic from interfering
        // when our custom conflict resolution is active.
        // If the key is conflicting and we are handling it, cancel the vanilla setPressed.
        if (KeybindManager.hasConflicts(this.boundKey)) {
            ci.cancel();
        }
    }
}
