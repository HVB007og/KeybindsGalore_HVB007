package net.hvb007.keybindsgalore;

import net.minecraft.client.gui.GuiGraphics;

/**
 * This class provides utility methods for rendering triangles and other shapes
 * using a software-based approach for GUI rendering.
 */
public class KBRenderer {

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
        // Use software rendering as it is the most reliable method for GUI in this version
        fillTriangle(drawContext, x1, y1, x2, y2, x3, y3, color);
    }
}
