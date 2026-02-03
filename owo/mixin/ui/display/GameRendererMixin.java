package io.wispforest.owo.mixin.ui.display;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.wispforest.owo.braid.core.KeyModifiers;
import io.wispforest.owo.braid.core.events.MouseButtonReleaseEvent;
import io.wispforest.owo.braid.core.events.MouseMoveEvent;
import io.wispforest.owo.braid.display.BraidDisplayBinding;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_757;
import net.minecraft.class_9779;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_757.class)
public class GameRendererMixin {

    @Shadow
    @Final
    private class_310 minecraft;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;)V"))
    public void beforeWorldRender(class_9779 tickCounter, boolean tick, CallbackInfo ci) {
        BraidDisplayBinding.updateAndDrawDisplays();
    }

    @Inject(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;raycastHitResult(FLnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/HitResult;"))
    public void updateTargetDisplay(
        float tickDelta,
        CallbackInfo ci,
        @Local class_1297 camera,
        @Share("camera") LocalRef<class_1297> cameraRef,
        @Share("target_display") LocalRef<BraidDisplayBinding.DisplayHitResult> targetDisplay
    ) {
        cameraRef.set(camera);
        targetDisplay.set(
            BraidDisplayBinding.queryTargetDisplay(camera.method_5836(tickDelta), camera.method_5828(tickDelta))
        );
    }

    @Inject(method = "pick", at = @At(value = "TAIL"))
    public void checkDisplayHitTest(
        float tickDelta,
        CallbackInfo ci,
        @Share("camera") LocalRef<class_1297> cameraRef,
        @Share("target_display") LocalRef<BraidDisplayBinding.DisplayHitResult> targetDisplay
    ) {
        if (targetDisplay.get() == null) {
            this.setTargetDisplay(null);
            return;
        }

        var displayHitPoint = targetDisplay.get().display().quad.unproject(targetDisplay.get().point());

        var cameraPos = cameraRef.get().method_5836(tickDelta);
        if (this.minecraft.field_1765.method_17784().method_1025(cameraPos) > displayHitPoint.method_1025(cameraPos)) {
            this.setTargetDisplay(targetDisplay.get());
            BraidDisplayBinding.onDisplayHit(BraidDisplayBinding.targetDisplay);

            var display = BraidDisplayBinding.targetDisplay.display();

            if (display.primaryPressed && !class_310.method_1551().field_1690.field_1904.method_1434()) {
                display.app.eventBinding.add(new MouseButtonReleaseEvent(GLFW.GLFW_MOUSE_BUTTON_LEFT, KeyModifiers.NONE));
                display.primaryPressed = false;
            }

            if (display.secondaryPressed && !class_310.method_1551().field_1690.field_1886.method_1434()) {
                display.app.eventBinding.add(new MouseButtonReleaseEvent(GLFW.GLFW_MOUSE_BUTTON_RIGHT, KeyModifiers.NONE));
                display.secondaryPressed = false;
            }

            this.minecraft.field_1765 = class_3965.method_17778(
                this.minecraft.field_1765.method_17784(),
                class_2350.field_11036,
                class_2338.method_49638(this.minecraft.field_1765.method_17784())
            );

            this.minecraft.field_1692 = null;
        } else {
            this.setTargetDisplay(null);
        }
    }

    @Unique
    private void setTargetDisplay(@Nullable BraidDisplayBinding.DisplayHitResult newTarget) {
        if (BraidDisplayBinding.targetDisplay == null) {
            BraidDisplayBinding.targetDisplay = newTarget;
            return;
        }

        if (newTarget == null || BraidDisplayBinding.targetDisplay.display() != newTarget.display()) {
            BraidDisplayBinding.targetDisplay.display().app.eventBinding.add(new MouseMoveEvent(0, 0));
        }

        BraidDisplayBinding.targetDisplay = newTarget;
    }
}
