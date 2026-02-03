package io.wispforest.owo.mixin.ui.access;

import net.minecraft.class_4286;
import net.minecraft.class_7940;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_4286.class)
public interface CheckboxAccessor {
    @Accessor("selected")
    void owo$setSelected(boolean checked);

    @Accessor("textWidget")
    class_7940 owo$getTextWidget();
}
