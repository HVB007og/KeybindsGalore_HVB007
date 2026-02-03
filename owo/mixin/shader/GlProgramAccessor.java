package io.wispforest.owo.mixin.shader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.class_284;
import net.minecraft.class_5944;

@Mixin(class_5944.class)
public interface GlProgramAccessor {

    @Accessor("uniformsByName")
    Map<String, class_284> owo$getUniformsByName();

}
