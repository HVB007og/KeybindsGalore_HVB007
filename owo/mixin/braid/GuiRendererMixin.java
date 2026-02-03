package io.wispforest.owo.mixin.braid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import io.wispforest.owo.braid.core.BraidRenderPipelines;
import io.wispforest.owo.braid.util.BraidGuiRenderer;
import io.wispforest.owo.util.pond.BraidGuiRendererExtension;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;
import net.minecraft.class_11228;
import net.minecraft.class_11239;
import net.minecraft.class_11256;
import net.minecraft.class_11278;
import net.minecraft.class_12137;
import net.minecraft.class_276;

@Mixin(class_11228.class)
public class GuiRendererMixin implements BraidGuiRendererExtension {

    @Unique
    private BraidGuiRenderer.Target target = null;

    @Override
    public void owo$setTarget(BraidGuiRenderer.Target target) {
        this.target = target;
    }

    // ---

    @WrapOperation(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/CachedOrthoProjectionMatrixBuffer;getBuffer(FF)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"))
    private GpuBufferSlice injectSurfaceDimensions(class_11278 instance, float width, float height, Operation<GpuBufferSlice> original) {
        if (this.target == null) return original.call(instance, width, height);

        var surface = this.target.surface();
        return original.call(instance, (float) surface.width(), (float) surface.height());
    }

    @ModifyExpressionValue(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getMainRenderTarget()Lcom/mojang/blaze3d/pipeline/RenderTarget;"))
    private class_276 injectFramebuffer(class_276 original) {
        if (this.target == null) return original;
        return this.target.framebuffer();
    }

    @ModifyExpressionValue(method = "enableScissor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getHeight()I"))
    private int injectSurfaceHeightForScissor(int original) {
        if (this.target == null) return original;
        return this.target.framebuffer().field_1481;
    }

    @ModifyExpressionValue(method = "enableScissor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getGuiScale()I"))
    private int injectSurfaceScaleForScissor(int original) {
        if (this.target == null) return original;
        return (int) this.target.surface().scaleFactor();
    }

    @ModifyExpressionValue(method = "preparePictureInPicture", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getGuiScale()I"))
    private int injectSurfaceScaleForPIP(int original) {
        if (this.target == null) return original;
        return (int) this.target.surface().scaleFactor();
    }

    @ModifyExpressionValue(method = "getGuiScaleInvalidatingItemAtlasIfChanged", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getGuiScale()I"))
    private int injectSurfaceScaleForItemAtlas(int original) {
        if (this.target == null) return original;
        return (int) this.target.surface().scaleFactor();
    }

    // ---

    @ModifyArg(
        method = "executeDraw(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;bindTexture(Ljava/lang/String;Lcom/mojang/blaze3d/textures/GpuTextureView;Lcom/mojang/blaze3d/textures/GpuSampler;)V", ordinal = 0),
        index = 2
    )
    private @Nullable class_12137 injectTextureFilter(class_12137 sampler, @Local(argsOnly = true) class_11228.class_11230 draw) {
        if (draw.comp_4050().comp_4052() == null) {
            return sampler;
        }

        if (draw.comp_4049() == BraidRenderPipelines.TEXTURED_BILINEAR) {
            return RenderSystem.getSamplerCache().method_75293(AddressMode.REPEAT, AddressMode.REPEAT, FilterMode.LINEAR, FilterMode.LINEAR, false);
        } else if (draw.comp_4049() == BraidRenderPipelines.TEXTURED_NEAREST) {
            return RenderSystem.getSamplerCache().method_75293(AddressMode.REPEAT, AddressMode.REPEAT, FilterMode.NEAREST, FilterMode.NEAREST, false);
        } else {
            return sampler;
        }
    }

    // ---

    @ModifyExpressionValue(method = "close", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/render/GuiRenderer;pictureInPictureRenderers:Ljava/util/Map;"))
    private Map<Class<? extends class_11256>, class_11239<?>> keepAliveRenderers(Map<Class<? extends class_11256>, class_11239<?>> original) {
        if (((Object) this) instanceof BraidGuiRenderer) {
            return Map.of();
        }

        return original;
    }
}
