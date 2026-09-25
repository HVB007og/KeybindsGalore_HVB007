/*
 * This file is heavily inspired by the PSI mod by Vazkii.
 */
package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.input.minecraft.SelectionActivationService;
import net.hvb007.keybindsgalore.ui.minecraft.ConflictActionPresentation;
import net.hvb007.keybindsgalore.ui.model.ConflictListLayout;
import net.hvb007.keybindsgalore.ui.model.ConflictSelectionModel;
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

import java.util.ArrayList;
import java.util.List;

import static net.hvb007.keybindsgalore.KeybindsGalore.customDataManager;

/**
 * The conflict resolution screen, which displays a list of keybindings
 * that share the same physical key.
 */
public class KeybindSelectorScreen extends Screen {
    private static final int BOX_HORIZONTAL_PADDING = 3;
    private static final int BOX_VERTICAL_PADDING = 3;
    private static final int BOX_SPACING = 3;
    private static final int SCREEN_MARGIN = 50;

    private final InputConstants.Key conflictedKey;
    private final List<KeyMapping> conflicts = new ArrayList<>();
    private final ConflictActionPresentation presentation;
    private final ConflictSelectionModel<KeyMapping> selection;
    private final List<ConflictListLayout.Box> cachedBoxes = new ArrayList<>();

    private boolean firstFrame = true;

    public KeybindSelectorScreen(InputConstants.Key key) {
        super(Component.empty());
        this.conflictedKey = key;
        this.conflicts.addAll(KeybindManager.getConflicts(key));
        this.presentation = new ConflictActionPresentation(this.conflicts, customDataManager);
        this.selection = new ConflictSelectionModel<>(this.conflicts);
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
        closeMenu();
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
            selection.cancel();
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
     */
    private void updateSelection(int mx, int my) {
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
        if (Configurations.DARKENED_BACKGROUND) {
            ctx.fill(0, 0, width, height, 0x60000000);
        }
    }

}
