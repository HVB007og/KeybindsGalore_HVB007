package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.mixin.KeyBindingAccessor;
import net.hvb007.keybindsgalore.mixin.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.hvb007.keybindsgalore.KeybindsGalore.customDataManager;

public class KeybindCircularScreen extends Screen {

    private final InputUtil.Key conflictedKey;
    private final List<KeyBinding> conflicts = new ArrayList<>();
    private int selectedSectorIndex = -1;
    private int ticksInScreen = 0;
    private boolean mouseDown = false;

    private int centreX = 0, centreY = 0;
    private float maxRadius = 0;
    private float maxExpandedRadius = 0;
    private float cancelZoneRadius = 0;
    private boolean isFirstFrame = true;

    public KeybindCircularScreen(InputUtil.Key key) {
        super(NarratorManager.EMPTY);
        this.conflictedKey = key;
        this.conflicts.addAll(KeybindManager.getConflicts(key));
    }

    @Override
    protected void init() {
        super.init();
        this.centreX = this.width / 2;
        this.centreY = this.height / 2;
        this.maxRadius = Math.min((this.centreX * Configurations.PIE_MENU_SCALE) - Configurations.PIE_MENU_MARGIN, (this.centreY * Configurations.PIE_MENU_SCALE) - Configurations.PIE_MENU_MARGIN);
        this.maxExpandedRadius = this.maxRadius * Configurations.EXPANSION_FACTOR_WHEN_SELECTED;
        this.cancelZoneRadius = maxRadius * Configurations.CANCEL_ZONE_SCALE;
        this.isFirstFrame = false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (Configurations.DARKENED_BACKGROUND) {
            context.fill(0, 0, this.width, this.height, 0x60000000);
        }

        if (this.isFirstFrame) {
            this.init();
        }

        double mouseAngle = mouseAngle(this.centreX, this.centreY, mouseX, mouseY);
        float mouseDistanceFromCentre = MathHelper.sqrt((float) ((mouseX - this.centreX) * (mouseX - this.centreX) + (mouseY - this.centreY) * (mouseY - this.centreY)));

        int numberOfSectors = this.conflicts.size();
        if (numberOfSectors == 0) return;

        float sectorAngle = (float) (MathHelper.TAU / numberOfSectors);

        this.selectedSectorIndex = (int) (mouseAngle / sectorAngle);
        if (this.selectedSectorIndex >= numberOfSectors) this.selectedSectorIndex = numberOfSectors - 1;
        if (this.selectedSectorIndex < 0) this.selectedSectorIndex = 0;

        if (mouseDistanceFromCentre <= this.cancelZoneRadius) {
            this.selectedSectorIndex = -1;
        }

        final int colorEven = 0x80606060;
        final int colorOdd = 0x80808080;
        final int colorSelected = 0xFFE0E0E0;
        final int colorLastOddFix = 0x80A0A0A0; // A third color for the odd-sector case

        // Render sectors
        for (int i = 0; i < numberOfSectors; i++) {
            float startAngle = i * sectorAngle;
            float endAngle = (i + 1) * sectorAngle;

            float outerX1 = this.centreX + (float) Math.cos(startAngle) * this.maxRadius;
            float outerY1 = this.centreY + (float) Math.sin(startAngle) * this.maxRadius;
            float outerX2 = this.centreX + (float) Math.cos(endAngle) * this.maxRadius;
            float outerY2 = this.centreY + (float) Math.sin(endAngle) * this.maxRadius;

            int color;
            if (i == this.selectedSectorIndex) {
                color = colorSelected;
            } else {
                if (numberOfSectors % 2 != 0 && i == numberOfSectors - 1) {
                    color = colorLastOddFix;
                } else {
                    color = (i % 2 == 0) ? colorEven : colorOdd;
                }
            }

            // We need to draw two triangles to form a sector of the pie
            TriangleStripRenderer.fillTriangle(context, this.centreX, this.centreY, (int) outerX1, (int) outerY1, (int) outerX2, (int) outerY2, color);
        }

        // Render cancel zone
        // This is a bit of a hack, we draw a black circle by drawing a lot of triangles
        for (int i = 0; i < 360; i++) {
            float startAngle = (float) Math.toRadians(i);
            float endAngle = (float) Math.toRadians(i + 1);

            float outerX1 = this.centreX + (float) Math.cos(startAngle) * this.cancelZoneRadius;
            float outerY1 = this.centreY + (float) Math.sin(startAngle) * this.cancelZoneRadius;
            float outerX2 = this.centreX + (float) Math.cos(endAngle) * this.cancelZoneRadius;
            float outerY2 = this.centreY + (float) Math.sin(endAngle) * this.cancelZoneRadius;

            TriangleStripRenderer.fillTriangle(context, this.centreX, this.centreY, (int) outerX1, (int) outerY1, (int) outerX2, (int) outerY2, 0xFF000000);
        }


        renderLabelTexts(context, delta, numberOfSectors);
    }

