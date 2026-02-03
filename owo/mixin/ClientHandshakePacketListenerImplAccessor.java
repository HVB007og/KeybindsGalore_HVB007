package io.wispforest.owo.mixin;

import net.minecraft.class_2535;
import net.minecraft.class_635;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_635.class)
public interface ClientHandshakePacketListenerImplAccessor {

    @Accessor("connection")
    class_2535 owo$getConnection();

}
