package io.wispforest.owo.mixin.braid;

import net.minecraft.class_12247;
import net.minecraft.class_1921;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.class_1921.class)
public interface RenderTypeInvoker {
    @Invoker("create")
    static class_1921 owo$of(String name, class_12247 renderSetup) {throw new UnsupportedOperationException();}
}
