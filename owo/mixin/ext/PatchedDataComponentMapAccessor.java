package io.wispforest.owo.mixin.ext;

import net.minecraft.class_9323;
import net.minecraft.class_9335;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_9335.class)
public interface PatchedDataComponentMapAccessor {
    @Accessor("prototype")
    class_9323 owo$getPrototype();

    @Accessor("prototype")
    @Mutable
    void owo$setPrototype(class_9323 baseComponents);
}
