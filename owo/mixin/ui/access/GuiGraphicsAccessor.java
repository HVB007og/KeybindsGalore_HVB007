package io.wispforest.owo.mixin.ui.access;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5684;
import net.minecraft.class_8000;

@Mixin(class_332.class)
public interface GuiGraphicsAccessor {

    @Invoker("renderTooltip")
    void owo$drawTooltipImmediately(class_327 textRenderer, List<class_5684> components, int x, int y, class_8000 positioner, @Nullable class_2960 texture);

    @Accessor("pose")
    Matrix3x2fStack owo$getPose();

    @Mutable
    @Accessor("pose")
    void owo$setPose(Matrix3x2fStack matrices);

    @Accessor("scissorStack")
    class_332.class_8214 owo$getScissorStack();

    @Mutable
    @Accessor("scissorStack")
    void owo$setScissorStack(class_332.class_8214 scissorStack);

    @Accessor("deferredTooltip")
    void owo$setDeferredTooltip(Runnable drawer);

    @Accessor("deferredTooltip")
    Runnable owo$getDeferredTooltip();

    @Accessor("mouseX")
    int owo$getMouseX();

    @Accessor("mouseY")
    int owo$getMouseY();
}
