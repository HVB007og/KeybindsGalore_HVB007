package io.wispforest.owo.braid.display;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.wispforest.owo.Owo;
import io.wispforest.owo.braid.core.AppState;
import io.wispforest.owo.braid.core.EventBinding;
import io.wispforest.owo.braid.core.TextureSurface;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.mixin.braid.RenderTypeInvoker;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;
import net.minecraft.class_10799;
import net.minecraft.class_11659;
import net.minecraft.class_12247;
import net.minecraft.class_1921;
import net.minecraft.class_310;
import net.minecraft.class_4587;

public class BraidDisplay {

    public DisplayQuad quad;

    public final AppState app;
    public final TextureSurface surface;

    @ApiStatus.Internal
    public boolean primaryPressed = false;
    @ApiStatus.Internal
    public boolean secondaryPressed = false;

    boolean renderAutomatically = false;

    public BraidDisplay(DisplayQuad quad, int surfaceWidth, int surfaceHeight, Widget widget) {
        this.quad = quad;
        this.surface = new TextureSurface(surfaceWidth, surfaceHeight);
        this.app = new AppState(
            null,
            AppState.formatName("BraidDisplay", widget),
            class_310.method_1551(),
            this.surface,
            new EventBinding.Headless(),
            widget
        );
    }

    public BraidDisplay renderAutomatically() {
        this.renderAutomatically = true;
        return this;
    }

    public void updateAndDrawApp() {
        var client = this.app.client();

        this.app.processEvents(
            client.method_61966().method_60636()
        );

        this.app.draw(this.surface.guiRenderer.newGraphics(this.app.cursorPosition().x(), this.app.cursorPosition().y()));
    }

    public void render(class_4587 matrices, class_11659 queue, int light) {
        var layer = RENDER_TYPE.apply(this.surface);
        queue.method_73483(matrices, layer, (matricesEntry, buffer) -> {
            var normal = this.quad.normal.method_46409();
            buffer.method_56824(matricesEntry, 0, 0, 0).method_22915(1f, 1f, 1f, 1f).method_22913(0, 1).method_60803(light).method_61959(matricesEntry, normal);
            buffer.method_61032(matricesEntry, this.quad.left.method_46409()).method_22915(1f, 1f, 1f, 1f).method_22913(0, 0).method_60803(light).method_61959(matricesEntry, normal);
            buffer.method_61032(matricesEntry, this.quad.top.method_1019(this.quad.left).method_46409()).method_22915(1f, 1f, 1f, 1f).method_22913(1, 0).method_60803(light).method_61959(matricesEntry, normal);
            buffer.method_61032(matricesEntry, this.quad.top.method_46409()).method_22915(1f, 1f, 1f, 1f).method_22913(1, 1).method_60803(light).method_61959(matricesEntry, normal);
        });
    }

    // ---

    public static final RenderPipeline PIPELINE = RenderPipeline.builder(class_10799.field_64223)
        .withLocation(Owo.id("pipeline/braid_display"))
        .withShaderDefine("ALPHA_CUTOUT", 0.1F)
        .withCull(false)
        .withBlend(BlendFunction.TRANSLUCENT)
        .build();

    private static final Function<TextureSurface, class_1921> RENDER_TYPE = surface -> RenderTypeInvoker.owo$of(
        Owo.id("braid_display").toString(),
        class_12247.method_75927(PIPELINE)
            .method_75934("Sampler0", surface.registeredTextureId)
            .method_75928()
            .method_75938()
    );
}
