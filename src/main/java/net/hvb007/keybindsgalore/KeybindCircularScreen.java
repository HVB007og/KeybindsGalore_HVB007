package net.hvb007.keybindsgalore;

import com.mojang.blaze3d.vertex.Tesselator;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Surface;
import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;
import net.hvb007.keybindsgalore.mixin.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.GameNarrator;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Util;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.hvb007.keybindsgalore.KeybindsGalore.customDataManager;

public class KeybindCircularScreen extends BaseOwoScreen<FlowLayout> {

    private final InputConstants.Key conflictedKey;
    private final List<KeyMapping> conflicts = new ArrayList<>();
    private int selectedSectorIndex = -1;
    private int ticksInScreen = 0;
    private boolean mouseDown = false;

    private int centreX = 0, centreY = 0;
    private float maxRadius = 0;
    private float maxExpandedRadius = 0;
    private float cancelZoneRadius = 0;
    private boolean isFirstFrame = true;

    public KeybindCircularScreen(InputConstants.Key key) {
        super(); 
        this.conflictedKey = key;
        this.conflicts.addAll(KeybindManager.getConflicts(key));
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, UIContainers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        if (Configurations.DARKENED_BACKGROUND) {
            rootComponent.surface(Surface.VANILLA_TRANSLUCENT);
        }
    }

    @Override
    protected void init() {
        super.init(); // Important for Owo
        this.centreX = this.width / 2;
        this.centreY = this.height / 2;
        this.maxRadius = Math.min((this.centreX * Configurations.PIE_MENU_SCALE) - Configurations.PIE_MENU_MARGIN, (this.centreY * Configurations.PIE_MENU_SCALE) - Configurations.PIE_MENU_MARGIN);
        this.maxExpandedRadius = this.maxRadius * Configurations.EXPANSION_FACTOR_WHEN_SELECTED;
        this.cancelZoneRadius = maxRadius * Configurations.CANCEL_ZONE_SCALE;
        this.isFirstFrame = false;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        // Call super.render to draw Owo UI components (including the surface background)
        super.render(context, mouseX, mouseY, delta);

        if (this.isFirstFrame) {
            this.init();
        }

        double mouseAngle = mouseAngle(this.centreX, this.centreY, mouseX, mouseY);
        float mouseDistanceFromCentre = Mth.sqrt((float) ((mouseX - this.centreX) * (mouseX - this.centreX) + (mouseY - this.centreY) * (mouseY - this.centreY)));

        int numberOfSectors = this.conflicts.size();
        if (numberOfSectors == 0) return;

        float sectorAngle = (float) (Mth.TWO_PI / numberOfSectors);

        this.selectedSectorIndex = (int) (mouseAngle / sectorAngle);
        if (this.selectedSectorIndex >= numberOfSectors) this.selectedSectorIndex = numberOfSectors - 1;
        if (this.selectedSectorIndex < 0) this.selectedSectorIndex = 0;

        if (mouseDistanceFromCentre <= this.cancelZoneRadius) {
            this.selectedSectorIndex = -1;
        }

        final int colorEven = 0xFF606060;
        final int colorOdd = 0xFF808080;
        final int colorSelected = 0xFFE0E0E0;
        final int colorLastOddFix = 0xFFA0A0A0;

        // Render sectors
        for (int i = 0; i < numberOfSectors; i++) {
            float startAngle = i * sectorAngle;
            float endAngle = (i + 1) * sectorAngle;

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

            // Use drawSector which handles both software and Owo rendering
            TriangleStripRenderer.drawSector(context, this.centreX, this.centreY, startAngle, endAngle, this.maxRadius, color);
        }

        // Render cancel zone (black circle)
        TriangleStripRenderer.drawSector(context, this.centreX, this.centreY, 0, (float)Mth.TWO_PI, this.cancelZoneRadius, 0xFF000000);

        renderLabelTexts(context, delta, numberOfSectors);
    }

    private void renderLabelTexts(GuiGraphics context, float delta, int numberOfSectors) {
        if (numberOfSectors == 0) return;

        Font textRenderer = Minecraft.getInstance().font;

        for (int sectorIndex = 0; sectorIndex < numberOfSectors; sectorIndex++) {
            float sectorAngle = (float) (Mth.TWO_PI / numberOfSectors);
            float radius = this.maxRadius;
            float textRadius = radius * 1.1f;
            float angle = (sectorIndex + 0.5f) * sectorAngle;

            float xPos = this.centreX + Mth.cos(angle) * textRadius;
            float yPos = this.centreY + Mth.sin(angle) * textRadius;

            KeyMapping action = this.conflicts.get(sectorIndex);
            String actionName = formatName(action).getString();

            int textWidth = textRenderer.width(actionName);
            int textHeight = textRenderer.lineHeight;

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
                actionName = ChatFormatting.UNDERLINE + actionName;
                // Draw highlight box BEHIND text - Very Light Grey with 50% opacity
                context.fill((int)xPos - 2, (int)yPos - 2, (int)xPos + textWidth + 2, (int)yPos + textHeight + 2, 0x80E0E0E0);
            }

            context.drawString(textRenderer, actionName, (int) xPos, (int) yPos, 0xFFFFFFFF, true);
        }
    }

    private static double mouseAngle(int x, int y, int mx, int my) {
        return (Mth.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }

    public void onKeyRelease() {
        closePieMenu();
    }

    private void closePieMenu() {
        Minecraft client = Minecraft.getInstance();
        client.setScreen(null);

        if (this.selectedSectorIndex != -1 && this.selectedSectorIndex < this.conflicts.size()) {
            KeyMapping selectedKeyBinding = this.conflicts.get(this.selectedSectorIndex);
            KeybindsGalore.activePulseTarget = selectedKeyBinding;
            KeybindsGalore.pulseTimer = 5;
            ((KeyMappingAccessor) selectedKeyBinding).setIsDown(true);
            ((KeyMappingAccessor) selectedKeyBinding).setClickCount(1);

            if (selectedKeyBinding.same(client.options.keyAttack) && Configurations.ENABLE_ATTACK_WORKAROUND) {
                ((MinecraftAccessor) client).setMissTime(0);
            }
        }
    }

    @Override
    public void tick() {
        this.ticksInScreen++;
    }

    private Component formatName(KeyMapping kb) {
        String id = KeybindManager.safeGetTranslationKey(kb);
        String cat = KeybindManager.safeGetCategory(kb);
        String nameStr = Component.translatable(cat).getString() + ": " + Component.translatable(id).getString();
        if (customDataManager.hasCustomData) {
            try {
                if (customDataManager.customData.get(id).hideCategory)
                    nameStr = Component.translatable(id).getString();
                nameStr = Objects.requireNonNull(customDataManager.customData.get(id).displayName);
            } catch (Exception ignored) {
            }
        }
        return Component.literal(nameStr);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
