package io.wispforest.owo.mixin.extension.recipe;

import net.minecraft.class_1863;
import net.minecraft.class_7654;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_1863.class)
public interface RecipeManagerAccessor {

    @Accessor("RECIPE_LISTER")
    static class_7654 owo$getFinder() {
        throw new UnsupportedOperationException();
    }
}
