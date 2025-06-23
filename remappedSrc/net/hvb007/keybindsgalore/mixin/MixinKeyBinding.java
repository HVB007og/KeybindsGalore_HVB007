// IDK what Mixins are but this code was here and didn't require much changing and i don't remember the changes i made if there were any
package net.hvb007.keybindsgalore.mixin;

import net.hvb007.keybindsgalore.KeybindsManager;
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
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) throws Exception {
        if (pressed) {
            // Skip conflict handling for mouse buttons
            if (key.getCategory() == InputUtil.Type.MOUSE) {
                return;
            }

            boolean conflicting = KeybindsManager.handleConflict(key);
            if (conflicting) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        // Skip conflict handling for mouse buttons
        if (key.getCategory() == InputUtil.Type.MOUSE) {
            return;
        }

        if (KeybindsManager.isConflicting(key)) {
            ci.cancel();
            KeybindsManager.openConflictMenu(key);
        }
    }

    @Inject(method = "setPressed", at = @At("HEAD"), cancellable = true)
    private void setPressed(boolean pressed, CallbackInfo ci) {
        // Skip conflict handling for mouse buttons
        if (this.boundKey.getCategory() == InputUtil.Type.MOUSE) {
            return;
        }

        if (KeybindsManager.isConflicting(this.boundKey)) {
            ci.cancel();
        }
    }

}
