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

        // Render background sectors
        renderBackgroundPie(context, numberOfSectors, sectorAngle);

        // Render selected sector
        renderSelectionIndicator(context, numberOfSectors, sectorAngle);

        renderLabelTexts(context, delta, numberOfSectors, sectorAngle);
    }

    private void renderBackgroundPie(DrawContext context, int numberOfSectors, float sectorAngle) {
        for (int i = 0; i < numberOfSectors; i++) {
            if (i == this.selectedSectorIndex) continue;

            float startAngle = i * sectorAngle;
            float endAngle = (i + 1) * sectorAngle;
            
            // Alternating colors: Brighter Grey vs Even Brighter Grey
            int fillColor = (i % 2 == 0) ? 0x80606060 : 0x80808080;
            int outlineColor = 0xFF000000; // Black outline for boundaries
            
            drawSectorWireframe(context, startAngle, endAngle, this.cancelZoneRadius, this.maxRadius, fillColor, outlineColor);
        }
    }

    private void renderSelectionIndicator(DrawContext context, int numberOfSectors, float sectorAngle) {
        if (this.selectedSectorIndex == -1) return;

        float startAngle = this.selectedSectorIndex * sectorAngle;
        float endAngle = (this.selectedSectorIndex + 1) * sectorAngle;
        
        // Light Grey for selection fill lines
        int fillColor = 0xFFE0E0E0; 
        int outlineColor = 0xFF000000; // Black outline for boundaries
        
        drawSectorWireframe(context, startAngle, endAngle, this.cancelZoneRadius, this.maxRadius, fillColor, outlineColor);
    }

    private void drawSectorWireframe(DrawContext context, float startAngle, float endAngle, float innerRadius, float outerRadius, int fillColor, int outlineColor) {
        // Draw sparse lines to indicate the sector
        int lines = 5;
        float step = (endAngle - startAngle) / (lines - 1);

        // Draw radial lines
        for (int i = 0; i < lines; i++) {
            float angle = startAngle + i * step;
            
            float x1 = this.centreX + (float) Math.cos(angle) * innerRadius;
            float y1 = this.centreY + (float) Math.sin(angle) * innerRadius;
            
            float x2 = this.centreX + (float) Math.cos(angle) * outerRadius;
            float y2 = this.centreY + (float) Math.sin(angle) * outerRadius;
            
            // Use outlineColor for first and last line, fillColor for internal lines
            int color = (i == 0 || i == lines - 1) ? outlineColor : fillColor;
            
            drawLine(context, (int)x1, (int)y1, (int)x2, (int)y2, color);
        }

        // Draw outer arc (using outlineColor)
        for (int i = 0; i < lines - 1; i++) {
            float angle1 = startAngle + i * step;
            float angle2 = startAngle + (i + 1) * step;

            float x1 = this.centreX + (float) Math.cos(angle1) * outerRadius;
            float y1 = this.centreY + (float) Math.sin(angle1) * outerRadius;

            float x2 = this.centreX + (float) Math.cos(angle2) * outerRadius;
            float y2 = this.centreY + (float) Math.sin(angle2) * outerRadius;

            drawLine(context, (int)x1, (int)y1, (int)x2, (int)y2, outlineColor);
        }
        
        // Draw inner arc (using outlineColor)
        for (int i = 0; i < lines - 1; i++) {
            float angle1 = startAngle + i * step;
            float angle2 = startAngle + (i + 1) * step;

            float x1 = this.centreX + (float) Math.cos(angle1) * innerRadius;
            float y1 = this.centreY + (float) Math.sin(angle1) * innerRadius;

            float x2 = this.centreX + (float) Math.cos(angle2) * innerRadius;
            float y2 = this.centreY + (float) Math.sin(angle2) * innerRadius;

            drawLine(context, (int)x1, (int)y1, (int)x2, (int)y2, outlineColor);
        }
    }

    private void drawLine(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance == 0) return;
        
        float stepX = dx / distance;
        float stepY = dy / distance;
        
        float curX = x1;
        float curY = y1;
        
        // Draw points with larger size (4x4) and adjusted step (3.0f) for thicker lines
        for (float i = 0; i < distance; i += 3.0f) {
            context.fill((int)curX, (int)curY, (int)curX + 4, (int)curY + 4, color);
            curX += stepX * 3.0f;
            curY += stepY * 3.0f;
        }
    }

    private void renderLabelTexts(DrawContext context, float delta, int numberOfSectors, float sectorAngle) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        
        for (int sectorIndex = 0; sectorIndex < numberOfSectors; sectorIndex++) {
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
