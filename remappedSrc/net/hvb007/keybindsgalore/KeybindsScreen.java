package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.mixin.AccessorKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.*;
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

        // Draw labels only
        for (int seg = 0; seg < segments; seg++) {
            boolean mouseInSector = degPer * seg < angle && angle < degPer * (seg + 1);
            float radius = Math.max(0F, Math.min((timeIn + delta - seg * 6F / segments) * 40F, MAX_RADIUS));
            if (mouseInSector) {
                radius *= 1.025f;
                slotSelected = seg;
            }

            float rad = (seg + 0.5f) * degPer;
            float xp = centerX + MathHelper.cos(rad) * radius;
            float yp = centerY + MathHelper.sin(rad) * radius;

            String name = (mouseInSector ? Formatting.UNDERLINE : Formatting.RESET)
                    + Text.translatable(keyList.get(seg).getTranslationKey()).getString();

            int textWidth = textRenderer.getWidth(name);
            float textX = xp - (xp < centerX ? textWidth - 8 : 4);
            float textY = yp - (yp < centerY ? 9 : 0);

            context.drawText(textRenderer, name, (int) textX, (int) textY, 0xFFFFFF, true);
        }
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

