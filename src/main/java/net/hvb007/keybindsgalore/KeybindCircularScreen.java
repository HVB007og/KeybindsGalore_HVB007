package net.hvb007.keybindsgalore;

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
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.hvb007.keybindsgalore.KeybindsGalore.customDataManager;

public class KeybindCircularScreen extends Screen {

    private final InputConstants.Key conflictedKey;
    private final List<KeyMapping> conflicts = new ArrayList<>();
    private int selectedSectorIndex = -1;

    private int centreX = 0, centreY = 0;
    private float maxRadius = 0;
    private float cancelZoneRadius = 0;

    public KeybindCircularScreen(InputConstants.Key key) {
        super(GameNarrator.NO_TITLE);
        this.conflictedKey = key;
        this.conflicts.addAll(KeybindManager.getConflicts(key));
    }

    @Override
    protected void init() {
        super.init();
        this.centreX = this.width / 2;
        this.centreY = this.height / 2;
        this.maxRadius = Math.min(this.width, this.height) / 2.0f * 0.8f;
        this.cancelZoneRadius = maxRadius * 0.2f;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (Configurations.DARKENED_BACKGROUND) {
            this.renderBackground(context);
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

        final int colorEven = Configurations.PIE_MENU_SECTOR_COLOR_EVEN;
        final int colorOdd = Configurations.PIE_MENU_SECTOR_COLOR_ODD;
        final int colorSelected = Configurations.PIE_MENU_SECTOR_COLOR_SELECTED;
        final int colorLastOddFix = Configurations.PIE_MENU_SECTOR_COLOR_LAST_ODD;
        
        int cancelZoneColor = Configurations.PIE_MENU_CANCEL_ZONE_COLOR;
        if (mouseDistanceFromCentre <= this.cancelZoneRadius) {
            cancelZoneColor = Configurations.PIE_MENU_CANCEL_ZONE_HOVER_COLOR;
        }

        if (!Configurations.USE_SOFTWARE_RENDERING) {
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
            com.mojang.blaze3d.systems.RenderSystem.disableCull();
            com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();
            com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionColorShader);
            
            com.mojang.blaze3d.vertex.BufferBuilder bufferbuilder = com.mojang.blaze3d.vertex.Tesselator.getInstance().getBuilder();

            // DRAW MAIN SECTORS
            bufferbuilder.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLE_FAN, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);

            // Center vertex always uses sector 0's default color (NOT selected color)
            // so the highlight gradient only affects the perimeter of the selected sector
            int centerA = (colorEven >> 24) & 255;
            int centerR = (colorEven >> 16) & 255;
            int centerG = (colorEven >> 8) & 255;
            int centerB = colorEven & 255;

            bufferbuilder.vertex(this.centreX, this.centreY, 0.0D).color(centerR, centerG, centerB, centerA).endVertex();

            for (int i = 0; i < numberOfSectors; i++) {
                boolean mouseInSector = (i == this.selectedSectorIndex);
                float radius = this.maxRadius;
                if (mouseInSector) {
                    radius *= 1.025f;
                }

                int color;
                if (mouseInSector) {
                    color = colorSelected;
                } else {
                    if (numberOfSectors % 2 != 0 && i == numberOfSectors - 1) {
                        color = colorLastOddFix;
                    } else {
                        color = (i % 2 == 0) ? colorEven : colorOdd;
                    }
                }

                int a = (color >> 24) & 255;
                int r = (color >> 16) & 255;
                int g = (color >> 8) & 255;
                int b = color & 255;

                float startAngle = i * sectorAngle;
                float step = (float) Math.PI / 180.0f;

                for (float angleOffset = 0; angleOffset < sectorAngle + step / 2.0f; angleOffset += step) {
                    float rad = angleOffset + startAngle;
                    float xp = this.centreX + Mth.cos(rad) * radius;
                    float yp = this.centreY + Mth.sin(rad) * radius;

                    if (angleOffset == 0) {
                        bufferbuilder.vertex(xp, yp, 0.0D).color(r, g, b, a).endVertex();
                    }
                    bufferbuilder.vertex(xp, yp, 0.0D).color(r, g, b, a).endVertex();
                }
            }

            com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(bufferbuilder.end());

            // DRAW CANCEL ZONE ON TOP
            bufferbuilder.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLE_FAN, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);

            int cancelA = (cancelZoneColor >> 24) & 255;
            int cancelR = (cancelZoneColor >> 16) & 255;
            int cancelG = (cancelZoneColor >> 8) & 255;
            int cancelB = cancelZoneColor & 255;

            bufferbuilder.vertex(this.centreX, this.centreY, 0.0D).color(cancelR, cancelG, cancelB, cancelA).endVertex();

            for (int i = 0; i < numberOfSectors; i++) {
                float startAngle = i * sectorAngle;
                float step = (float) Math.PI / 180.0f;

                for (float angleOffset = 0; angleOffset < sectorAngle + step / 2.0f; angleOffset += step) {
                    float rad = angleOffset + startAngle;
                    float xp = this.centreX + Mth.cos(rad) * this.cancelZoneRadius;
                    float yp = this.centreY + Mth.sin(rad) * this.cancelZoneRadius;

                    if (i == 0 && angleOffset == 0) {
                        bufferbuilder.vertex(xp, yp, 0.0D).color(cancelR, cancelG, cancelB, cancelA).endVertex();
                    }
                    bufferbuilder.vertex(xp, yp, 0.0D).color(cancelR, cancelG, cancelB, cancelA).endVertex();
                }
            }

            com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(bufferbuilder.end());

            com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
            com.mojang.blaze3d.systems.RenderSystem.enableCull();
            com.mojang.blaze3d.systems.RenderSystem.disableBlend();
        } else {
            // Software rendering logic remains the same (looping individually)
            for (int i = 0; i < numberOfSectors; i++) {
                float startAngle = i * sectorAngle;
                float endAngle = (i + 1) * sectorAngle;

                int color;
                float currentRadius = this.maxRadius;

                if (i == this.selectedSectorIndex) {
                    color = colorSelected;
                } else {
                    if (numberOfSectors % 2 != 0 && i == numberOfSectors - 1) {
                        color = colorLastOddFix;
                    } else {
                        color = (i % 2 == 0) ? colorEven : colorOdd;
                    }
                }

                TriangleStripRenderer.drawSector(context, this.centreX, this.centreY, startAngle, endAngle, this.cancelZoneRadius, currentRadius, color);
            }
            
            for (int i = 0; i < numberOfSectors; i++) {
                float start = i * sectorAngle;
                float end = (i + 1) * sectorAngle;
                TriangleStripRenderer.drawSector(context, this.centreX, this.centreY, start, end, 0, this.cancelZoneRadius, cancelZoneColor);
            }
        }

        renderLabelTexts(context, numberOfSectors);
        super.render(context, mouseX, mouseY, delta);
    }


    private void renderLabelTexts(GuiGraphics context, int numberOfSectors) {
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
                context.fill((int)xPos - 2, (int)yPos - 2, (int)xPos + textWidth + 2, (int)yPos + textHeight + 2, 0x80E0E0E0);
            }

            context.drawString(textRenderer, actionName, (int) xPos, (int) yPos, 0xFFFFFFFF, true);
        }
    }

    private static double mouseAngle(int x, int y, int mx, int my) {
        return (Mth.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == this.conflictedKey.getValue()) {
            closePieMenu();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
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

    @Override
    public void renderBackground(GuiGraphics context) {
        if (Configurations.DARKENED_BACKGROUND) {
            context.fill(0, 0, this.width, this.height, 0x60000000);
        }
    }
}
