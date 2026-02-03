package io.wispforest.owo.mixin.itemgroup;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import net.minecraft.class_1761;
import net.minecraft.class_1799;
import net.minecraft.class_2561;

@Mixin(class_1761.class)
public interface CreativeModeTabAccessor {

    @Accessor("displayItemsGenerator")
    class_1761.class_7914 owo$getDisplayItemsGenerator();

    @Mutable
    @Accessor("displayItemsGenerator")
    void owo$setDisplayItemsGenerator(class_1761.class_7914 collector);

    @Accessor("displayItemsSearchTab")
    void owo$setDisplayItemsSearchTab(Set<class_1799> searchTabStacks);

    @Mutable
    @Accessor("displayName")
    void owo$setDisplayName(class_2561 displayName);

    @Mutable
    @Accessor("column")
    void owo$setColumn(int column);

    @Mutable
    @Accessor("row")
    void owo$setRow(class_1761.class_7915 row);
}
