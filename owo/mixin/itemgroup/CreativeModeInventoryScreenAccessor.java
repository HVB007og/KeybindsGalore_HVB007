package io.wispforest.owo.mixin.itemgroup;

import net.minecraft.class_1761;
import net.minecraft.class_481;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_481.class)
public interface CreativeModeInventoryScreenAccessor {

    @Accessor("selectedTab")
    static class_1761 owo$getSelectedTab() {
        throw new IllegalStateException("Mixin stub must not be called");
    }

}
