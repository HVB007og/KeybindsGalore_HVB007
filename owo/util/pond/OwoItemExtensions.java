package io.wispforest.owo.util.pond;

import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.json.OwoItemGroupLoader;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import net.minecraft.class_1761;
import net.minecraft.class_1792;

public interface OwoItemExtensions {

    /**
     * @return The 0-indexed tab id this item resides in, {@code -1} if none is defined
     */
    int owo$tab();

    /**
     * @return The function used for adding stacks of
     * this item to an {@link OwoItemGroup} it resides in
     */
    BiConsumer<class_1792, class_1761.class_7704> owo$stackGenerator();

    /**
     * Sets the group of this item, used by {@link OwoItemGroupLoader} to ensure
     * all {@code ItemGroup} references in items are correct for data-driven owo groups
     *
     * @param group The group to replace the current on with
     */
    void owo$setGroup(class_1761 group);

    /**
     * @return The item group this item should reside in
     */
    @Nullable class_1761 owo$group();

    /**
     * @return {@code true} if this item should automatically
     * have its usage stat incremented
     */
    boolean owo$shouldTrackUsageStat();
}
