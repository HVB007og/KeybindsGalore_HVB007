package io.wispforest.owo.mixin.ui.access;

import net.minecraft.class_339;
import net.minecraft.class_9110;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_339.class)
public interface AbstractWidgetAccessor {

    @Accessor("height")
    void owo$setHeight(int height);

    @Accessor("width")
    void owo$setWidth(int width);

    @Accessor("x")
    void owo$setX(int x);

    @Accessor("y")
    void owo$setY(int y);

    @Accessor("tooltip")
    class_9110 owo$getTooltip();
}
