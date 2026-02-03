package io.wispforest.owo.registration.reflect;

import net.minecraft.class_2378;
import net.minecraft.class_2591;
import net.minecraft.class_7923;

public interface BlockEntityRegistryContainer extends AutoRegistryContainer<class_2591<?>> {

    @Override
    default class_2378<class_2591<?>> getRegistry() {
        return class_7923.field_41181;
    }

    @Override
    default Class<class_2591<?>> getTargetFieldType() {
        return AutoRegistryContainer.conform(class_2591.class);
    }
}
