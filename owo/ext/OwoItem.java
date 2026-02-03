package io.wispforest.owo.ext;

import net.minecraft.class_9323;
import net.minecraft.class_9326;
import org.jetbrains.annotations.ApiStatus;

public interface OwoItem {
    /**
     * Generates component-derived-components from the stack's components
     * @param source a map containing the item stack's non-derived components
     * @param target a builder for the derived component map
     */
    @ApiStatus.Experimental
    default void deriveStackComponents(class_9323 source, class_9326.class_9327 target) { }
}
