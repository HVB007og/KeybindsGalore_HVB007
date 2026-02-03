package io.wispforest.owo.mixin.braid;

import net.minecraft.class_11228;
import net.minecraft.class_757;
import net.minecraft.class_758;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_757.class)
public interface GameRendererAccessor {
    @Accessor("guiRenderer")
    class_11228 owo$getGuiRenderer();

    @Accessor("fogRenderer")
    class_758 owo$getFogRenderer();
}
