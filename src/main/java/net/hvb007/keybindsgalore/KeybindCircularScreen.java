package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.ui.model.CircularMenuGeometry;
import net.hvb007.keybindsgalore.ui.model.ConflictInputActions;
import net.hvb007.keybindsgalore.ui.model.ContestedKeyConfirmGate;
import net.hvb007.keybindsgalore.ui.model.ConflictSelectionModel;
import net.hvb007.keybindsgalore.input.minecraft.SelectionActivationService;
import net.hvb007.keybindsgalore.ui.minecraft.ConflictActionPresentation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
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
    // Longest open animation accepted, in milliseconds.
    private static final int MAX_ANIMATION_MILLIS = 2000;

    private final InputConstants.Key conflictedKey;
    private final List<KeyMapping> conflicts = new ArrayList<>();
    private final ConflictActionPresentation presentation;
    private final ConflictSelectionModel<KeyMapping> selection;
    private final ConflictInputActions input;

    private int centreX = 0, centreY = 0;
    private float maxRadius = 0;
    private float cancelZoneRadius = 0;
    private int lastHoveredSector = Integer.MIN_VALUE;
    private long openedAtNanos;

    /**
     * True while the player is choosing with keys or a controller, so the per-frame hover pass
     * yields instead of overwriting the highlight.
     */
    private boolean keyboardFocus;
    private final ContestedKeyConfirmGate confirmGate = new ContestedKeyConfirmGate();

    public KeybindCircularScreen(InputConstants.Key key) {
        super(Component.empty());
        this.conflictedKey = key;
        this.conflicts.addAll(KeybindManager.getConflicts(key));
        this.presentation = new ConflictActionPresentation(this.conflicts, customDataManager);
        this.selection = new ConflictSelectionModel<>(this.conflicts);
        this.input = new ConflictInputActions(this.selection);
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
        long duration = Math.max(0, Math.min(MAX_ANIMATION_MILLIS, Configurations.ANIMATION_DURATION));
        if (!Configurations.ANIMATE_PIE_MENU || duration == 0L) {
            return 1.0f;
        }
        long elapsed = (System.nanoTime() - this.openedAtNanos) / 1_000_000L;
        if (elapsed >= duration) {
            return 1.0f;
        }
        float t = (float) elapsed / (float) duration;
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
        // While the player is navigating by keys or a controller the hover pass is skipped.
        // It runs every frame and would otherwise overwrite the highlight the moment the mouse
        // sat outside a wedge, discarding the choice being made.
        int hoverSector = keyboardFocus ? this.selection.selectedIndex() : geometrySelection.sectorIndex();
        this.selection.select(hoverSector);
        if (hoverSector != this.lastHoveredSector) {
            this.lastHoveredSector = hoverSector;
            if (hoverSector < 0) {
                KeybindsGalore.verboseLog("Pie hover: cancel zone for key {}", this.conflictedKey.getName());
            } else {
                KeybindsGalore.verboseLog("Pie hover: sector {} -> {}",
                        hoverSector, this.presentation.label(hoverSector));
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
        if (!Configurations.SECTOR_GRADATION || Configurations.GRADATION_INTENSITY <= 0) {
            return outerColor;
        }
        // Blend toward white rather than adding a flat offset, so the intensity slider
        // means the same thing for a dark wedge as for a pale one and saturates cleanly
        // instead of clipping one channel at a time.
        float amount = Math.min(1.0f, Configurations.GRADATION_INTENSITY / 100.0f);
        int alpha = (outerColor >>> 24) & 0xFF;
        int red = Math.round(((outerColor >> 16) & 0xFF) + (255 - ((outerColor >> 16) & 0xFF)) * amount);
        int green = Math.round(((outerColor >> 8) & 0xFF) + (255 - ((outerColor >> 8) & 0xFF)) * amount);
        int blue = Math.round((outerColor & 0xFF) + (255 - (outerColor & 0xFF)) * amount);
        return (alpha << 24) | (clampChannel(red) << 16) | (clampChannel(green) << 8) | clampChannel(blue);
    }

    private static int clampChannel(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private void renderLabelTexts(GuiGraphicsExtractor context, int numberOfSectors, float progress) {
        if (numberOfSectors == 0) return;

        // Labels appear only once the pie has finished opening. Fading them in with the
        // animation looked wrong, because the text slides outward while the wedge it
        // belongs to is still growing.
        if (progress < 1.0f) {
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
        completeFinalization(key);
        return true;
    }

    /**
     * Activates the chosen sector and closes the pie. Split out of {@link #tryFinalize} because
     * the keyboard-control commit path has already marked the model finalised, and the guard in
     * tryFinalize would otherwise reject it and leave the menu stuck open.
     */
    private void completeFinalization(InputConstants.Key key) {
        KeyMapping selected = selection.selected();
        if (selected == null) {
            KeybindsGalore.verboseLog("Pie selection CANCELLED for key {}: nothing was highlighted", key.getName());
            SelectionActivationService.cancel(conflicts);
        } else {
            KeybindsGalore.verboseLog("Pie selection FINALISED for key {}: chose sector {} -> {}",
                    key.getName(), selection.selectedIndex(), selected.getName());
            SelectionActivationService.activate(conflicts, selected);
        }
        Minecraft.getInstance().gui.setScreen(null);
    }

    /**
     * Routes navigation keys through the shared input layer.
     *
     * <p>The contested key is excluded, because it is resolved on release. That contract is the
     * most delicate path in the mod and must not change.
     */
    @Override
    public boolean keyPressed(KeyEvent event) {
        InputConstants.Key key = InputConstants.getKey(event);
        if (selection.isFinalized()) {
            return super.keyPressed(event);
        }

        if (ownsKey(key)) {
            // In keyboard control mode the contested key doubles as confirm, so a player can
            // commit with the same finger that opened the menu. It only counts once a real
            // release has been seen, otherwise OS auto-repeat would close the menu instantly.
            if (!Configurations.KEYBOARD_CONTROL_MODE) {
                return super.keyPressed(event);
            }
            return confirmGate.shouldConfirmOnPress() ? confirmAndClose() : true;
        }

        ConflictInputActions.Action action = SelectorKeyBindings.toAction(key);
        if (action == null) {
            return super.keyPressed(event);
        }

        // Keyboard nav is opt-in. When the option is off these keys must be inert so the menu
        // cannot be driven without consent, and so movement keys are not swallowed.
        if (!Configurations.KEYBOARD_CONTROL_MODE) {
            return super.keyPressed(event);
        }

        if (action == ConflictInputActions.Action.COMMIT) {
            return confirmAndClose();
        }
        if (action == ConflictInputActions.Action.CANCEL) {
            return cancelAndClose();
        }

        keyboardFocus = true;
        ConflictInputActions.Outcome outcome = input.apply(action);
        if (outcome == ConflictInputActions.Outcome.SELECTION_MOVED) {
            int sector = selection.selectedIndex();
            KeybindsGalore.verboseLog("Pie keyboard focus: sector {} -> {}",
                    sector, sector >= 0 ? this.presentation.label(sector) : "cancel zone");
            triggerImmediateNarration(false);
        }
        // IGNORED is a deliberate no-op, for example moving in a single-sector menu. Swallowing
        // the key there would stop the player from reaching vanilla, so report unhandled.
        return outcome != ConflictInputActions.Outcome.IGNORED;
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        InputConstants.Key key = InputConstants.getKey(event);
        if (ownsKey(key)) {
            confirmGate.onRelease();
            // In mouse mode the release is the commit gesture and must keep working. In keyboard
            // mode it is only evidence that the player let go, so the pie stays up and the
            // highlighted sector is preserved for the next deliberate key press.
            if (Configurations.KEYBOARD_CONTROL_MODE) {
                return true;
            }
            return tryFinalize(key);
        }
        return super.keyReleased(event);
    }

    /** Commits the highlighted sector and closes, used by the keyboard-control confirm paths. */
    private boolean confirmAndClose() {
        ConflictInputActions.Outcome outcome = input.apply(ConflictInputActions.Action.COMMIT);
        if (outcome == ConflictInputActions.Outcome.IGNORED) {
            return false;
        }
        if (outcome == ConflictInputActions.Outcome.CANCELLED) {
            Minecraft.getInstance().gui.setScreen(null);
            return true;
        }
        completeFinalization(conflictedKey);
        return true;
    }

    /** Abandons the selection and closes, used by the keyboard-control cancel path. */
    private boolean cancelAndClose() {
        if (input.apply(ConflictInputActions.Action.CANCEL) == ConflictInputActions.Outcome.IGNORED) {
            return false;
        }
        KeybindsGalore.verboseLog("Pie selection CANCELLED for key {}: cancel key pressed", conflictedKey.getName());
        SelectionNarration.announceCancel();
        Minecraft.getInstance().gui.setScreen(null);
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return tryFinalize(InputConstants.Type.MOUSE.getOrCreate(event.button()));
    }

    @Override
    protected void updateNarrationState(NarrationElementOutput output) {
        SelectionNarration.narrateOutput(presentation, selection.selected(), conflictedKey.getName(), output);
    }

    @Override
    public void onClose() {
        if (!selection.isFinalized()) {
            KeybindsGalore.verboseLog("Pie selection CANCELLED for key {}: selector closed without releasing over a sector", conflictedKey.getName());
            selection.cancel();
            SelectionNarration.announceCancel();
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
