package io.wispforest.owo.mixin.ui;

import net.minecraft.class_1735;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_1735.class)
public interface SlotAccessor {
    @Mutable
    @Accessor("x")
    void owo$setX(int x);

    @Mutable
    @Accessor("y")
    void owo$setY(int y);
}
