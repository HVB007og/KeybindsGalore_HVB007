// IDK what Mixins are but this code was here and didn't require much changing and i don't remember the changes i made if there were any
package net.hvb007.keybindsgalore.mixin;

import net.hvb007.keybindsgalore.KeybindsManager;
import net.minecraft.class_304;
import net.minecraft.class_3675;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_304.class, priority = -5000)
public abstract class MixinKeyBinding {

    @Shadow private class_3675.class_306 boundKey;

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(class_3675.class_306 key, boolean pressed, CallbackInfo ci) throws Exception {
        if (pressed) {
            boolean conflicting = KeybindsManager.handleConflict(key);
            if (conflicting) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(class_3675.class_306 key, CallbackInfo ci) {
        if (KeybindsManager.isConflicting(key)) {
            ci.cancel();
            KeybindsManager.openConflictMenu(key);
        }
    }

    @Inject(method = "setPressed", at = @At("HEAD"), cancellable = true)
    private void setPressed(boolean pressed, CallbackInfo ci) {
        if (KeybindsManager.isConflicting(this.boundKey)) {
            ci.cancel();
        }
    }
}
