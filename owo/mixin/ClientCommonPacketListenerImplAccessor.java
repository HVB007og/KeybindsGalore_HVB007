package io.wispforest.owo.mixin;

import net.minecraft.class_2535;
import net.minecraft.class_8673;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_8673.class)
public interface ClientCommonPacketListenerImplAccessor {
    @Accessor
    class_2535 getConnection();
}
