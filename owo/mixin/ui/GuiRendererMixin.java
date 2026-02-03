package io.wispforest.owo.mixin.ui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.wispforest.owo.mixin.ui.access.GlCommandEncoderAccessor;
import io.wispforest.owo.ui.renderstate.BlurQuadElementRenderState;
import net.minecraft.class_11228;
import net.minecraft.class_11231;
import net.minecraft.class_11244;
import net.minecraft.class_310;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(class_11228.class)
public class GuiRendererMixin {

    @Shadow
    @Nullable
    private class_11231 previousTextureSetup;

    @ModifyArgs(
        method = "executeDraw(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setIndexBuffer(Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V")
    )
    private void fixNonQuadIndexing(Args args, @Local(argsOnly = true) class_11228.class_11230 draw) {
        var pipeline = draw.comp_4049();
        if (!pipeline.getLocation().method_12836().equals("owo")) return;

        if (pipeline.getVertexFormatMode() != VertexFormat.class_5596.field_27382) {
            var shapeIndexBuffer = RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
            args.set(0, shapeIndexBuffer.method_68274(draw.comp_4048()));
            args.set(1, shapeIndexBuffer.method_31924());
        }
    }

    @Inject(
        method = "executeDraw(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIII)V")
    )
    private void drawBlur(class_11228.class_11230 draw, RenderPass pass, GpuBuffer indexBuffer, VertexFormat.class_5595 indexType, CallbackInfo ci) {
        var blurSetup = BlurQuadElementRenderState.getBlurSetupOf(draw.comp_4050());
        if (blurSetup == null) return;

        var mainBuffer = class_310.method_1551().method_1522();
        var inputSize = new Vector2i(mainBuffer.field_1482, mainBuffer.field_1481);

        var encoder = RenderSystem.getDevice().createCommandEncoder();

        ((GlCommandEncoderAccessor)encoder).owo$setInRenderPass(false);
        encoder.copyTextureToTexture(
            class_310.method_1551().method_1522().method_30277(),
            BlurQuadElementRenderState.input.method_30277(),
            0, 0, 0, 0, 0, inputSize.x, inputSize.y
        );

        var uniforms = BlurQuadElementRenderState.uniforms.write(inputSize, blurSetup.directions(), blurSetup.quality(), blurSetup.size());
        ((GlCommandEncoderAccessor)encoder).owo$setInRenderPass(true);

        pass.setUniform("BlurSettings", uniforms);
        pass.bindTexture("InputSampler", BlurQuadElementRenderState.inputView, RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST));
    }

    @ModifyExpressionValue(method = "addElementToMesh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/TextureSetup;equals(Ljava/lang/Object;)Z"))
    private boolean adjustCheckForBlurElements(boolean original, @Local(argsOnly = true) class_11244 state) {
        return original && !(state instanceof BlurQuadElementRenderState || BlurQuadElementRenderState.hasBlurSetupFor(previousTextureSetup));
    }
}
