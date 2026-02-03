package io.wispforest.owo.mixin.serialization;

import net.minecraft.class_6903;
import net.minecraft.class_7225;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_6903.class_9683.class)
public interface CachedRegistryInfoGetterAccessor {
    @Accessor("lookupProvider") class_7225.class_7874 owo$getRegistriesLookup();
}
