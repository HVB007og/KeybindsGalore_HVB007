package io.wispforest.owo.ops;

import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2588;
import net.minecraft.class_327;
import net.minecraft.class_5250;
import net.minecraft.class_5481;

/**
 * A collection of common operations
 * for working with and stylizing {@link class_2561}
 */
public final class TextOps {

    private TextOps() {}

    /**
     * Appends the {@code text} onto the {@code prefix} without
     * modifying the siblings of either one
     *
     * @param prefix The prefix
     * @param text   The text to add onto the prefix
     * @return The combined text
     */
    public static class_5250 concat(class_2561 prefix, class_2561 text) {
        return class_2561.method_43473().method_10852(prefix).method_10852(text);
    }

    /**
     * Creates a new {@link class_2561} with the specified color
     * already applied
     *
     * @param text  The text to create
     * @param color The color to use in {@code RRGGBB} format
     * @return The colored text, specifically a {@link net.minecraft.class_8828}
     */
    public static class_5250 withColor(String text, int color) {
        return class_2561.method_43470(text).method_10862(class_2583.field_24360.method_36139(color));
    }

    /**
     * Creates a new {@link class_2561} with the specified color
     * already applied
     *
     * @param text  The text to create
     * @param color The color to use in {@code RRGGBB} format
     * @return The colored text, specifically a {@link class_2588}
     */
    public static class_5250 translateWithColor(String text, int color) {
        return class_2561.method_43471(text).method_10862(class_2583.field_24360.method_36139(color));
    }

    /**
     * Applies multiple {@link class_124}s to the given String, with
     * each one after the first one beginning on a {@code §} symbol.
     * The amount of {@code §} symbols must equal the amount of
     * supplied formattings - 1
     *
     * @param text       The text to format, with optional format delimiters
     * @param formatting The formattings to apply
     * @return The formatted text
     */
    public static class_5250 withFormatting(String text, class_124... formatting) {
        var textPieces = text.split("§");
        if (formatting.length != textPieces.length) return withColor("unmatched format specifiers - this is a bug", 0xff007f);

        var textBase = class_2561.method_43470(textPieces[0]).method_27692(formatting[0]);

        for (int i = 1; i < textPieces.length; i++) {
            textBase.method_10852(class_2561.method_43470(textPieces[i]).method_27692(formatting[i]));
        }

        return textBase;
    }

    /**
     * Applies multiple colors to the given String, with
     * each one after the first one beginning on a {@code §} symbol.
     * The amount of {@code §} symbols must equal the amount of
     * supplied colors - 1
     *
     * @param text   The text to colorize, with optional color delimiters
     * @param colors The colors to apply, in {@code RRGGBB} format
     * @return The colorized text
     * @see #color(class_124)
     */
    public static class_5250 withColor(String text, int... colors) {
        var textPieces = text.split("§");
        if (colors.length != textPieces.length) return withColor("unmatched color specifiers - this is a bug", 0xff007f);

        var textBase = withColor(textPieces[0], colors[0]);

        for (int i = 1; i < textPieces.length; i++) {
            textBase.method_10852(withColor(textPieces[i], colors[i]));
        }

        return textBase;
    }

    /**
     * Determine the width of the given iterable of texts,
     * which is defined as the width of the widest text
     * in the iterable
     *
     * @param renderer The text renderer responsible for rendering
     *                 the text later on
     * @param texts    The texts to check
     * @return The width of the widest text in the collection
     */
    public static int width(class_327 renderer, Iterable<class_2561> texts) {
        int width = 0;
        for (var text : texts) width = Math.max(width, renderer.method_27525(text));
        return width;
    }

    /**
     * Determine the width of the given iterable of texts,
     * which is defined as the width of the widest text
     * in the iterable
     *
     * @param renderer The text renderer responsible for rendering
     *                 the text later on
     * @param texts    The texts to check
     * @return The width of the widest text in the collection
     */
    public static int widthOrdered(class_327 renderer, Iterable<class_5481> texts) {
        int width = 0;
        for (var text : texts) width = Math.max(width, renderer.method_30880(text));
        return width;
    }

    /**
     * @return The color value associated with the given formatting
     * in {@code RRGGBB} format, or {@code 0} if there is none
     */
    public static int color(class_124 formatting) {
        return formatting.method_532() == null ? 0 : formatting.method_532();
    }

}
