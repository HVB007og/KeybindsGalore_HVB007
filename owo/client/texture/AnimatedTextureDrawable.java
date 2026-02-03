package io.wispforest.owo.client.texture;

import net.minecraft.class_10799;
import net.minecraft.class_156;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_4068;

/**
 * A drawable that can draw an animated texture, very similar to how
 * .mcmeta works on stitched textures in ticked atlases
 *
 * <p>Originally from Animawid, adapted for oωo</p>
 *
 * @author Tempora
 * @author glisco
 */
public class AnimatedTextureDrawable implements class_4068 {

    private final SpriteSheetMetadata metadata;
    private final class_2960 texture;

    private final int validFrames;
    private final int delay;
    private final boolean loop;
    private final int rows;
    private long startTime = -1L;

    private final int width, height;
    private int x, y;

    /**
     * Creates a new animated texture widget using the width and height of the spritesheet as dimensions
     *
     * @see #AnimatedTextureDrawable(int, int, int, int, class_2960, SpriteSheetMetadata, int, boolean)
     */
    public AnimatedTextureDrawable(int x, int y, class_2960 texture, SpriteSheetMetadata metadata, int delay, boolean loop) {
        this(x, y, metadata.width(), metadata.height(), texture, metadata, delay, loop);
    }

    /**
     * Creates a new animated texture widget that can be placed on your Screen or overlay etc.
     *
     * @param x        The x position of the widget.
     * @param y        The y position of the widget.
     * @param width    The width of the widget.
     * @param height   The height of the widget.
     * @param texture  The identifier of the texture, eg: {@code mymod:texture/animation_spritesheet.png}
     * @param metadata Metadata on the spritesheet.
     * @param delay    The delay, in milliseconds, between each frame.
     */
    public AnimatedTextureDrawable(int x, int y, int width, int height, class_2960 texture, SpriteSheetMetadata metadata, int delay, boolean loop) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.delay = delay;
        this.metadata = metadata;
        this.width = width;
        this.height = height;
        this.loop = loop;

        int columns = metadata.width() / metadata.frameWidth();
        this.rows = metadata.height() / metadata.frameHeight();
        this.validFrames = columns * this.rows;
    }

    /**
     * Renders this drawable at the given position. The position
     * of this drawable is mutated non-temporarily
     */
    public void render(int x, int y, class_332 context, int mouseX, int mouseY, float delta) {
        this.x = x;
        this.y = y;
        this.method_25394(context, mouseX, mouseY, delta);
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        if (startTime == -1L) startTime = class_156.method_658();

        long currentTime = class_156.method_658();
        long frame = Math.min(validFrames - 1, (currentTime - startTime) / delay);

        if (loop && frame == validFrames - 1) {
            startTime = class_156.method_658();
            frame = 0;
        }

        context.method_25290(class_10799.field_56883, this.texture, x, y, (frame / rows) * metadata.frameWidth(), (frame % rows) * metadata.frameHeight(), width, height, metadata.width(), metadata.height());
    }
}
