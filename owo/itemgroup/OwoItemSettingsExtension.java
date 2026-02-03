package io.wispforest.owo.itemgroup;

import java.util.function.BiConsumer;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1761;
import net.minecraft.class_1792;
import net.minecraft.class_1937;

public interface OwoItemSettingsExtension {

    default class_1792.class_1793 group(ItemGroupReference ref) {
        throw new IllegalStateException("Implemented in mixin.");
    }

    /**
     * @param group The item group this item should appear in
     */
    default class_1792.class_1793 group(OwoItemGroup group) {
        throw new IllegalStateException("Implemented in mixin.");
    }

    default OwoItemGroup group() {
        throw new IllegalStateException("Implemented in mixin.");
    }

    default class_1792.class_1793 tab(int tab) {
        throw new IllegalStateException("Implemented in mixin.");
    }

    default int tab() {
        throw new IllegalStateException("Implemented in mixin.");
    }

    /**
     * @param generator The function this item uses for creating stacks in the
     *                  {@link OwoItemGroup} it is in, by default this will be {@link OwoItemGroup#DEFAULT_STACK_GENERATOR}
     */
    default class_1792.class_1793 stackGenerator(BiConsumer<class_1792, class_1761.class_7704> generator) {
        throw new IllegalStateException("Implemented in mixin.");
    }

    default BiConsumer<class_1792, class_1761.class_7704> stackGenerator() {
        throw new IllegalStateException("Implemented in mixin.");
    }

    /**
     * Automatically increment {@link net.minecraft.class_3468#field_15372}
     * for this item every time {@link class_1792#method_7836(class_1937, class_1657, class_1268)}
     * returns an accepted result
     */
    default class_1792.class_1793 trackUsageStat() {
        throw new IllegalStateException("Implemented in mixin.");
    }

    default boolean shouldTrackUsageStat() {
        throw new IllegalStateException("Implemented in mixin.");
    }
}
