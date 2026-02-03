package io.wispforest.owo.mixin.ui.access;

import net.minecraft.class_342;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_342.class)
public interface EditBoxAccessor {
    @Accessor("bordered")
    boolean owo$bordered();

    @Invoker("updateTextPosition")
    void owo$updateTextPosition();
}
