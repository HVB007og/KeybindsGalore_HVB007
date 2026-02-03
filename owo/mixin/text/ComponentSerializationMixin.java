package io.wispforest.owo.mixin.text;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.wispforest.owo.text.CustomTextRegistry;
import net.minecraft.class_2561;
import net.minecraft.class_5699;
import net.minecraft.class_7417;
import net.minecraft.class_8824;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_8824.class)
public abstract class ComponentSerializationMixin {

    @Inject(method = "createCodec", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/ComponentSerialization;bootstrap(Lnet/minecraft/util/ExtraCodecs$LateBoundIdMapper;)V", shift = At.Shift.AFTER))
    private static void injectOwoCodecs(Codec<class_2561> selfCodec, CallbackInfoReturnable<Codec<class_2561>> cir, @Local class_5699.class_10388<String, MapCodec<? extends class_7417>> mapper) {
        CustomTextRegistry.inject(mapper);
    }

}

