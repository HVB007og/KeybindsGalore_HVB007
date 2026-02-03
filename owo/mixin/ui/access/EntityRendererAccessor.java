package io.wispforest.owo.mixin.ui.access;

import net.minecraft.class_1297;
import net.minecraft.class_2561;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_897.class)
public interface EntityRendererAccessor<T extends class_1297> {
    @Invoker("getNameTag")
    class_2561 owo$getNameTag(T entity);
}
