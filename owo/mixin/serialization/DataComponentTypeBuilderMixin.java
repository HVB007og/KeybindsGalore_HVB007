package io.wispforest.owo.mixin.serialization;

import io.wispforest.owo.serialization.OwoDataComponentTypeBuilder;
import net.minecraft.class_9331;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(class_9331.class_9332.class)
public abstract class DataComponentTypeBuilderMixin<T> implements OwoDataComponentTypeBuilder<T> {
}
