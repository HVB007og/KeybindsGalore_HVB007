package net.hvb007.keybindsgalore;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.*;

/**
 * Minecraft 1.21.11 Triangle Strip Rendering Example (Yarn Mappings)
 *
 * This class demonstrates how to render shapes using DrawContext
 * (Yarn-mapped equivalent of GuiGraphics in official Mojang mappings).
 *
 * DrawContext is the primary GUI/HUD rendering class in Minecraft 1.21.11.
 * It provides methods for drawing rectangles, lines, text, and textures.
 */
public class TriangleStripRenderer {

    /**
     * Renders a diamond shape using lines.
     *
     * @param drawContext The DrawContext instance
     * @param x The X coordinate for the diamond center
     * @param y The Y coordinate for the diamond center
     * @param size The size of the diamond
     * @param color The ARGB color value
     */
    /**
     * Renders a filled diamond shape using triangles.
     *
     * @param drawContext The DrawContext instance
     * @param x The X coordinate for the diamond center
     * @param y The Y coordinate for the diamond center
     * @param size The size of the diamond
     * @param color The ARGB color value
     */
    public static void renderDiamond(DrawContext drawContext, float x, float y, float size, int color) {
        int xi = (int) x;
        int yi = (int) y;
        int si = (int) size;

        // Top triangle
        fillTriangle(drawContext, xi, yi - si, xi - si, yi, xi + si, yi, color);

        // Bottom triangle
        fillTriangle(drawContext, xi, yi + si, xi - si, yi, xi + si, yi, color);
    }

