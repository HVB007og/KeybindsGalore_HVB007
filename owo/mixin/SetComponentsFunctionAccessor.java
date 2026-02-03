package io.wispforest.owo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import net.minecraft.class_5341;
import net.minecraft.class_9320;
import net.minecraft.class_9326;

@Mixin(class_9320.class)
public interface SetComponentsFunctionAccessor {
    @Invoker("<init>")
    static class_9320 createSetComponentsLootFunction(List<class_5341> list, class_9326 componentChanges) {
        throw new UnsupportedOperationException();
    }
}
