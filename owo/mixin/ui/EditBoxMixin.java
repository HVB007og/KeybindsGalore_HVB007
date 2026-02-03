package io.wispforest.owo.mixin.ui;

import io.wispforest.owo.mixin.ui.access.TextBoxComponentAccessor;
import io.wispforest.owo.ui.inject.GreedyInputUIComponent;
import net.minecraft.class_2561;
import net.minecraft.class_339;
import net.minecraft.class_342;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_342.class)
public abstract class EditBoxMixin extends class_339 implements GreedyInputUIComponent {

    public EditBoxMixin(int x, int y, int width, int height, class_2561 message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "onValueChange", at = @At("HEAD"))
    private void callOwoListener(String newText, CallbackInfo ci) {
        if (!(this instanceof TextBoxComponentAccessor accessor)) return;
        accessor.owo$textValue().set(newText);
    }

    @Override
    public void onFocusGained(FocusSource source) {
        super.onFocusGained(source);
        this.method_25365(true);
    }

}