    private void renderLabelTexts(DrawContext context, float delta, int numberOfSectors) {
        if (numberOfSectors == 0) return;

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        for (int sectorIndex = 0; sectorIndex < numberOfSectors; sectorIndex++) {
            float sectorAngle = (float) (MathHelper.TAU / numberOfSectors);
            float radius = this.maxRadius;
            float textRadius = radius * 1.1f;
            float angle = (sectorIndex + 0.5f) * sectorAngle;

            float xPos = this.centreX + MathHelper.cos(angle) * textRadius;
            float yPos = this.centreY + MathHelper.sin(angle) * textRadius;

            KeyBinding action = this.conflicts.get(sectorIndex);
            String actionName = formatName(action).getString();

            int textWidth = textRenderer.getWidth(actionName);
            int textHeight = textRenderer.fontHeight;

            if (xPos > this.centreX) {
                xPos -= Configurations.LABEL_TEXT_INSET;
                if (this.width - xPos < textWidth)
                    xPos -= textWidth - this.width + xPos;
            } else {
                xPos -= textWidth - Configurations.LABEL_TEXT_INSET;
                if (xPos < 0) xPos = Configurations.LABEL_TEXT_INSET;
            }
            yPos -= Configurations.LABEL_TEXT_INSET;

            if (this.selectedSectorIndex == sectorIndex) {
                actionName = Formatting.UNDERLINE + actionName;
                // Draw highlight box BEHIND text - Very Light Grey with 50% opacity
                context.fill((int)xPos - 2, (int)yPos - 2, (int)xPos + textWidth + 2, (int)yPos + textHeight + 2, 0x80E0E0E0);
            }

            context.drawText(textRenderer, actionName, (int) xPos, (int) yPos, 0xFFFFFFFF, true);
        }
    }

    private static double mouseAngle(int x, int y, int mx, int my) {
        return (MathHelper.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }

    public void onKeyRelease() {
        closePieMenu();
    }

    private void closePieMenu() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(null);

        if (this.selectedSectorIndex != -1 && this.selectedSectorIndex < this.conflicts.size()) {
            KeyBinding selectedKeyBinding = this.conflicts.get(this.selectedSectorIndex);
            KeybindsGalore.activePulseTarget = selectedKeyBinding;
            KeybindsGalore.pulseTimer = 5;
            ((KeyBindingAccessor) selectedKeyBinding).setPressed(true);
            ((KeyBindingAccessor) selectedKeyBinding).setTimesPressed(1);

            if (selectedKeyBinding.equals(client.options.attackKey) && Configurations.ENABLE_ATTACK_WORKAROUND) {
                ((MinecraftClientAccessor) client).setAttackCooldown(0);
            }
        }
    }

    @Override
    public void tick() {
        this.ticksInScreen++;
    }

    private Text formatName(KeyBinding kb) {
        String id = KeybindManager.safeGetTranslationKey(kb);
        String cat = KeybindManager.safeGetCategory(kb);
        String nameStr = Text.translatable(cat).getString() + ": " + Text.translatable(id).getString();
        if (customDataManager.hasCustomData) {
            try {
                if (customDataManager.customData.get(id).hideCategory)
                    nameStr = Text.translatable(id).getString();
                nameStr = Objects.requireNonNull(customDataManager.customData.get(id).displayName);
            } catch (Exception ignored) {
            }
        }
        return Text.literal(nameStr);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
