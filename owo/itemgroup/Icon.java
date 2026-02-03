package io.wispforest.owo.itemgroup;

import io.wispforest.owo.client.texture.AnimatedTextureDrawable;
import io.wispforest.owo.client.texture.SpriteSheetMetadata;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_2960;
import net.minecraft.class_332;

/**
 * An icon used for rendering on buttons in {@link OwoItemGroup}s
 * <p>
 * Default implementations provided for textures and item stacks
 */
@FunctionalInterface
public interface Icon {

    @Environment(EnvType.CLIENT)
    void render(class_332 graphics, int x, int y, int mouseX, int mouseY, float delta);

    static Icon of(class_1799 stack) {
        return new Icon() {
            @Override
            public void render(class_332 graphics, int x, int y, int mouseX, int mouseY, float delta) {
                graphics.method_51445(stack, x, y);
            }
        };
    }

    static Icon of(class_1935 item) {
        return of(new class_1799(item));
    }

    static Icon of(class_2960 texture, int u, int v, int textureWidth, int textureHeight) {
        return new Icon() {
            @Override
            public void render(class_332 graphics, int x, int y, int mouseX, int mouseY, float delta) {
                graphics.method_25290(class_10799.field_56883, texture, x, y, u, v, 16, 16, textureWidth, textureHeight);
            }
        };
    }

    /**
     * Creates an Animated ItemGroup Icon
     *
     * @param texture     The texture to render, this is the spritesheet
     * @param textureSize The size of the texture, it is assumed to be square
     * @param frameDelay  The delay in milliseconds between frames.
     * @param loop        Should the animation play once or loop?
     * @return The created icon instance
     */
    static Icon of(class_2960 texture, int textureSize, int frameDelay, boolean loop) {
        var widget = new AnimatedTextureDrawable(0, 0, 16, 16, texture, new SpriteSheetMetadata(textureSize, 16), frameDelay, loop);
        return new Icon() {
            @Override
            public void render(class_332 graphics, int x, int y, int mouseX, int mouseY, float delta) {
                widget.render(x, y, graphics, mouseX, mouseY, delta);
            }
        };
    }
}
