package io.wispforest.owo.mixin.registry;

import net.minecraft.class_5321;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_6880.class_6883.class)
public interface ReferenceAccessor<T> {
    @Invoker("bindKey")
    void owo$setRegistryKey(class_5321<T> registryKey);

    @Invoker("bindValue")
    void owo$setValue(T value);
}
