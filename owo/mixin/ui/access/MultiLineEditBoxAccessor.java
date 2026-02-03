package io.wispforest.owo.mixin.ui.access;

import net.minecraft.class_7529;
import net.minecraft.class_7530;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_7529.class)
public interface MultiLineEditBoxAccessor {

    @Accessor("textField")
    class_7530 owo$getTextField();

}
