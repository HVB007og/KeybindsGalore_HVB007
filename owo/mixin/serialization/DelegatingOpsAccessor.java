package io.wispforest.owo.mixin.serialization;

import com.mojang.serialization.DynamicOps;
import net.minecraft.class_5379;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_5379.class)
public interface DelegatingOpsAccessor<T> {
    @Accessor("delegate")
    DynamicOps<T> owo$delegate();
}
