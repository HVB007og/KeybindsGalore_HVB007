package io.wispforest.owo.mixin.ui;

import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.component.SliderComponent;
import io.wispforest.owo.ui.core.CursorStyle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_339;
import net.minecraft.class_357;

@SuppressWarnings("ConstantConditions")
@Mixin(class_357.class)
public abstract class AbstractSliderButtonMixin extends class_339 {
    @Shadow
    protected abstract void setValue(double value);

    @Shadow protected double value;

    public AbstractSliderButtonMixin(int x, int y, int width, int height, class_2561 message) {
        super(x, y, width, height, message);
    }

    @ModifyArg(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractSliderButton;setValue(D)V"))
    private double injectCustomStep(double value) {
        if (!((Object) this instanceof SliderComponent slider)) return value;
        return this.value + Math.signum(value - this.value) * slider.scrollStep();
    }

    @Inject(method = "setValueFromMouse", at = @At("HEAD"), cancellable = true)
    private void makeItSnappyTeam(class_11909 click, CallbackInfo ci) {
        if (!((Object) this instanceof DiscreteSliderComponent discrete)) return;
        if (!discrete.snap()) return;

        ci.cancel();

        double value = (click.comp_4798() - (this.method_46426() + 4d)) / (this.field_22758 - 8d);
        double min = discrete.min(), max = discrete.max();
        int decimalPlaces = discrete.decimalPlaces();

        this.setValue(
                (new BigDecimal(min + value * (max - min)).setScale(decimalPlaces, RoundingMode.HALF_UP).doubleValue() - min) / (max - min)
        );
    }

    protected CursorStyle owo$preferredCursorStyle() {
        return CursorStyle.MOVE;
    }
}