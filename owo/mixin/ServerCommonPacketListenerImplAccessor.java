package io.wispforest.owo.mixin;

import net.minecraft.class_2535;
import net.minecraft.class_8609;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_8609.class)
public interface ServerCommonPacketListenerImplAccessor {

    @Accessor("connection")
    class_2535 owo$getConnection();

}
