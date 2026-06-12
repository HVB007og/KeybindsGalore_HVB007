package net.hvb007.keybindsgalore;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

/**
 * This class provides utility methods for rendering triangles and other shapes
 * using a software-based approach for GUI rendering.
 */
public class TriangleStripRenderer {

    /**
     * Fills a triangle using horizontal scanlines.
     * This is a software-based approach and may have aliasing.
     */
    public static void fillTriangle(GuiGraphics drawContext, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        // Sort vertices by y coordinate
        if (y1 > y2) { int tx = x1; x1 = x2; x2 = tx; int ty = y1; y1 = y2; y2 = ty; }
        if (y1 > y3) { int tx = x1; x1 = x3; x3 = tx; int ty = y1; y1 = y3; y3 = ty; }
        if (y2 > y3) { int tx = x2; x2 = x3; x3 = tx; int ty = y2; y2 = y3; y3 = ty; }

        // Draw horizontal lines to fill triangle
        for (int y = y1; y <= y3; y++) {
            float xLeft, xRight;

            if (y <= y2) {
                float denom = y2 - y1;
                xLeft = (denom == 0) ? x1 : x1 + (float)(x2 - x1) * (y - y1) / denom;
                denom = y3 - y1;
                xRight = (denom == 0) ? x1 : x1 + (float)(x3 - x1) * (y - y1) / denom;
            } else {
                float denom = y3 - y2;
                xLeft = (denom == 0) ? x2 : x2 + (float)(x3 - x2) * (y - y2) / denom;
                denom = y3 - y1;
                xRight = (denom == 0) ? x1 : x1 + (float)(x3 - x1) * (y - y1) / denom;
            }

            if (xLeft > xRight) { float temp = xLeft; xLeft = xRight; xRight = temp; }
            drawContext.hLine((int)xLeft, (int)xRight, y, color);
        }
    }

    /**
     * Fills a convex quad using horizontal scanlines.
     * This avoids internal seams that appear when drawing two triangles with transparency.
     */
    public static void fillQuad(GuiGraphics drawContext, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int color) {
        // Find min and max Y to determine scanline range
        int minY = Math.min(Math.min(y1, y2), Math.min(y3, y4));
        int maxY = Math.max(Math.max(y1, y2), Math.max(y3, y4));

        // For each scanline, find the min and max X intersections with the 4 edges
        for (int y = minY; y <= maxY; y++) {
            float minX = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE;

            // Inline logic for finding intersections
            // Edge 1-2
            if ((y1 <= y && y2 > y) || (y2 <= y && y1 > y)) {
                float x = x1 + (float)(x2 - x1) * (y - y1) / (y2 - y1);
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
            }
            // Edge 2-3
            if ((y2 <= y && y3 > y) || (y3 <= y && y2 > y)) {
                float x = x2 + (float)(x3 - x2) * (y - y2) / (y3 - y2);
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
            }
            // Edge 3-4
            if ((y3 <= y && y4 > y) || (y4 <= y && y3 > y)) {
                float x = x3 + (float)(x4 - x3) * (y - y3) / (y4 - y3);
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
            }
            // Edge 4-1
            if ((y4 <= y && y1 > y) || (y1 <= y && y4 > y)) {
                float x = x4 + (float)(x1 - x4) * (y - y4) / (y1 - y4);
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
            }

            if (minX != Float.MAX_VALUE && maxX != -Float.MAX_VALUE) {
                drawContext.hLine((int)minX, (int)maxX, y, color);
            }
        }
    }

    public static void drawTriangle(GuiGraphics drawContext, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        fillTriangle(drawContext, x1, y1, x2, y2, x3, y3, color);
    }

    public static void drawSector(GuiGraphics drawContext, int centerX, int centerY, float startAngleRad, float endAngleRad, float innerRadius, float outerRadius, int color) {
        if (Configurations.USE_SOFTWARE_RENDERING) {
            // Check if angle is too large (>= PI) and split if necessary
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
            
            // Use fillQuad instead of two triangles to avoid seams
            fillQuad(drawContext, (int)innerX1, (int)innerY1, (int)outerX1, (int)outerY1, (int)outerX2, (int)outerY2, (int)innerX2, (int)innerY2, color);
        } else {
            if (Configurations.VERBOSE_DEBUG) {
                KeybindsGalore.LOGGER.info("(KBG DEBUG) drawSector HW: center({}, {}), rIn={}, rOut={}, angles({} -> {}), color={}", 
                    centerX, centerY, innerRadius, outerRadius, startAngleRad, endAngleRad, Integer.toHexString(color));
            }
            
            // Hardware-accelerated rendering natively without OwoLib
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
            com.mojang.blaze3d.systems.RenderSystem.disableCull();
            com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();
            com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionColorShader);

            org.joml.Matrix4f matrix = drawContext.pose().last().pose();
            com.mojang.blaze3d.vertex.BufferBuilder bufferbuilder = com.mojang.blaze3d.vertex.Tesselator.getInstance().getBuilder();
            bufferbuilder.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLE_STRIP, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);

            int a = (color >> 24) & 255;
            int r = (color >> 16) & 255;
            int g = (color >> 8) & 255;
            int b = color & 255;

            int segments = 32;
            float angleStep = (endAngleRad - startAngleRad) / segments;
            
            for (int i = 0; i <= segments; i++) {
                float angle = startAngleRad + i * angleStep;
                float cos = (float) Math.cos(angle);
                float sin = (float) Math.sin(angle);
                
                bufferbuilder.vertex(matrix, centerX + cos * innerRadius, centerY + sin * innerRadius, 0.0F).color(r, g, b, a);
                bufferbuilder.vertex(matrix, centerX + cos * outerRadius, centerY + sin * outerRadius, 0.0F).color(r, g, b, a);
            }
            
            com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(bufferbuilder.end());
            com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
            com.mojang.blaze3d.systems.RenderSystem.enableCull();
            com.mojang.blaze3d.systems.RenderSystem.disableBlend();
        }
    }
}
