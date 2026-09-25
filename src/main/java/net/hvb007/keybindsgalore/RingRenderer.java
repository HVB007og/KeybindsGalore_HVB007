package net.hvb007.keybindsgalore;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.hvb007.keybindsgalore.mixin.GuiGraphicsExtractorAccessor;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

public class RingRenderer {

    public static void drawRing(GuiGraphicsExtractor context, int cx, int cy,
                                  double startDeg, double endDeg, int segments,
                                  double innerRadius, double outerRadius,
                                  int innerColor, int outerColor) {
        GuiRenderState state = ((GuiGraphicsExtractorAccessor) context).kbg$getGuiRenderState();
        state.addGuiElement(new RingElement(ElementType.RING, context.pose(), cx, cy,
            startDeg, endDeg, segments, innerRadius, outerRadius, innerColor, outerColor));
    }

    public static void drawCircle(GuiGraphicsExtractor context, int cx, int cy,
                                    double startDeg, double endDeg, int segments,
                                    double radius, int color) {
        drawRing(context, cx, cy, startDeg, endDeg, segments, 0, radius, color, color);
    }

    private enum ElementType { RING }

    private static class RingElement implements GuiElementRenderState {
        private final Matrix3x2fc pose;
        private final int cx, cy;
        private final double startDeg, endDeg;
        private final int segments;
        private final double innerRadius, outerRadius;
        private final int innerColor, outerColor;
        private final ScreenRectangle bounds;

        RingElement(ElementType type, Matrix3x2fc pose, int cx, int cy,
                     double startDeg, double endDeg, int segments,
                     double innerRadius, double outerRadius,
                     int innerColor, int outerColor) {
            this.pose = new Matrix3x2f(pose);
            this.cx = cx;
            this.cy = cy;
            this.startDeg = startDeg;
            this.endDeg = endDeg;
            this.segments = Math.max(4, segments);
            this.innerRadius = innerRadius;
            this.outerRadius = outerRadius;
            this.innerColor = innerColor;
            this.outerColor = outerColor;

            int r = (int) Math.ceil(outerRadius);
            this.bounds = new ScreenRectangle(cx - r, cy - r, r * 2 + 1, r * 2 + 1);
        }

        @Override
        public void buildVertices(VertexConsumer consumer) {
            double startRad = Math.toRadians(startDeg);
            double endRad = Math.toRadians(endDeg);
            double deltaAngle = (endRad - startRad) / segments;

            for (int i = 0; i < segments; i++) {
                double a1 = startRad + i * deltaAngle;
                double a2 = startRad + (i + 1) * deltaAngle;
                double cos1 = Math.cos(a1), sin1 = Math.sin(a1);
                double cos2 = Math.cos(a2), sin2 = Math.sin(a2);

                consumer.addVertexWith2DPose(pose,
                    (float)(cx + cos1 * outerRadius),
                    (float)(cy + sin1 * outerRadius))
                    .setColor(outerColor);

                consumer.addVertexWith2DPose(pose,
                    (float)(cx + cos1 * innerRadius),
                    (float)(cy + sin1 * innerRadius))
                    .setColor(innerColor);

                consumer.addVertexWith2DPose(pose,
                    (float)(cx + cos2 * innerRadius),
                    (float)(cy + sin2 * innerRadius))
                    .setColor(innerColor);

                consumer.addVertexWith2DPose(pose,
                    (float)(cx + cos2 * outerRadius),
                    (float)(cy + sin2 * outerRadius))
                    .setColor(outerColor);
            }
        }

        @Override
        public RenderPipeline pipeline() { return RenderPipelines.GUI; }

        @Override
        public TextureSetup textureSetup() { return TextureSetup.noTexture(); }

        @Override
        public ScreenRectangle scissorArea() { return null; }

        @Override
        public ScreenRectangle bounds() { return bounds; }
    }
}
