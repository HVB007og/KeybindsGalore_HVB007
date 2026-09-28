/*
 * This file is heavily inspired by the PSI mod by Vazkii.
 */
package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.input.minecraft.SelectionActivationService;
import net.hvb007.keybindsgalore.ui.minecraft.ConflictActionPresentation;
import net.hvb007.keybindsgalore.ui.model.ConflictListLayout;
import net.hvb007.keybindsgalore.ui.model.ConflictInputActions;
import net.hvb007.keybindsgalore.ui.model.ConflictSelectionModel;
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

import java.util.ArrayList;
import java.util.List;

import static net.hvb007.keybindsgalore.KeybindsGalore.customDataManager;

/**
 * The conflict resolution screen, which displays a list of keybindings
 * that share the same physical key.
 */
public class KeybindSelectorScreen extends Screen {

    private static final int LIST_BACKGROUND_DIMMED = 0x60000000;
    private static final int BOX_HORIZONTAL_PADDING = 3;
    private static final int BOX_VERTICAL_PADDING = 3;
    private static final int BOX_SPACING = 3;
    private static final int SCREEN_MARGIN = 50;

    private final InputConstants.Key conflictedKey;
    private final List<KeyMapping> conflicts = new ArrayList<>();
    private final ConflictActionPresentation presentation;
    private final ConflictSelectionModel<KeyMapping> selection;
    private final List<ConflictListLayout.Box> cachedBoxes = new ArrayList<>();
    private final ConflictInputActions input;

    private boolean firstFrame = true;

    /**
     * True while the player is choosing with keys or a controller. The hover pass yields to it
     * so the highlight does not flicker back to the mouse position every frame.
     */
    private boolean keyboardFocus;

