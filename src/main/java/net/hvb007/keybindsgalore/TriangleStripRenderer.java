package net.hvb007.keybindsgalore;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

/**
 * 1.21.5 GPU-batched sector renderer using custom RenderPipeline + RenderType.
 *
 * Draws pie sectors as interleaved outer/inner vertex pairs forming a triangle strip,
 * submitted to the shared GuiGraphics BufferSource via getBuffer(GUI_SECTOR_LAYER).
 * The BufferSource is exposed through the GuiGraphicsAccessor mixin.
 *
 * Rendering pipeline:
 *   KeybindsGalore.onInitializeClient()
 *     -> registers GUI_TRIANGLE_STRIP pipeline (from RenderPipelines.GUI_SNIPPET base)
 *     -> wraps it in GUI_SECTOR_LAYER RenderType via RenderType.create()
 *   KeybindCircularScreen.render()
 *     -> TriangleStripRenderer.drawSector() emits vertices to BufferSource
 *     -> GuiGraphics' normal end-frame lifecycle flushes the buffer ~1 draw call
 *
 * This replaces the old scanline approach (KBRenderer) which issued ~1500 fill() calls
 * per frame, and the dead Tesselator/BufferUploader HW path (removed in 1.21.5).
 */
public class TriangleStripRenderer {

    /**
     * Draws a single pie sector as a triangle strip.
     *
     * The strip alternates outer-edge and inner-edge vertices along the arc,
     * producing filled quads between consecutive angle steps. If the arc exceeds
     * ~180 degrees it is split in half to avoid degenerate geometry.
     */
    public static void drawSector(GuiGraphics drawContext, int centerX, int centerY, float startAngleRad, float endAngleRad, float innerRadius, float outerRadius, int color) {
        if (innerRadius >= outerRadius) return;

        float angleDiff = endAngleRad - startAngleRad;
        if (angleDiff > (float) Math.PI - 0.01f) {
            float midAngle = startAngleRad + angleDiff / 2.0f;
            drawSector(drawContext, centerX, centerY, startAngleRad, midAngle, innerRadius, outerRadius, color);
            drawSector(drawContext, centerX, centerY, midAngle, endAngleRad, innerRadius, outerRadius, color);
            return;
        }

        int a = (color >> 24) & 255;
        int r = (color >> 16) & 255;
        int g = (color >> 8) & 255;
        int b = color & 255;

        int segments = Math.max(4, Configurations.CIRCLE_VERTICES);
        float angleStep = angleDiff / segments;

        // Access GuiGraphics' shared BufferSource via mixin accessor, then grab
        // a VertexConsumer for our custom RenderType. The buffer is flushed
        // automatically by GuiGraphics' normal end-frame lifecycle.
        var bufferSource = ((net.hvb007.keybindsgalore.mixin.GuiGraphicsAccessor) drawContext).kbg$getBufferSource();
        var consumer = bufferSource.getBuffer(KeybindsGalore.GUI_SECTOR_LAYER);

        // Emit interleaved outer/inner vertices along the arc arc forming a
        // continuous triangle strip. Each pair (outer_i, inner_i, outer_i+1, inner_i+1)
        // produces two filled triangles covering one radial segment.
        for (int i = 0; i <= segments; i++) {
            float angle = startAngleRad + i * angleStep;
            float cos = Mth.cos(angle);
            float sin = Mth.sin(angle);

            consumer.addVertex(centerX + cos * outerRadius, centerY + sin * outerRadius, 0.0F).setColor(r, g, b, a);
            consumer.addVertex(centerX + cos * innerRadius, centerY + sin * innerRadius, 0.0F).setColor(r, g, b, a);
        }
    }
}
