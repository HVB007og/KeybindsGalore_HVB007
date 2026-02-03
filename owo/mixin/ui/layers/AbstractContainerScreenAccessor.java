package io.wispforest.owo.mixin.ui.layers;

import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_465.class)
public interface AbstractContainerScreenAccessor {

    @Accessor("leftPos")
    int owo$getRootX();

    @Accessor("topPos")
    int owo$getRootY();

}