    public KeybindSelectorScreen(InputConstants.Key key) {
        super(Component.empty());
        this.conflictedKey = key;
        this.conflicts.addAll(KeybindManager.getConflicts(key));
        this.presentation = new ConflictActionPresentation(this.conflicts, customDataManager);
        this.selection = new ConflictSelectionModel<>(this.conflicts);
        this.input = new ConflictInputActions(this.selection);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        if (firstFrame) {
            calculateLayout(width / 2, height / 2);
            firstFrame = false;
        }

        updateSelection(mouseX, mouseY);
        renderMenu(ctx);
        renderLabels(ctx);
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
            KeybindsGalore.verboseLog("List selection CANCELLED for key {}: no row was hovered", key.getName());
        } else {
            KeybindsGalore.verboseLog("List selection FINALISED for key {}: chose row {} -> {}",
                    key.getName(), selection.selectedIndex(), selected.getName());
        }
        closeMenu();
        SelectionActivationService.activate(conflicts, selected);
        return true;
    }

    /**
     * Routes navigation keys through the shared input layer.
     *
     * <p>Only keys that are not the contested key are handled here. The contested key is
     * resolved on release, which is the mod's existing contract and must not change.
     */
    @Override
    public boolean keyPressed(KeyEvent event) {
        InputConstants.Key key = InputConstants.getKey(event);
        if (selection.isFinalized() || ownsKey(key)) {
            return super.keyPressed(event);
        }

        keyboardFocus = true;
        ConflictInputActions.Outcome outcome = input.apply(SelectorKeyBindings.toAction(key));
        if (outcome == ConflictInputActions.Outcome.IGNORED) {
            return super.keyPressed(event);
        }

        if (outcome == ConflictInputActions.Outcome.SELECTION_MOVED) {
            KeybindsGalore.verboseLog("List keyboard focus moved to row {} -> {}",
                    selection.selectedIndex(), selection.selected().getName());
            SelectionNarration.announceSelection(presentation, selection.selected());
        }
        return true;
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        return tryFinalize(InputConstants.getKey(event));
    }

    /** Hands selection back to the mouse once the player moves it again. */
    private void releaseKeyboardFocusOnMouseMove(int mouseX, int mouseY) {
        if (keyboardFocus && !pointerOverAnyBox(mouseX, mouseY)) {
            keyboardFocus = false;
        }
    }

    private boolean pointerOverAnyBox(int mx, int my) {
        for (ConflictListLayout.Box box : cachedBoxes) {
            if (mx >= box.x() && mx <= box.x() + box.width()
                    && my >= box.y() && my <= box.y() + box.height()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return tryFinalize(InputConstants.Type.MOUSE.getOrCreate(event.button()));
    }

    @Override
    protected void updateNarrationState(NarrationElementOutput output) {
        SelectionNarration.narrateOutput(presentation, selection.selected(), output);
    }

    @Override
    public void onClose() {
        if (!selection.isFinalized()) {
            KeybindsGalore.verboseLog("List selection CANCELLED for key {}: selector closed before finalisation", conflictedKey.getName());
            selection.cancel();
            SelectionNarration.announceCancel();
            SelectionActivationService.cancel(conflicts);
        }
        closeMenu();
    }

    private void calculateLayout(int centerX, int centerY) {
        ConflictListLayout.Result layout = new ConflictListLayout().calculate(
                presentation.labels(),
                centerX,
                centerY,
                width,
                font.lineHeight,
                BOX_HORIZONTAL_PADDING,
                BOX_VERTICAL_PADDING,
                BOX_SPACING,
                SCREEN_MARGIN,
                font::width
        );
        cachedBoxes.clear();
        cachedBoxes.addAll(layout.boxes());
    }

    /**
     * Renders the background boxes for each keybinding in the list.
     */
    private void renderMenu(GuiGraphicsExtractor ctx) {
        for (int i = 0; i < cachedBoxes.size(); i++) {
            ConflictListLayout.Box box = cachedBoxes.get(i);
            drawBox(ctx, box.x(), box.y(), box.width(), box.height(), i);
        }
    }

    /**
     * Renders the text labels for each keybinding.
     */
    private void renderLabels(GuiGraphicsExtractor ctx) {
        for (int i = 0; i < conflicts.size(); i++) {
            ConflictListLayout.Box box = cachedBoxes.get(i);
            int x = box.x();
            int y = box.y();
            if (selection.selectedIndex() == i) {
                x -= 2;
                y -= 1;
            }
            String name = presentation.label(i);
            if (selection.selectedIndex() == i) {
                name = ChatFormatting.UNDERLINE + name;
            }
            int tw = font.width(name);
            ctx.text(font, name,
                    x + (box.width() - tw) / 2,
                    y + (box.height() - font.lineHeight) / 2,
                    0xFFFFFFFF, true);
        }
    }

    private void drawBox(GuiGraphicsExtractor ctx, int x, int y, int w, int h, int idx) {
        int bg = presentation.color(idx, Configurations.PIE_MENU_COLOR);
        if (selection.selectedIndex() == idx) {
            bg = Configurations.PIE_MENU_HIGHLIGHT_COLOR;
            x -= 2;
            y -= 1;
            w += 4;
            h += 2;
        }
        int alpha = (Configurations.PIE_MENU_ALPHA << 24) | (bg & 0x00FFFFFF);
        ctx.fill(x, y, x + w, y + h, alpha);
    }

    /**
     * Updates the currently selected index based on the mouse position.
     *
     * <p>While the player is navigating by keyboard or controller the hover pass is skipped,
     * because it runs every frame and would otherwise reset the selection to nothing the moment
     * the mouse moved off a row, discarding the focus the player had just set. Moving the mouse
     * again hands control back to hover.
     */
    private void updateSelection(int mx, int my) {
        releaseKeyboardFocusOnMouseMove(mx, my);
        if (keyboardFocus) {
            return;
        }
        selection.select(-1);
        for (int i = 0; i < cachedBoxes.size(); i++) {
            ConflictListLayout.Box box = cachedBoxes.get(i);
            if (mx >= box.x() && mx <= box.x() + box.width()
                    && my >= box.y() && my <= box.y() + box.height()) {
                selection.select(i);
                break;
            }
        }
    }

    private void closeMenu() {
        Minecraft.getInstance().gui.setScreen(null);
    }

    @Override
    public void removed() {
        if (!selection.isFinalized()) {
            selection.cancel();
            SelectionActivationService.cancel(conflicts);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void renderBackground(GuiGraphicsExtractor ctx, int mx, int my, float d) {
        // The list menu keeps its own fixed darkening. DARKENED_BACKGROUND_STRENGTH is a
        // pie-menu option; the list menu is a different layout with a different contrast
        // problem, and tying the two together was rejected during testing.
        if (Configurations.DARKENED_BACKGROUND) {
            ctx.fill(0, 0, width, height, LIST_BACKGROUND_DIMMED);
        }
    }

}
