package io.wispforest.owo.mixin.ui.access;

import net.minecraft.class_2586;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_2586.class)
public interface BlockEntityAccessor {
    @Accessor("blockState")
    void owo$setBlockState(class_2680 state);
}
