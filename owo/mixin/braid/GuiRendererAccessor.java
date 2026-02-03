package io.wispforest.owo.mixin.braid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.class_11228;
import net.minecraft.class_11239;
import net.minecraft.class_11246;
import net.minecraft.class_11256;

@Mixin(value = class_11228.class, priority = 1100)
public interface GuiRendererAccessor {
    @Accessor("renderState")
    class_11246 owo$getRenderState();

    @Accessor("pictureInPictureRenderers")
    Map<Class<? extends class_11256>, class_11239<?>> owo$getPictureInPictureRenderers();
}
