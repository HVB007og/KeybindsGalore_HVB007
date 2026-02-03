package net.hvb007.keybindsgalore;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.Color;

/**
 * This class provides utility methods for rendering triangles and other shapes
 * using a software-based approach for GUI rendering.
 */
public class TriangleStripRenderer {

    /**
     * Fills a triangle using horizontal scanlines.
     * This is a software-based approach and may have aliasing.
     *
     * @param drawContext The DrawContext instance
     * @param x1 X coordinate of the first vertex
     * @param y1 Y coordinate of the first vertex
     * @param x2 X coordinate of the second vertex
     * @param y2 Y coordinate of the second vertex
     * @param x3 X coordinate of the third vertex
     * @param y3 Y coordinate of the third vertex
     * @param color The ARGB color value
     */
    public static void fillTriangle(GuiGraphics drawContext, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        // Sort vertices by y coordinate
        if (y1 > y2) {
            int tx = x1; x1 = x2; x2 = tx;
            int ty = y1; y1 = y2; y2 = ty;
        }
        if (y1 > y3) {
            int tx = x1; x1 = x3; x3 = tx;
            int ty = y1; y1 = y3; y3 = ty;
        }
        if (y2 > y3) {
            int tx = x2; x2 = x3; x3 = tx;
            int ty = y2; y2 = y3; y3 = ty;
        }

        // Draw horizontal lines to fill triangle
        for (int y = y1; y <= y3; y++) {
            float xLeft, xRight;

            if (y <= y2) {
                xLeft = x1 + (float)(x2 - x1) * (y - y1) / (y2 - y1 + 1);
                xRight = x1 + (float)(x3 - x1) * (y - y1) / (y3 - y1 + 1);
            } else {
                xLeft = x2 + (float)(x3 - x2) * (y - y2) / (y3 - y2 + 1);
                xRight = x1 + (float)(x3 - x1) * (y - y1) / (y3 - y1 + 1);
            }

            if (xLeft > xRight) {
                float temp = xLeft;
                xLeft = xRight;
                xRight = temp;
            }

            drawContext.hLine((int)xLeft, (int)xRight, y, color);
        }
    }

    public static void drawTriangle(GuiGraphics drawContext, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        fillTriangle(drawContext, x1, y1, x2, y2, x3, y3, color);
    }

    public static void drawSector(GuiGraphics drawContext, int centerX, int centerY, float startAngleRad, float endAngleRad, float innerRadius, float outerRadius, int color) {
        if (Configurations.USE_SOFTWARE_RENDERING) {
            // Check if angle is too large (>= PI) and split if necessary to avoid collinear vertices
            // Using PI (180 degrees) - epsilon ensures we only split when absolutely necessary (e.g. 2 sectors)
            // This preserves the N-gon shape of the inner hole for N > 2.
            float angleDiff = endAngleRad - startAngleRad;
            if (angleDiff > (float)Math.PI - 0.01f) {
                float midAngle = startAngleRad + angleDiff / 2.0f;
                drawSector(drawContext, centerX, centerY, startAngleRad, midAngle, innerRadius, outerRadius, color);
                drawSector(drawContext, centerX, centerY, midAngle, endAngleRad, innerRadius, outerRadius, color);
                return;
            }

            // Calculate vertices for software rendering
            float cosStart = (float) Math.cos(startAngleRad);
            float sinStart = (float) Math.sin(startAngleRad);
            float cosEnd = (float) Math.cos(endAngleRad);
            float sinEnd = (float) Math.sin(endAngleRad);

            float innerX1 = centerX + cosStart * innerRadius;
            float innerY1 = centerY + sinStart * innerRadius;
            float outerX1 = centerX + cosStart * outerRadius;
            float outerY1 = centerY + sinStart * outerRadius;
            
            float innerX2 = centerX + cosEnd * innerRadius;
            float innerY2 = centerY + sinEnd * innerRadius;
            float outerX2 = centerX + cosEnd * outerRadius;
            float outerY2 = centerY + sinEnd * outerRadius;
            
            // Draw two triangles to form the sector quad
            // Triangle 1: Inner1, Outer1, Outer2
            fillTriangle(drawContext, (int)innerX1, (int)innerY1, (int)outerX1, (int)outerY1, (int)outerX2, (int)outerY2, color);
            // Triangle 2: Inner1, Outer2, Inner2
            fillTriangle(drawContext, (int)innerX1, (int)innerY1, (int)outerX2, (int)outerY2, (int)innerX2, (int)innerY2, color);
        } else {
            // Owo Lib rendering
            OwoUIGraphics owoGraphics = OwoUIGraphics.of(drawContext);
            
            // Convert radians to degrees
            // Add 180 degrees offset to correct orientation
            double startDeg = Math.toDegrees(startAngleRad) + 180;
            double endDeg = Math.toDegrees(endAngleRad) + 180;
            
            int segments = 32; 
            
            // Use drawRing for sectors with inner radius
            owoGraphics.drawRing(centerX, centerY, startDeg, endDeg, segments, innerRadius, outerRadius, Color.ofArgb(color), Color.ofArgb(color));
        }
    }
}
