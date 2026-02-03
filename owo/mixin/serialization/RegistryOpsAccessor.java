package io.wispforest.owo.mixin.serialization;

import net.minecraft.class_6903;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_6903.class)
public interface RegistryOpsAccessor {
    @Accessor("lookupProvider")
    class_6903.class_7863 owo$infoGetter();
}
