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

    public static void drawSector(GuiGraphics drawContext, int centerX, int centerY, float startAngleRad, float endAngleRad, float radius, int color) {
        if (Configurations.USE_SOFTWARE_RENDERING) {
            // Calculate vertices for software rendering
            float outerX1 = centerX + (float) Math.cos(startAngleRad) * radius;
            float outerY1 = centerY + (float) Math.sin(startAngleRad) * radius;
            float outerX2 = centerX + (float) Math.cos(endAngleRad) * radius;
            float outerY2 = centerY + (float) Math.sin(endAngleRad) * radius;
            
            fillTriangle(drawContext, centerX, centerY, (int) outerX1, (int) outerY1, (int) outerX2, (int) outerY2, color);
        } else {
            // Owo Lib rendering
            OwoUIGraphics owoGraphics = OwoUIGraphics.of(drawContext);
            
            // Convert radians to degrees
            // Add 90 degrees offset because Owo seems to start at South (Down) or similar?
            // User reported diametrically opposite (180 deg) offset.
            // Let's try adding 180 degrees.
            double offset = 90; // Actually, usually 0 is East. If it's opposite, maybe it's West?
            // If I hover 3 (Left-Up) and 1 (Right-Down) highlights.
            // 3 is 180-270. 1 is 0-90.
            // So I need to shift 0-90 to 180-270. That is +180.
            
            // Wait, if I hover 3 (Left-Up), I WANT 3 to highlight.
            // But 1 highlights.
            // So when I pass angles for 3 (180-270), it draws at 0-90?
            // No, the loop iterates 0..N.
            // If selectedSectorIndex is 3.
            // The loop draws sector 3 with "Selected" color.
            // Sector 3 angles are 270-360 (Up-Right).
            // If it draws at Left-Down (90-180), then it's 180 off.
            
            // Let's try adding 90 first, because usually 0 is Up in some systems.
            // If 0 is Up (270 deg standard), and I pass 0 (East), it draws at East relative to Up? i.e. Right-Down?
            
            // Let's try adding 90 degrees.
            // If that's wrong, I'll try 180.
            // Actually, if it's "diametrically opposite", it's 180.
            
            // But wait, Owo might be using a different coordinate system.
            // Let's try +90 first as it's a common offset (East vs North).
            
            double startDeg = Math.toDegrees(startAngleRad) + 90;
            double endDeg = Math.toDegrees(endAngleRad) + 90;

            int segments = 32; 
            
            owoGraphics.drawCircle(centerX, centerY, startDeg, endDeg, segments, radius, Color.ofArgb(color));
        }
    }
}
