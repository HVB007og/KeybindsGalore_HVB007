package io.wispforest.owo.mixin.braid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;
import net.minecraft.class_12225;
import net.minecraft.class_2583;

@Mixin(class_12225.class_12226.class)
public interface ClickableStyleFinderAccessor {
    @Mutable
    @Accessor("styleScanner")
    void owo$setStyleScanner(Consumer<class_2583> setStyleCallback);

    @Accessor("result")
    void owo$setResult(class_2583 style);
}
