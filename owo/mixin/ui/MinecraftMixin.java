package io.wispforest.owo.mixin.ui;

import io.wispforest.owo.ui.event.ClientRenderCallback;
import io.wispforest.owo.ui.event.WindowResizeCallback;
import io.wispforest.owo.ui.renderstate.BlurQuadElementRenderState;
import io.wispforest.owo.ui.util.DisposableScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.class_1041;
import net.minecraft.class_128;
import net.minecraft.class_148;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_542;

@Mixin(class_310.class)
public class MinecraftMixin {

    @Unique
    private final Set<DisposableScreen> screensToDispose = new HashSet<>();

    @Shadow
    @Final
    private class_1041 window;

    @Shadow
    @Nullable
    public class_437 screen;

    @Inject(method = "resizeDisplay", at = @At("TAIL"))
    private void captureResize(CallbackInfo ci) {
        WindowResizeCallback.EVENT.invoker().onResized((class_310) (Object) this, this.window);
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;setErrorSection(Ljava/lang/String;)V", ordinal = 1))
    private void beforeRender(boolean tick, CallbackInfo ci) {
        ClientRenderCallback.BEFORE.invoker().onRender((class_310) (Object) this);
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;updateDisplay(Lcom/mojang/blaze3d/TracyFrameCapture;)V", shift = At.Shift.AFTER))
    private void afterRender(boolean tick, CallbackInfo ci) {
        ClientRenderCallback.AFTER.invoker().onRender((class_310) (Object) this);
    }

    @Inject(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;frameTimeNs:J"))
    private void beforeSwap(boolean tick, CallbackInfo ci) {
        ClientRenderCallback.BEFORE_SWAP.invoker().onRender((class_310) (Object) this);
    }

    @Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;removed()V"))
    private void captureSetScreen(class_437 screen, CallbackInfo ci) {
        if (screen != null && this.screen instanceof DisposableScreen disposable) {
            this.screensToDispose.add(disposable);
        } else if (screen == null) {
            if (this.screen instanceof DisposableScreen disposable) {
                this.screensToDispose.add(disposable);
            }

            for (var disposable : this.screensToDispose) {
                try {
                    disposable.dispose();
                } catch (Throwable error) {
                    var report = new class_128("Failed to dispose screen", error);
                    report.method_562("Screen being disposed: ")
                            .method_578("Screen class", disposable.getClass())
                            .method_578("Screen being closed", this.screen)
                            .method_578("Total screens to dispose", this.screensToDispose.size());

                    throw new class_148(report);
                }
            }

            this.screensToDispose.clear();
        }
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;initRenderer(JIZLcom/mojang/blaze3d/shaders/ShaderSource;Z)V", shift = At.Shift.AFTER))
    private void initBlurRenderer(class_542 args, CallbackInfo ci) {
        BlurQuadElementRenderState.initialize((class_310) (Object) this);
    }
}
