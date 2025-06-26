/*
 * Modified from the PSI mod by Vazkii (https://github.com/Vazkii/Psi)
 * Updated for KeybindsGalorePlus 1.21.x
 */
package me.av306.keybindsgaloreplus;

import static me.av306.keybindsgaloreplus.KeybindsGalorePlus.customDataManager;

import me.av306.keybindsgaloreplus.mixin.KeyBindingAccessor;
import me.av306.keybindsgaloreplus.mixin.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KeybindSelectorScreen extends Screen {
    // === Configurable Layout Constants ===
    private static final int BOX_HORIZONTAL_PADDING = 3;  // px on each side of text
    private static final int BOX_VERTICAL_PADDING   = 3;   // px above/below text
    private static final int BOX_SPACING            = 3;   // px between boxes
    private static final int SCREEN_MARGIN          = 50;  // px margin from screen edges

    // === Instance Fields ===
    private final MinecraftClient mc;
    private final InputUtil.Key conflictedKey;
    private final List<KeyBinding> conflicts     = new ArrayList<>();
    private final List<BoxDimensions> cachedBoxes = new ArrayList<>();

    private int     widthCenter, heightCenter;
    private boolean firstFrame = true;
    private int     selectedIndex = -1;
    private boolean mouseDown     = false;

    // Split-column fields
    private List<KeyBinding> topList, bottomList;
    private int halfCount, topStartY, bottomStartY;

    public KeybindSelectorScreen(InputUtil.Key key) {
        super(NarratorManager.EMPTY);
        this.mc = MinecraftClient.getInstance();
        this.conflictedKey = key;
        this.conflicts.addAll(KeybindManager.getConflicts(key));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        if (firstFrame) {
            widthCenter  = width  / 2;
            heightCenter = height / 2;
            calculateLayout();
            firstFrame = false;
        }
        updateSelection(mouseX, mouseY);
        renderMenu(ctx);
        renderLabels(ctx);
    }

    private void calculateLayout() {
        cachedBoxes.clear();
        halfCount   = conflicts.size() / 2;
        topList     = conflicts.subList(0, halfCount);
        bottomList  = conflicts.subList(halfCount, conflicts.size());

        int boxHeight = textRenderer.fontHeight + 2 * BOX_VERTICAL_PADDING;
        int maxWidth  = 0;
        for (KeyBinding kb : conflicts) {
            String name = formatName(kb);
            int textW   = textRenderer.getWidth(name);
            int totalW  = textW + 2 * BOX_HORIZONTAL_PADDING;
            maxWidth    = Math.max(maxWidth, totalW);
            BoxDimensions dim = new BoxDimensions();
            dim.height  = boxHeight;
            cachedBoxes.add(dim);
        }
        maxWidth = Math.min(maxWidth, width - 2 * SCREEN_MARGIN);
        for (BoxDimensions d : cachedBoxes) {
            d.finalWidth = maxWidth;
        }

        int topHeight = topList.size()    * boxHeight + (topList.size()    - 1) * BOX_SPACING;
//        int botHeight = bottomList.size() * boxHeight + (bottomList.size() - 1) * BOX_SPACING;
        topStartY     = heightCenter - BOX_SPACING/2 - topHeight;
        bottomStartY  = heightCenter + BOX_SPACING/2;
    }

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

    private void renderLabels(DrawContext ctx) {
        for (int i = 0; i < conflicts.size(); i++) {
            BoxDimensions dim = cachedBoxes.get(i);
            int baseY = (i < halfCount
                    ? topStartY + i * (dim.height + BOX_SPACING)
                    : bottomStartY + (i - halfCount) * (dim.height + BOX_SPACING));
            int x = widthCenter - (dim.finalWidth / 2);
            int y = baseY;
            if (selectedIndex == i) { x -= 2; y -= 1; }
            String name = formatName(conflicts.get(i));
            if (selectedIndex == i) { name = Formatting.UNDERLINE + name; }
            int tw = textRenderer.getWidth(name);
            ctx.drawText(textRenderer, name,
                    x + (dim.finalWidth - tw)/2,
                    y + (dim.height - textRenderer.fontHeight)/2,
                    0xFFFFFFFF, true);
        }
    }

    private void drawBox(DrawContext ctx, int x, int y, int w, int h, int idx) {
        int bg = Configurations.PIE_MENU_COLOR;
        if (customDataManager.hasCustomData) {
            try {
                bg = customDataManager.customData
                        .get(conflicts.get(idx).getTranslationKey()).sectorColor;
            } catch (Exception ignored) {}
        }
        if (selectedIndex == idx) {
            bg = mouseDown ? Configurations.PIE_MENU_HIGHLIGHT_COLOR
                    : Configurations.PIE_MENU_SELECT_COLOR;
            x -= 2; y -= 1; w += 4; h += 2;
        }
        int alpha = (Configurations.PIE_MENU_ALPHA << 24) | (bg & 0x00FFFFFF);
        ctx.fill(x, y, x + w, y + h, alpha);
        int border    = selectedIndex == idx ? 0x80FFFF00 : 0x80FFFFFF;
        int thickness = 1;
        for (int i = 0; i < thickness; i++) {
            ctx.fill(x - i, y - i, x + w + i, y - i + 1, border);
            ctx.fill(x - i, y + h + i - 1, x + w + i, y + h + i, border);
            ctx.fill(x - i, y - i, x - i + 1, y + h + i, border);
            ctx.fill(x + w + i - 1, y - i, x + w + i, y + h + i, border);
        }
    }

    private void updateSelection(int mx, int my) {
        selectedIndex = -1;
        for (int i = 0; i < conflicts.size(); i++) {
            BoxDimensions dim = cachedBoxes.get(i);
            int y0 = (i < halfCount)
                    ? topStartY + i * (dim.height + BOX_SPACING)
                    : bottomStartY + (i - halfCount) * (dim.height + BOX_SPACING);
            int x0 = widthCenter - (dim.finalWidth / 2);
            if (mx >= x0 && mx <= x0 + dim.finalWidth &&
                    my >= y0 && my <= y0 + dim.height) {
                selectedIndex = i;
                break;
            }
        }
    }

    private String formatName(KeyBinding kb) {
        String id   = kb.getTranslationKey();
        String name = Text.translatable(kb.getCategory()).getString()
                + ": " + Text.translatable(id).getString();
        if (customDataManager.hasCustomData) {
            try {
                if (customDataManager.customData.get(id).hideCategory)
                    name = Text.translatable(id).getString();
                name = Objects.requireNonNull(customDataManager.customData.get(id).displayName);
            } catch (Exception ignored) {}
        }
        return name;
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        mouseDown = true;
        return super.mouseClicked(x, y, btn);
    }

    @Override
    public boolean mouseReleased(double x, double y, int btn) {
        if (btn == conflictedKey.getCode()) {
            closeMenu();
        } else {
            mc.setScreen(null);
            KeyBinding.unpressAll();
            if (selectedIndex != -1) {
                KeyBinding kb = conflicts.get(selectedIndex);
                KeybindManager.clickHoldKeys.put(conflictedKey.getCode(), kb);
                if (conflictedKey.getCode() <= GLFW.GLFW_MOUSE_BUTTON_LAST)
                    kb.setPressed(true);
            } else {
                KeybindManager.clickHoldKeys.put(conflictedKey.getCode(), null);
            }
        }
        return super.mouseReleased(x, y, btn);
    }

    @Override
    public boolean keyReleased(int key, int scan, int mod) {
        if (key == conflictedKey.getCode()) closeMenu();
        return super.keyReleased(key, scan, mod);
    }

    private void closeMenu() {
        mc.setScreen(null);
        if (selectedIndex != -1) {
            KeyBinding kb = conflicts.get(selectedIndex);
            ((KeyBindingAccessor) kb).setPressed(true);
            ((KeyBindingAccessor) kb).setTimesPressed(1);
            if (kb.equals(mc.options.attackKey) && Configurations.ENABLE_ATTACK_WORKAROUND) {
                ((MinecraftClientAccessor) mc).setAttackCooldown(0);
            }
        }
    }

    @Override public boolean shouldPause() { return false; }

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
