/*
 * This file is heavily inspired by the PSI mod by Vazkii.
 */
package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.mixin.KeyBindingAccessor;
import net.hvb007.keybindsgalore.mixin.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private final InputUtil.Key conflictedKey;
    private final List<KeyBinding> conflicts = new ArrayList<>();
    private final List<BoxDimensions> cachedBoxes = new ArrayList<>();

    private int widthCenter, heightCenter;
    private boolean firstFrame = true;
    private int selectedIndex = -1;

    private List<KeyBinding> topList, bottomList;
    private int halfCount, topStartY, bottomStartY;

    public KeybindSelectorScreen(InputUtil.Key key) {
        super(NarratorManager.EMPTY);
        this.conflictedKey = key;
        this.conflicts.addAll(KeybindManager.getConflicts(key));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        if (firstFrame) {
            widthCenter = width / 2;
            heightCenter = height / 2;
            calculateLayout();
            firstFrame = false;
        }

        updateSelection(mouseX, mouseY);
        renderMenu(ctx);
        renderLabels(ctx);
    }

    /**
     * Called by the KeybindManager when the physical key is released.
     * This is the trigger to finalize the selection.
     */
    public void onKeyRelease() {
        handleSelectionFinish();
    }

    /**
     * Finalizes the keybind selection, activates the chosen keybind,
     * and closes the menu.
     */
    private void handleSelectionFinish() {
        if (selectedIndex != -1) {
            KeyBinding kb = conflicts.get(selectedIndex);

            // Set the chosen key as the "pulse target" to keep it pressed.
            KeybindsGalore.activePulseTarget = kb;
            KeybindsGalore.pulseTimer = 5;

            // Manually press the keybind to ensure it activates.
            ((KeyBindingAccessor) kb).setPressed(true);
            ((KeyBindingAccessor) kb).setTimesPressed(1);

            // Apply a workaround for the attack key if needed.
            if (kb.equals(MinecraftClient.getInstance().options.attackKey) && Configurations.ENABLE_ATTACK_WORKAROUND) {
                ((MinecraftClientAccessor) MinecraftClient.getInstance()).setAttackCooldown(0);
            }
        }
        // If no key was selected, we do nothing and let the KeybindManager handle the reset.

        closeMenu();
    }

    /**
     * Calculates the dimensions and positions of all the list items.
     */
    private void calculateLayout() {
        cachedBoxes.clear();
        halfCount = conflicts.size() / 2;
        topList = conflicts.subList(0, halfCount);
        bottomList = conflicts.subList(halfCount, conflicts.size());

        int boxHeight = textRenderer.fontHeight + 2 * BOX_VERTICAL_PADDING;
        int maxWidth = 0;
        for (KeyBinding kb : conflicts) {
            String name = formatName(kb);
            int textW = textRenderer.getWidth(name);
            int totalW = textW + 2 * BOX_HORIZONTAL_PADDING;
            maxWidth = Math.max(maxWidth, totalW);
            BoxDimensions dim = new BoxDimensions();
            dim.height = boxHeight;
            cachedBoxes.add(dim);
        }
        maxWidth = Math.min(maxWidth, width - 2 * SCREEN_MARGIN);
        for (BoxDimensions d : cachedBoxes) d.finalWidth = maxWidth;

        int topHeight = topList.size() * boxHeight + (topList.size() - 1) * BOX_SPACING;
        topStartY = heightCenter - BOX_SPACING / 2 - topHeight;
        bottomStartY = heightCenter + BOX_SPACING / 2;
    }

    /**
     * Renders the background boxes for each keybinding in the list.
     */
    private void renderMenu(DrawContext ctx) {
        for (int i = 0; i < topList.size(); i++) {
            BoxDimensions dim = cachedBoxes.get(i);
            int x = widthCenter - (dim.finalWidth / 2);
            int y = topStartY + i * (dim.height + BOX_SPACING);
            drawBox(ctx, x, y, dim.finalWidth, dim.height, i);
        }
        for (int i = 0; i < bottomList.size(); i++) {
            BoxDimensions dim = cachedBoxes.get(i + halfCount);
            int x = widthCenter - (dim.finalWidth / 2);
            int y = bottomStartY + i * (dim.height + BOX_SPACING);
            drawBox(ctx, x, y, dim.finalWidth, dim.height, i + halfCount);
        }
    }

    /**
     * Renders the text labels for each keybinding.
     */
    private void renderLabels(DrawContext ctx) {
        for (int i = 0; i < conflicts.size(); i++) {
            BoxDimensions dim = cachedBoxes.get(i);
            int baseY = (i < halfCount)
                    ? topStartY + i * (dim.height + BOX_SPACING)
                    : bottomStartY + (i - halfCount) * (dim.height + BOX_SPACING);
            int x = widthCenter - (dim.finalWidth / 2);
            int y = baseY;
            if (selectedIndex == i) {
                x -= 2;
                y -= 1;
            }
            String name = formatName(conflicts.get(i));
            if (selectedIndex == i) {
                name = Formatting.UNDERLINE + name;
            }
            int tw = textRenderer.getWidth(name);
            ctx.drawText(textRenderer, name,
                    x + (dim.finalWidth - tw) / 2,
                    y + (dim.height - textRenderer.fontHeight) / 2,
                    0xFFFFFFFF, true);
        }
    }

    private void drawBox(DrawContext ctx, int x, int y, int w, int h, int idx) {
        int bg = Configurations.PIE_MENU_COLOR;
        if (customDataManager.hasCustomData) {
            try {
                String key = KeybindManager.safeGetTranslationKey(conflicts.get(idx));
                bg = customDataManager.customData.get(key).sectorColor;
            } catch (Exception ignored) {
            }
        }
        if (selectedIndex == idx) {
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
        selectedIndex = -1;
        for (int i = 0; i < conflicts.size(); i++) {
            BoxDimensions dim = cachedBoxes.get(i);
            int y0 = (i < halfCount)
                    ? topStartY + i * (dim.height + BOX_SPACING)
                    : bottomStartY + (i - halfCount) * (dim.height + BOX_SPACING);
            int x0 = widthCenter - (dim.finalWidth / 2);
            if (mx >= x0 && mx <= x0 + dim.finalWidth && my >= y0 && my <= y0 + dim.height) {
                selectedIndex = i;
                break;
            }
        }
    }

    /**
     * Formats the name of a keybinding for display, including its category.
     */
    private String formatName(KeyBinding kb) {
        String id = KeybindManager.safeGetTranslationKey(kb);
        String cat = KeybindManager.safeGetCategory(kb);
        String name = Text.translatable(cat).getString() + ": " + Text.translatable(id).getString();
        if (customDataManager.hasCustomData) {
            try {
                if (customDataManager.customData.get(id).hideCategory)
                    name = Text.translatable(id).getString();
                name = Objects.requireNonNull(customDataManager.customData.get(id).displayName);
            } catch (Exception ignored) {
            }
        }
        return name;
    }

    private void closeMenu() {
        MinecraftClient.getInstance().setScreen(null);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float d) {
        if (Configurations.DARKENED_BACKGROUND) {
            ctx.fill(0, 0, width, height, 0x60000000);
        }
    }

    private static class BoxDimensions {
        int finalWidth, height;
    }
}
