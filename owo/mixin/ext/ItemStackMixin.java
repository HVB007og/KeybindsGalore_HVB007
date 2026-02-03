package io.wispforest.owo.mixin.ext;

import io.wispforest.owo.ext.DerivedComponentMap;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_9323;
import net.minecraft.class_9326;
import net.minecraft.class_9335;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_1799.class)
public class ItemStackMixin {
    @Shadow @Final
    class_9335 components;

    @Unique private DerivedComponentMap derivedMap;

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("TAIL"))
    private void injectDerivedComponentMap(class_1935 item, int count, class_9335 components, CallbackInfo ci) {
        var base = ((PatchedDataComponentMapAccessor)(Object) this.components).owo$getPrototype();

        if (base instanceof DerivedComponentMap derived) {
            derivedMap = derived;
        } else {
            derivedMap = new DerivedComponentMap(base);
            ((PatchedDataComponentMapAccessor)(Object) this.components).owo$setPrototype(derivedMap);
        }
    }

    @Inject(method = "applyComponentsAndValidate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/PatchedDataComponentMap;applyPatch(Lnet/minecraft/core/component/DataComponentPatch;)V", shift = At.Shift.AFTER))
    private void deriveComponents2(class_9326 changes, CallbackInfo ci) {
        if (derivedMap == null) return;
        derivedMap.derive((class_1799)(Object) this);
    }

    @Inject(method = "applyComponents(Lnet/minecraft/core/component/DataComponentPatch;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/PatchedDataComponentMap;applyPatch(Lnet/minecraft/core/component/DataComponentPatch;)V", shift = At.Shift.AFTER))
    private void deriveComponents3(class_9326 changes, CallbackInfo ci) {
        if (derivedMap == null) return;
        derivedMap.derive((class_1799)(Object) this);
    }

    @Inject(method = "applyComponents(Lnet/minecraft/core/component/DataComponentMap;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/PatchedDataComponentMap;setAll(Lnet/minecraft/core/component/DataComponentMap;)V", shift = At.Shift.AFTER))
    private void deriveComponents4(class_9323 components, CallbackInfo ci) {
        if (derivedMap == null) return;
        derivedMap.derive((class_1799)(Object) this);
    }
}
