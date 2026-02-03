package io.wispforest.owo.mixin.ui;

import io.wispforest.owo.ui.util.CommandOpenedScreen;
import net.minecraft.class_11908;
import net.minecraft.class_310;
import net.minecraft.class_408;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_408.class)
public class ChatScreenMixin {

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"), cancellable = true)
    private void cancelClose(class_11908 input, CallbackInfoReturnable<Boolean> cir) {
        if (class_310.method_1551().field_1755 instanceof CommandOpenedScreen) {
            cir.setReturnValue(true);
        }
    }

}
