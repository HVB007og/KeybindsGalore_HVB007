package io.wispforest.owo.mixin.braid;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import io.wispforest.owo.braid.display.BraidDisplayBinding;
import net.minecraft.class_11658;
import net.minecraft.class_11661;
import net.minecraft.class_3695;
import net.minecraft.class_4587;
import net.minecraft.class_761;
import net.minecraft.class_9925;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_761.class)
public class LevelRendererMixin {

    @Shadow
    @Final
    private class_11661 submitNodeStorage;

    @Inject(method = "method_62214", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;submitBlockEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeStorage;)V", shift = At.Shift.AFTER))
    private void renderBraidDisplays(GpuBufferSlice gpuBufferSlice, class_11658 levelRenderState, class_3695 profiler, Matrix4f matrix4f, class_9925<?> handle, class_9925<?> handle2, boolean bl, class_9925<?> handle3, class_9925<?> handle4, CallbackInfo ci, @Local class_4587 matrixStack) {
        BraidDisplayBinding.renderAutomaticDisplays(matrixStack, levelRenderState.field_63082, submitNodeStorage);
    }
}
