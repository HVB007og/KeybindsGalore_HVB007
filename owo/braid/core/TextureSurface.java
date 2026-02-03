package io.wispforest.owo.braid.core;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import io.wispforest.owo.Owo;
import io.wispforest.owo.braid.core.cursor.CursorStyle;
import io.wispforest.owo.braid.util.BraidGuiRenderer;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import java.util.UUID;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_6367;

public class TextureSurface implements Surface {

    private final class_6367 target;
    private final EventStream<ResizeCallback> resizeEvents = ResizeCallback.newStream();

    public final TextureSurfaceTexture registeredTexture;
    public final class_2960 registeredTextureId;

    private CursorStyle currentCursorStyle = CursorStyle.NONE;

    public final BraidGuiRenderer guiRenderer;

    public TextureSurface(int width, int height) {
        this.target = new class_6367("texture surface", width, height, true);
        this.guiRenderer = new BraidGuiRenderer(class_310.method_1551());

        this.registeredTexture = new TextureSurfaceTexture();
        this.registeredTextureId = Owo.id("texture_surface_" + UUID.randomUUID());

        class_310.method_1551().method_1531().method_4616(this.registeredTextureId, this.registeredTexture);
    }

    public void resize(int width, int height) {
        this.target.method_1234(width, height);
        this.resizeEvents.sink().onResize(width, height);

        this.registeredTexture.sync();
    }

    public GpuTextureView texture() {
        return this.target.method_71639();
    }

    @Override
    public int width() {
        return this.target.field_1482;
    }

    @Override
    public int height() {
        return this.target.field_1481;
    }

    @Override
    public double scaleFactor() {
        return 1;
    }

    @Override
    public EventSource<ResizeCallback> onResize() {
        return this.resizeEvents.source();
    }

    @Override
    public CursorStyle currentCursorStyle() {
        return this.currentCursorStyle;
    }

    @Override
    public void setCursorStyle(CursorStyle style) {
        this.currentCursorStyle = style;
    }

    // ---

    @Override
    public void beginRendering() {
        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(
            this.target.method_30277(),
            0x00000000,
            this.target.method_30278(),
            1
        );
    }

    @Override
    public void endRendering() {
        this.guiRenderer.render(new BraidGuiRenderer.Target(
            this.target,
            this
        ));
    }

    @Override
    public void dispose() {
        this.target.method_1238();
        class_310.method_1551().method_1531().method_4615(this.registeredTextureId);
    }

    // ---

    public class TextureSurfaceTexture extends class_1044 {

        public TextureSurfaceTexture() {
            this.sync();
            this.field_63613 = RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST);
        }

        private void sync() {
             this.field_56974 = TextureSurface.this.target.method_30277();
             this.field_60597 = TextureSurface.this.target.method_71639();
        }

        @Override
        public void close() {}
    }
}
