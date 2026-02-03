package io.wispforest.owo.mixin.braid;

import io.wispforest.owo.braid.util.layers.BraidLayersBinding;
import io.wispforest.owo.util.pond.OwoScreenExtension;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_437.class, priority = 1100)
public abstract class ScreenMixin implements OwoScreenExtension {

    @Unique
    private @Nullable BraidLayersBinding.LayersState braidLayersState;

    @Override
    public void owo$setBraidLayersState(BraidLayersBinding.LayersState state) {
        this.braidLayersState = state;
    }

    @Override
    public @Nullable BraidLayersBinding.LayersState owo$getBraidLayersState() {
        return this.braidLayersState;
    }

    @Inject(method = "renderWithTooltipAndSubtitles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER))
    private void renderLayers(class_332 context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        BraidLayersBinding.renderLayers(((class_437) (Object) this), context, mouseX, mouseY);
    }
}
