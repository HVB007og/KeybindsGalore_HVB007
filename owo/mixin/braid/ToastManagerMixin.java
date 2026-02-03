package io.wispforest.owo.mixin.braid;

import io.wispforest.owo.braid.util.BraidToast;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.class_374;

@Mixin(class_374.class)
public class ToastManagerMixin {

    @Shadow
    @Final
    private List<class_374.class_375<?>> visibleToasts;

    @Inject(method = "method_61991", at = @At(value = "INVOKE", target = "Ljava/util/BitSet;clear(II)V"))
    private void disposeBraidToasts(MutableBoolean mutableBoolean, class_374.class_375<?> entry, CallbackInfoReturnable<Boolean> cir) {
        if (entry.method_2001() instanceof BraidToast toast) {
            toast.dispose();
        }
    }

    @Inject(method = "clear", at = @At("HEAD"))
    private void disposeBraidToastsEpisode2(CallbackInfo ci) {
        for (var entry : this.visibleToasts) {
            if (entry.method_2001() instanceof BraidToast toast) {
                toast.dispose();
            }
        }
    }
}
