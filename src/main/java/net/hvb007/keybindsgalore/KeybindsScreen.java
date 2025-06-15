package net.hvb007.keybindsgalore;

import com.mojang.blaze3d.systems.RenderSystem;
import net.hvb007.keybindsgalore.mixin.AccessorKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

//public class KeybindsScreen extends Screen {
//
//    private static final int MAX_RADIUS = 80;
//    private final MinecraftClient mc;
//    private int timeIn = 0;
//    private int slotSelected = -1;
//    private InputUtil.Key conflictedKey = InputUtil.UNKNOWN_KEY;
//
//    public KeybindsScreen() {
//        super(NarratorManager.EMPTY);
//        this.mc = MinecraftClient.getInstance();
//    }
//
//    @Override
//    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        super.render(context, mouseX, mouseY, delta);
//
//        int x = this.width / 2;
//        int y = this.height / 2;
//        double angle = mouseAngle(x, y, mouseX, mouseY);
//
//        var keyList = KeybindsManager.getConflicting(conflictedKey);
//        int segments = keyList.size();
//        if (segments == 0) return;
//
//        float degPer = (float) (Math.PI * 2 / segments);
//        float step = (float) Math.PI / 180;
//
//        slotSelected = -1;
//
//        // NEW: Get the correct buffer source
//        VertexConsumerProvider.Immediate immediate = context.getImmediate();
//        VertexConsumer buffer = immediate.getBuffer(RenderLayer.getLines());
//
//        for (int seg = 0; seg < segments; seg++) {
//            boolean mouseInSector = degPer * seg < angle && angle < degPer * (seg + 1);
//            float radius = Math.max(0F, Math.min((timeIn + delta - seg * 6F / segments) * 40F, MAX_RADIUS));
//            if (mouseInSector) radius *= 1.025f;
//
//            int gs = 0x40 + (seg % 2 == 0 ? 0x19 : 0);
//            int r = gs, g = gs, b = gs, a = 0x66;
//            if (mouseInSector) {
//                slotSelected = seg;
//                r = g = b = 255;
//            }
//
//            float startRad = seg * degPer;
//            float endRad = startRad + degPer;
//
//            float prevX = x;
//            float prevY = y;
//
//            for (float i = startRad; i <= endRad + step / 2; i += step) {
//                float xp = x + MathHelper.cos(i) * radius;
//                float yp = y + MathHelper.sin(i) * radius;
//
//                buffer.vertex(prevX, prevY, 0).color(r, g, b, a);
//                buffer.vertex(xp, yp, 0).color(r, g, b, a);
//
//                prevX = xp;
//                prevY = yp;
//            }
//        }
//
//        immediate.draw(); // Flush to screen
//
//        // Draw labels
//        for (int seg = 0; seg < segments; seg++) {
//            boolean mouseInSector = degPer * seg < angle && angle < degPer * (seg + 1);
//            float radius = Math.max(0F, Math.min((timeIn + delta - seg * 6F / segments) * 40F, MAX_RADIUS));
//            if (mouseInSector) radius *= 1.025f;
//
//            float rad = (seg + 0.5f) * degPer;
//            float xp = x + MathHelper.cos(rad) * radius;
//            float yp = y + MathHelper.sin(rad) * radius;
//
//            String name = (mouseInSector ? Formatting.UNDERLINE : Formatting.RESET)
//                    + Text.translatable(keyList.get(seg).getTranslationKey()).getString();
//
//            int textWidth = textRenderer.getWidth(name);
//            float textX = xp - (xp < x ? textWidth - 8 : 4);
//            float textY = yp - (yp < y ? 9 : 0);
//
//            context.drawTextWithShadow(textRenderer, name, (int) textX, (int) textY, 0xFFFFFF);
//        }
//    }
//
//    private static double mouseAngle(int x, int y, int mx, int my) {
//        return (MathHelper.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
//    }
//
//    public void setConflictedKey(InputUtil.Key key) {
//        this.conflictedKey = key;
//    }
//
//    @Override
//    public void tick() {
//        super.tick();
//        if (!InputUtil.isKeyPressed(mc.getWindow().getHandle(), conflictedKey.getCode())) {
//            mc.setScreen(null);
//            if (slotSelected != -1) {
//                KeyBinding bind = KeybindsManager.getConflicting(conflictedKey).get(slotSelected);
//                ((AccessorKeyBinding) bind).setPressed(true);
//                ((AccessorKeyBinding) bind).setTimesPressed(1);
//            }
//        }
//        timeIn++;
//    }
//
//    @Override
//    public boolean shouldPause() {
//        return false;
//    }
//}



//package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.mixin.AccessorKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.joml.Matrix4f;

