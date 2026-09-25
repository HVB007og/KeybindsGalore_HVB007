package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.ui.model.CircularMenuGeometry;
import net.hvb007.keybindsgalore.ui.model.ConflictSelectionModel;
import net.hvb007.keybindsgalore.input.minecraft.SelectionActivationService;
import net.hvb007.keybindsgalore.ui.minecraft.ConflictActionPresentation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

import static net.hvb007.keybindsgalore.KeybindsGalore.customDataManager;

public class KeybindCircularScreen extends Screen {

    private final InputConstants.Key conflictedKey;
    private final List<KeyMapping> conflicts = new ArrayList<>();
    private final ConflictActionPresentation presentation;
    private final ConflictSelectionModel<KeyMapping> selection;

    private int centreX = 0, centreY = 0;
    private float maxRadius = 0;
    private float cancelZoneRadius = 0;
    private int lastHoveredSector = Integer.MIN_VALUE;

    public KeybindCircularScreen(InputConstants.Key key) {
        super(Component.empty());
        this.conflictedKey = key;
        this.conflicts.addAll(KeybindManager.getConflicts(key));
        this.presentation = new ConflictActionPresentation(this.conflicts, customDataManager);
        this.selection = new ConflictSelectionModel<>(this.conflicts);
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
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        if (Configurations.DARKENED_BACKGROUND) {
            this.renderBackground(context, mouseX, mouseY, delta);
        }

        float mouseDistanceFromCentre = Mth.sqrt((float) ((mouseX - this.centreX) * (mouseX - this.centreX) + (mouseY - this.centreY) * (mouseY - this.centreY)));

        int numberOfSectors = this.conflicts.size();
        if (numberOfSectors == 0) return;

        CircularMenuGeometry.Selection geometrySelection = CircularMenuGeometry.select(
                mouseX - this.centreX,
                mouseY - this.centreY,
                this.cancelZoneRadius,
                numberOfSectors
        );
        this.selection.select(geometrySelection.sectorIndex());
        if (geometrySelection.sectorIndex() != this.lastHoveredSector) {
            this.lastHoveredSector = geometrySelection.sectorIndex();
            if (geometrySelection.sectorIndex() < 0) {
                KeybindsGalore.verboseLog("Pie hover: cancel zone for key {}", this.conflictedKey.getName());
            } else {
                KeybindsGalore.verboseLog("Pie hover: sector {} -> {}",
                        geometrySelection.sectorIndex(), this.presentation.label(geometrySelection.sectorIndex()));
            }
        }

        float sectorAngle = (float) (Mth.TWO_PI / numberOfSectors);

        final int colorEven = Configurations.PIE_MENU_SECTOR_COLOR_EVEN;
        final int colorOdd = Configurations.PIE_MENU_SECTOR_COLOR_ODD;
        final int colorSelected = Configurations.PIE_MENU_SECTOR_COLOR_SELECTED;
        final int colorLastOddFix = Configurations.PIE_MENU_SECTOR_COLOR_LAST_ODD;

        super.extractRenderState(context, mouseX, mouseY, delta);

        int segments = Math.max(4, Configurations.CIRCLE_VERTICES);

        for (int i = 0; i < numberOfSectors; i++) {
            float startAngleRad = i * sectorAngle;
            float endAngleRad = (i + 1) * sectorAngle;

            int color;
            if (i == selection.selectedIndex()) {
                color = colorSelected;
            } else {
                if (numberOfSectors % 2 != 0 && i == numberOfSectors - 1) {
                    color = colorLastOddFix;
                } else {
                    color = (i % 2 == 0) ? colorEven : colorOdd;
                }
            }

            double startDeg = Math.toDegrees(startAngleRad);
            double endDeg = Math.toDegrees(endAngleRad);

            RingRenderer.drawRing(context, this.centreX, this.centreY,
                startDeg, endDeg,
                segments,
                this.cancelZoneRadius, this.maxRadius,
                color, color);
        }

        int cancelZoneColor = mouseDistanceFromCentre <= this.cancelZoneRadius
            ? Configurations.PIE_MENU_CANCEL_ZONE_HOVER_COLOR
            : Configurations.PIE_MENU_CANCEL_ZONE_COLOR;

        RingRenderer.drawCircle(context, this.centreX, this.centreY, 0, 360,
            segments, this.cancelZoneRadius,
            cancelZoneColor);

        renderLabelTexts(context, numberOfSectors);
    }

    private void renderLabelTexts(GuiGraphicsExtractor context, int numberOfSectors) {
        if (numberOfSectors == 0) return;

        Font textRenderer = Minecraft.getInstance().font;

        for (int sectorIndex = 0; sectorIndex < numberOfSectors; sectorIndex++) {
            float sectorAngle = (float) (Mth.TWO_PI / numberOfSectors);
            float radius = this.maxRadius;

            float textRadius = radius * 1.1f;
            float angle = (sectorIndex + 0.5f) * sectorAngle;

            float xPos = this.centreX + Mth.cos(angle) * textRadius;
            float yPos = this.centreY + Mth.sin(angle) * textRadius;

            String actionName = presentation.label(sectorIndex);

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

            if (selection.selectedIndex() == sectorIndex) {
                actionName = ChatFormatting.UNDERLINE + actionName;
                context.fill((int)xPos - 2, (int)yPos - 2, (int)xPos + textWidth + 2, (int)yPos + textHeight + 2, 0x80E0E0E0);
            }

            context.text(textRenderer, actionName, (int) xPos, (int) yPos, 0xFFFFFFFF, true);
        }
    }

    public boolean ownsKey(InputConstants.Key key) {
        return conflictedKey.equals(key);
    }

    public void onKeyRelease() {
        onKeyRelease(conflictedKey);
    }

    public void onKeyRelease(InputConstants.Key key) {
        tryFinalize(key);
    }

    public boolean tryFinalize(InputConstants.Key key) {
        if (selection.isFinalized() || !ownsKey(key)) {
            return false;
        }

        selection.beginFinalization();
        KeyMapping selected = selection.selected();
        if (selected == null) {
            KeybindsGalore.verboseLog("Pie selection CANCELLED for key {}: cursor was inside the cancel zone or over no sector", key.getName());
        } else {
            KeybindsGalore.verboseLog("Pie selection FINALISED for key {}: chose sector {} -> {}",
                    key.getName(), selection.selectedIndex(), selected.getName());
        }
        Minecraft.getInstance().gui.setScreen(null);
        SelectionActivationService.activate(conflicts, selected);
        return true;
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        return tryFinalize(InputConstants.getKey(event));
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return tryFinalize(InputConstants.Type.MOUSE.getOrCreate(event.button()));
    }

    @Override
    public void onClose() {
        if (!selection.isFinalized()) {
            KeybindsGalore.verboseLog("Pie selection CANCELLED for key {}: selector closed without releasing over a sector", conflictedKey.getName());
            selection.cancel();
            SelectionActivationService.cancel(conflicts);
        }
        Minecraft.getInstance().gui.setScreen(null);
    }

    @Override
    public void removed() {
        if (!selection.isFinalized()) {
            KeybindsGalore.verboseLog("Pie selection CANCELLED for key {}: selector removed before finalisation", conflictedKey.getName());
            selection.cancel();
            SelectionActivationService.cancel(conflicts);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void renderBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        if (Configurations.DARKENED_BACKGROUND) {
            context.fill(0, 0, this.width, this.height, 0x60000000);
        }
    }
}