    /**
     * Fills a triangle using horizontal scanlines.
     * Package-private so other classes in the same package can call it directly.
     */
    static void fillTriangle(DrawContext drawContext, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
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

            drawContext.drawHorizontalLine((int)xLeft, (int)xRight, y, color);
        }
    }

    /**
     * Renders a triangle strip from a list of vertices.
     *
     * @param drawContext The DrawContext instance
     * @param vertices A list of Vec2f points for the strip
     * @param color The ARGB color value
     */
    public static void renderTriangleStrip(DrawContext drawContext, java.util.List<Vec2f> vertices, int color) {
        if (vertices.size() < 3) {
            return; // A triangle strip needs at least 3 vertices
        }

        for (int i = 0; i < vertices.size() - 2; i++) {
            Vec2f v1 = vertices.get(i);
            Vec2f v2 = vertices.get(i + 1);
            Vec2f v3 = vertices.get(i + 2);

            // The order of vertices matters for winding.
            // If i is even, the triangle is (v1, v2, v3).
            // If i is odd, the triangle is (v2, v1, v3) to maintain winding order.
            if (i % 2 == 0) {
                fillTriangle(drawContext, (int)v1.x, (int)v1.y, (int)v2.x, (int)v2.y, (int)v3.x, (int)v3.y, color);
            } else {
                fillTriangle(drawContext, (int)v2.x, (int)v2.y, (int)v1.x, (int)v1.y, (int)v3.x, (int)v3.y, color);
            }
        }
    }

    /**
     * Renders a quad using a triangle strip.
     *
     * @param drawContext The DrawContext instance
     * @param x The X coordinate
     * @param y The Y coordinate
     * @param width The width of the quad
     * @param height The height of the quad
     * @param color The ARGB color value
     */
    public static void renderQuad(DrawContext drawContext, int x, int y, int width, int height, int color) {
        java.util.List<Vec2f> vertices = new java.util.ArrayList<>();
        vertices.add(new Vec2f(x, y));
        vertices.add(new Vec2f(x + width, y));
        vertices.add(new Vec2f(x, y + height));
        vertices.add(new Vec2f(x + width, y + height));
        renderTriangleStrip(drawContext, vertices, color);
    }

    /**
     * Renders a gradient rectangle.
     *
     * @param drawContext The DrawContext instance
     * @param startX The starting X coordinate
     * @param startY The starting Y coordinate
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param color1 First color (ARGB)
     * @param color2 Second color (ARGB)
     */
    public static void renderGradientStrip(DrawContext drawContext, int startX, int startY,
                                           int width, int height, int color1, int color2) {
        drawContext.fillGradient(startX, startY, startX + width, startY + height, color1, color2);
    }

    /**
     * Renders a filled rectangle.
     *
     * @param drawContext The DrawContext instance
     * @param x1 First X coordinate
     * @param y1 First Y coordinate
     * @param x2 Second X coordinate
     * @param y2 Second Y coordinate
     * @param color The ARGB color value
     */
    public static void renderRectangle(DrawContext drawContext, int x1, int y1, int x2, int y2, int color) {
        drawContext.fill(x1, y1, x2, y2, color);
    }

    /**
     * Renders a rectangle outline (border/stroke).
     *
     * @param drawContext The DrawContext instance
     * @param x The X coordinate
     * @param y The Y coordinate
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param color The ARGB color value
     */
    public static void renderStrokedRectangle(DrawContext drawContext, int x, int y, int width, int height, int color) {
        drawContext.drawStrokedRectangle(x, y, width, height, color);
    }

    /**
     * Renders a horizontal line.
     *
     * @param drawContext The DrawContext instance
     * @param x1 Starting X coordinate
     * @param x2 Ending X coordinate
     * @param y The Y coordinate
     * @param color The ARGB color value
     */
    public static void renderHorizontalLine(DrawContext drawContext, int x1, int x2, int y, int color) {
        drawContext.drawHorizontalLine(x1, x2, y, color);
    }

    /**
     * Renders a vertical line.
     *
     * @param drawContext The DrawContext instance
     * @param x The X coordinate
     * @param y1 Starting Y coordinate
     * @param y2 Ending Y coordinate
     * @param color The ARGB color value
     */
    public static void renderVerticalLine(DrawContext drawContext, int x, int y1, int y2, int color) {
        drawContext.drawVerticalLine(x, y1, y2, color);
    }

    /**
     * Renders a pulsing diamond that scales over time.
     *
     * @param drawContext The DrawContext instance
     * @param centerX The X center coordinate
     * @param centerY The Y center coordinate
     * @param baseSize The base size of the diamond
     * @param color The ARGB color value
     */
    public static void renderPulsingDiamond(DrawContext drawContext, float centerX, float centerY,
                                            float baseSize, int color) {
        float currentTime = System.currentTimeMillis() / 1000f;
        float scale = MathHelper.sin(currentTime * 2f) * 0.5f + 1.5f;

        renderDiamond(drawContext, centerX, centerY, baseSize * scale, color);
    }

    /**
     * Enables scissor test (clipping) for GUI rendering.
     *
     * @param drawContext The DrawContext instance
     * @param x1 Top-left X coordinate
     * @param y1 Top-left Y coordinate
     * @param x2 Bottom-right X coordinate
     * @param y2 Bottom-right Y coordinate
     */
    public static void enableScissor(DrawContext drawContext, int x1, int y1, int x2, int y2) {
        drawContext.enableScissor(x1, y1, x2, y2);
    }

    /**
     * Disables scissor test (clipping).
     *
     * @param drawContext The DrawContext instance
     */
    public static void disableScissor(DrawContext drawContext) {
        drawContext.disableScissor();
    }

    /**
     * Checks if a point is within the current scissor region.
     *
     * @param drawContext The DrawContext instance
     * @param x The X coordinate
     * @param y The Y coordinate
     * @return true if the point is within the scissor region, false otherwise
     */
    public static boolean scissorContains(DrawContext drawContext, int x, int y) {
        return drawContext.scissorContains(x, y);
    }

    /**
     * Gets the DrawContext's matrix stack for transformations.
     *
     * @param drawContext The DrawContext instance
     * @return The Matrix3x2fStack for transformations
     */
    public static org.joml.Matrix3x2fStack getMatrices(DrawContext drawContext) {
        return drawContext.getMatrices();
    }

    /**
     * IMPORTANT NOTES FOR MINECRAFT 1.21.11:
     *
     * 1. DrawContext API Available Methods:
     *    - fill(x1, y1, x2, y2, color) - filled rectangle
     *    - fillGradient(startX, startY, endX, endY, colorStart, colorEnd) - gradient fill
     *    - drawHorizontalLine(x1, x2, y, color) - horizontal line
     *    - drawVerticalLine(x, y1, y2, color) - vertical line
     *    - drawStrokedRectangle(x, y, width, height, color) - rectangle outline
     *    - enableScissor(x1, y1, x2, y2) - enable clipping
     *    - disableScissor() - disable clipping
     *    - scissorContains(x, y) - check if point is in scissor
     *    - getMatrices() - get Matrix3x2fStack
     *    - drawText() methods for text rendering
     *    - drawGuiTexture() / drawTexture() for texture rendering
     *    - drawItem() for item rendering
     *
     * 2. COLOR FORMAT:
     *    - ARGB format (0xAARRGGBB)
     *    - Extract: alpha = (color >> 24) & 0xFF, etc.
     *
     * 3. MATRICES:
     *    - Use drawContext.getMatrices() for Matrix3x2fStack
     *    - Methods: pushMatrix(), popMatrix(), translate(), scale(), rotate()
     *
     * 4. NO DIRECT VERTEX/BUFFER RENDERING IN DrawContext:
     *    - DrawContext does NOT expose BufferBuilder for GUI rendering
     *    - For low-level rendering with vertices, use WorldRenderContext instead
     *    - DrawContext is limited to rectangles, lines, text, textures, and items
     *
     * 5. USAGE EXAMPLE:
     *    private void renderScreen(DrawContext drawContext, int mouseX, int mouseY) {
     *        TriangleStripRenderer.renderDiamond(drawContext, 100, 100, 50, 0xFFFFFFFF);
     *        TriangleStripRenderer.renderGradientStrip(drawContext, 10, 10, 100, 50, 0xFF0000FF, 0xFFFF0000);
     *    }
     */
}