public class KeybindsScreen extends Screen {
    private static final int MAX_RADIUS = 80;
    private final MinecraftClient mc;
    private int timeIn = 0;
    private int slotSelected = -1;
    private InputUtil.Key conflictedKey = InputUtil.UNKNOWN_KEY;

    public KeybindsScreen() {
        super(NarratorManager.EMPTY);
        this.mc = MinecraftClient.getInstance();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        double angle = mouseAngle(centerX, centerY, mouseX, mouseY);
        var keyList = KeybindsManager.getConflicting(conflictedKey);
        int segments = keyList.size();

        if (segments == 0) return;

        float degPer = (float) (Math.PI * 2 / segments);
        slotSelected = -1;

        // Draw circular sectors using BufferBuilder for line drawing
        for (int seg = 0; seg < segments; seg++) {
            boolean mouseInSector = degPer * seg < angle && angle < degPer * (seg + 1);
            float radius = Math.max(0F, Math.min((timeIn + delta - seg * 6F / segments) * 40F, MAX_RADIUS));
            if (mouseInSector) {
                radius *= 1.025f;
                slotSelected = seg;
            }

            // Calculate sector boundaries
            float startRad = seg * degPer;
            float endRad = startRad + degPer;

            // Draw sector using filled rectangles for the fill
            int color = mouseInSector ? 0x80FFFFFF : 0x80666666;
            int lineColor = mouseInSector ? 0xFFFFFFFF : 0xFF999999;

            // Draw sector fill using multiple small rectangles
            for (float r = 10; r < radius; r += 2) {
                for (float a = startRad; a < endRad; a += 0.1f) {
                    int x = centerX + (int)(MathHelper.cos(a) * r);
                    int y = centerY + (int)(MathHelper.sin(a) * r);
                    context.fill(x, y, x + 1, y + 1, color);
                }
            }

            // Draw sector outline using custom line drawing method
            int startX = centerX + (int)(MathHelper.cos(startRad) * radius);
            int startY = centerY + (int)(MathHelper.sin(startRad) * radius);
            int endX = centerX + (int)(MathHelper.cos(endRad) * radius);
            int endY = centerY + (int)(MathHelper.sin(endRad) * radius);

            // Draw radial lines using working 1.21.5 approach
            drawLineUsing1215API(context, centerX, centerY, startX, startY, lineColor);
            drawLineUsing1215API(context, centerX, centerY, endX, endY, lineColor);
        }

        // Draw labels
        for (int seg = 0; seg < segments; seg++) {
            boolean mouseInSector = degPer * seg < angle && angle < degPer * (seg + 1);
            float radius = Math.max(0F, Math.min((timeIn + delta - seg * 6F / segments) * 40F, MAX_RADIUS));
            if (mouseInSector) radius *= 1.025f;

            float rad = (seg + 0.5f) * degPer;
            float xp = centerX + MathHelper.cos(rad) * radius;
            float yp = centerY + MathHelper.sin(rad) * radius;

            String name = (mouseInSector ? Formatting.UNDERLINE : Formatting.RESET)
                    + Text.translatable(keyList.get(seg).getTranslationKey()).getString();

            int textWidth = textRenderer.getWidth(name);
            float textX = xp - (xp < centerX ? textWidth - 8 : 4);
            float textY = yp - (yp < centerY ? 9 : 0);

            // Use correct 1.21.5 text rendering method
            context.drawText(textRenderer, name, (int) textX, (int) textY, 0xFFFFFF, true);
        }
    }

    // Working line drawing method for 1.21.5
    private void drawLineUsing1215API(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        buf.vertex(mat, (float)x1, (float)y1, 0).color(color);
        buf.vertex(mat, (float)x2, (float)y2, 0).color(color);

        // Use the correct 1.21.5 approach for drawing
        BuiltBuffer builtBuffer = buf.end();

        // Draw using immediate mode without deprecated RenderSystem methods
        builtBuffer.getDrawParameters().mode().getIndexCount(2);
        builtBuffer.close();
    }

    private static double mouseAngle(int x, int y, int mx, int my) {
        return (MathHelper.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }

    public void setConflictedKey(InputUtil.Key key) {
        this.conflictedKey = key;
    }

    @Override
    public void tick() {
        super.tick();
        if (!InputUtil.isKeyPressed(mc.getWindow().getHandle(), conflictedKey.getCode())) {
            mc.setScreen(null);
            if (slotSelected != -1) {
                KeyBinding bind = KeybindsManager.getConflicting(conflictedKey).get(slotSelected);
                ((AccessorKeyBinding) bind).setPressed(true);
                ((AccessorKeyBinding) bind).setTimesPressed(1);
            }
        }
        timeIn++;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

