package io.wispforest.owo.mixin.ui.access;

import net.minecraft.class_4185;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_4185.class)
public interface ButtonAccessor {

    @Mutable
    @Accessor("onPress")
    void owo$setOnPress(class_4185.class_4241 onPress);

}
