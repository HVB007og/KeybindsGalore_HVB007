package io.wispforest.owo.mixin;

import io.wispforest.owo.util.pond.OwoAbstractContainerMenuExtension;
import net.minecraft.class_1703;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_3222.class)
public class ServerPlayerMixin {

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "initMenu", at = @At("HEAD"))
    private void attachScreenHandler(class_1703 screenHandler, CallbackInfo ci) {
        ((OwoAbstractContainerMenuExtension) screenHandler).owo$attachToPlayer((class_3222) (Object) this);
    }
}
