package io.wispforest.owo.mixin.ui.display;

import com.llamalad7.mixinextras.sugar.Local;
import io.wispforest.owo.braid.core.events.MouseScrollEvent;
import io.wispforest.owo.braid.display.BraidDisplayBinding;
import net.minecraft.class_310;
import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_312.class)
public class MouseHandlerMixin {

    @Shadow
    @Final
    private class_310 minecraft;

    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getInventory()Lnet/minecraft/world/entity/player/Inventory;"), cancellable = true)
    public void scrollBraidDisplays(long window, double horizontal, double vertical, CallbackInfo ci, @Local(ordinal = 3) double xOffset, @Local(ordinal = 4) double yOffset) {
        if (BraidDisplayBinding.targetDisplay == null || this.minecraft.field_1724.method_5715()) return;

        BraidDisplayBinding.targetDisplay.display().app.eventBinding.add(new MouseScrollEvent(xOffset, yOffset));
        ci.cancel();
    }

}
