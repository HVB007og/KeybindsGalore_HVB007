package io.wispforest.owo.mixin;

import io.wispforest.owo.util.OwoFreezer;
import net.minecraft.class_310;
import net.minecraft.class_542;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_310.class, priority = 0)
public class MinecraftMixin {

    @SuppressWarnings({"MixinAnnotationTarget"})
    @Group(name = "clientFreezeHooks", min = 1, max = 1)
    @Inject(method = "<init>", at = @At(value = "INVOKE", remap = false,
            target = "Lnet/fabricmc/loader/impl/game/minecraft/Hooks;startClient(Ljava/io/File;Ljava/lang/Object;)V", shift = At.Shift.AFTER))
    private void afterFabricHook(class_542 args, CallbackInfo ci) {
        OwoFreezer.freeze();
    }

    @SuppressWarnings({"MixinAnnotationTarget"})
    @Group(name = "clientFreezeHooks", min = 1, max = 1)
    @Inject(method = "<init>", at = @At(value = "INVOKE", remap = false,
            target = "Lorg/quiltmc/loader/impl/game/minecraft/Hooks;startClient(Ljava/io/File;Ljava/lang/Object;)V", shift = At.Shift.AFTER))
    private void afterQuiltHook(class_542 args, CallbackInfo ci) {
        OwoFreezer.freeze();
    }

}

