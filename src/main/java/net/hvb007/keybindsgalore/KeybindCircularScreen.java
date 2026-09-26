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

    // Labels sit just outside the pie edge rather than at a user-tunable distance.
    private static final float LABEL_GAP = 4.0f;
    private static final int SHADOW_OFFSET = 1;
    private static final int SHADOW_COLOR = 0x40000000;
    // How much each channel is lifted at the inner edge of a wedge when gradation is on.
    private static final int GRADATION_LIFT = 0x18;
    // Open animation length in milliseconds.
    private static final long OPEN_ANIMATION_MILLIS = 180L;

    private final InputConstants.Key conflictedKey;
    private final List<KeyMapping> conflicts = new ArrayList<>();
    private final ConflictActionPresentation presentation;
    private final ConflictSelectionModel<KeyMapping> selection;

    private int centreX = 0, centreY = 0;
    private float maxRadius = 0;
    private float cancelZoneRadius = 0;
    private int lastHoveredSector = Integer.MIN_VALUE;
    private long openedAtNanos;

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
        this.openedAtNanos = System.nanoTime();

        // PIE_MENU_MARGIN shrinks the available area, PIE_MENU_SCALE then takes a
        // fraction of what is left, and CANCEL_ZONE_SCALE is a fraction of the pie.
        // The option defaults were raised to the values this code previously
        // hardcoded (0.8 and 0.2) so that wiring them up does not resize the pie.
        float available = Math.max(0, Math.min(this.width, this.height) / 2.0f - Configurations.PIE_MENU_MARGIN);
        this.maxRadius = available * Configurations.PIE_MENU_SCALE;
        this.cancelZoneRadius = maxRadius * Configurations.CANCEL_ZONE_SCALE;
    }

    /**
     * Eased 0..1 open progress. Only the opening is animated; closing stays immediate
     * because delaying the screen teardown would mean holding input after the selection
     * has already been committed, and that is the most delicate path in the mod.
     */
    private float openProgress() {
        if (!Configurations.ANIMATE_PIE_MENU) {
            return 1.0f;
        }
        long elapsed = (System.nanoTime() - this.openedAtNanos) / 1_000_000L;
        if (elapsed >= OPEN_ANIMATION_MILLIS) {
            return 1.0f;
        }
        float t = (float) elapsed / (float) OPEN_ANIMATION_MILLIS;
        float eased = 1.0f - (1.0f - t) * (1.0f - t) * (1.0f - t);
        return Math.max(0.0f, eased);
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

        int totalSegments = Math.max(12, Configurations.CIRCLE_VERTICES);
        // Split the vertex budget across the wedges so a wedge is only as smooth as it
        // needs to be. Previously every wedge got the full budget, so the cancel circle
        // visibly polygonalised long before the pie did.
        int segmentsPerSector = Math.max(4, Math.round((float) totalSegments / numberOfSectors));

        // Scale the whole pie up from the centre over the opening animation.
        float progress = openProgress();
        float animatedMaxRadius = this.maxRadius * progress;
        float animatedCancelRadius = this.cancelZoneRadius * progress;
        if (animatedMaxRadius <= 0.5f) {
            renderLabelTexts(context, numberOfSectors, progress);
            return;
        }

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

            float outerRadius = animatedMaxRadius;
            if (i == selection.selectedIndex() && Configurations.EXPANSION_FACTOR_WHEN_SELECTED > 0) {
                outerRadius = animatedMaxRadius * (1.0f + Configurations.EXPANSION_FACTOR_WHEN_SELECTED);
            }

            RingRenderer.drawRing(context, this.centreX, this.centreY,
                startDeg, endDeg,
                segmentsPerSector,
                animatedCancelRadius, outerRadius,
                innerColorFor(color), color);
        }

        int cancelZoneColor = mouseDistanceFromCentre <= this.cancelZoneRadius
            ? Configurations.PIE_MENU_CANCEL_ZONE_HOVER_COLOR
            : Configurations.PIE_MENU_CANCEL_ZONE_COLOR;

        RingRenderer.drawCircle(context, this.centreX, this.centreY, 0, 360,
            totalSegments, animatedCancelRadius,
            cancelZoneColor);

        renderLabelTexts(context, numberOfSectors, progress);
    }

    /**
     * Returns the colour at the inner edge of a wedge.
     *
     * <p>With {@code SECTOR_GRADATION} off this is the flat wedge colour. With it on the
     * inner edge is tinted toward the centre light, so each wedge reads as slightly
     * rounded. The renderer already interpolates between the two colours per vertex, so
     * this costs nothing extra to draw.
     */
    private static int innerColorFor(int outerColor) {
        if (!Configurations.SECTOR_GRADATION) {
            return outerColor;
        }
        int alpha = (outerColor >>> 24) & 0xFF;
        int red = Math.min(255, ((outerColor >> 16) & 0xFF) + GRADATION_LIFT);
        int green = Math.min(255, ((outerColor >> 8) & 0xFF) + GRADATION_LIFT);
        int blue = Math.min(255, (outerColor & 0xFF) + GRADATION_LIFT);
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    private void renderLabelTexts(GuiGraphicsExtractor context, int numberOfSectors, float progress) {
        if (numberOfSectors == 0) return;

        // Labels only appear once the pie is most of the way open, so they do not pile up
        // on the centre while it is still growing.
        if (progress < 0.6f) {
            return;
        }

        Font textRenderer = Minecraft.getInstance().font;
        int margin = 2;

        for (int sectorIndex = 0; sectorIndex < numberOfSectors; sectorIndex++) {
            float sectorAngle = (float) (Mth.TWO_PI / numberOfSectors);

            // Labels always sit outside the pie, just clear of its edge.
            float textRadius = this.maxRadius * 1.1f + LABEL_GAP;
            float angle = (sectorIndex + 0.5f) * sectorAngle;

            float xPos = this.centreX + Mth.cos(angle) * textRadius;
            float yPos = this.centreY + Mth.sin(angle) * textRadius;

            String actionName = presentation.label(sectorIndex);

            int textWidth = textRenderer.width(actionName);
            int textHeight = textRenderer.lineHeight;

            // Clamp on both axes so a long label can never be cut off by the screen edge.
            if (xPos > this.centreX) {
                xPos -= margin;
            } else {
                xPos -= textWidth - margin;
            }
            xPos = Mth.clamp(xPos, margin, Math.max(margin, this.width - textWidth - margin));
            yPos -= textHeight / 2.0f;
            yPos = Mth.clamp(yPos, margin, Math.max(margin, this.height - textHeight - margin));

            int drawX = (int) xPos;
            int drawY = (int) yPos;

            if (selection.selectedIndex() == sectorIndex) {
                actionName = ChatFormatting.UNDERLINE + actionName;
                context.fill(drawX - 2, drawY - 2, drawX + textWidth + 2, drawY + textHeight + 2, 0x80E0E0E0);
            }

            if (Configurations.LABEL_TEXT_SHADOW) {
                context.text(textRenderer, actionName, drawX + SHADOW_OFFSET, drawY + SHADOW_OFFSET, SHADOW_COLOR, false);
            }
            context.text(textRenderer, actionName, drawX, drawY, 0xFFFFFFFF, false);
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
            context.fill(0, 0, this.width, this.height, Configurations.DARKENED_BACKGROUND_STRENGTH << 24);
        }
    }
}
