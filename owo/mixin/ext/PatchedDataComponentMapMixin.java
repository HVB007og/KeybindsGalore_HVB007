package io.wispforest.owo.mixin.ext;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.owo.ext.DerivedComponentMap;
import net.minecraft.class_9323;
import net.minecraft.class_9335;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(class_9335.class)
public class PatchedDataComponentMapMixin {
    @ModifyExpressionValue(method = "copy", at = @At(value = "FIELD", target = "Lnet/minecraft/core/component/PatchedDataComponentMap;prototype:Lnet/minecraft/core/component/DataComponentMap;"))
    private class_9323 reWrapDerived(class_9323 original) {
        return DerivedComponentMap.reWrapIfNeeded(original);
    }

    @WrapOperation(method = "equals", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/DataComponentMap;equals(Ljava/lang/Object;)Z"))
    private boolean prioritiseDerivedMap(class_9323 instance, Object object, Operation<Boolean> original) {
        return (object instanceof DerivedComponentMap derivedComponentMap)
            ? original.call(derivedComponentMap, instance)
            : original.call(instance, object);
    }
}
