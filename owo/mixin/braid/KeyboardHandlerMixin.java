package io.wispforest.owo.mixin.braid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.owo.braid.core.events.CharInputEvent;
import io.wispforest.owo.braid.util.layers.BraidLayersBinding;
import net.minecraft.class_11905;
import net.minecraft.class_309;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(class_309.class)
public class KeyboardHandlerMixin {

    @WrapOperation(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;charTyped(Lnet/minecraft/client/input/CharacterEvent;)Z"))
    private boolean captureScreenCharTyped(class_437 screen, class_11905 charInput, Operation<Boolean> original) {
        return BraidLayersBinding.tryHandleEvent(screen, new CharInputEvent((char) charInput.comp_4793(), charInput.comp_4794()))
            || original.call(screen, charInput);
    }
}