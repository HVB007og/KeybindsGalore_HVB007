package io.wispforest.owo.mixin.ui.access;

import net.minecraft.class_10860;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_10860.class)
public interface GlCommandEncoderAccessor {

    @Accessor("inRenderPass")
    void owo$setInRenderPass(boolean open);
}
