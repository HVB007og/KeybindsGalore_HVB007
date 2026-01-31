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
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.hvb007.keybindsgalore.KeybindsGalore.customDataManager;

public class KeybindCircularScreen extends Screen {

    private final InputUtil.Key conflictedKey;
    private final List<KeyBinding> conflicts = new ArrayList<>();
    private int selectedIndex = -1;

    private long animationStartTime;
    private static final long ANIMATION_DURATION_MS = 200;

    public KeybindCircularScreen(InputUtil.Key key) {
        super(NarratorManager.EMPTY);
        this.conflictedKey = key;
        this.conflicts.addAll(KeybindManager.getConflicts(key));
        if (Configurations.ANIMATE_PIE_MENU) {
            this.animationStartTime = Util.getMeasuringTimeMs();
        } else {
            this.animationStartTime = -1;
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Manually draw darkened background to avoid "Can only blur once per frame" crash
        if (Configurations.DARKENED_BACKGROUND) {
            ctx.fill(0, 0, this.width, this.height, 0x60000000);
        }

        int centerX = width / 2;
        int centerY = height / 2;
        int numConflicts = conflicts.size();

        // Logic adapted from provided snippet
        double mouseAngle = mouseAngle(centerX, centerY, mouseX, mouseY);
        float anglePerSlice = (float) (Math.PI * 2 / numConflicts); // Radians

        float baseRadius = Configurations.PIE_MENU_SCALE * Math.min(width, height) / 2f;
        float currentRadius = baseRadius;

        if (Configurations.ANIMATE_PIE_MENU && animationStartTime != -1) {
            long elapsedTime = Util.getMeasuringTimeMs() - animationStartTime;
            float progress = (float) elapsedTime / ANIMATION_DURATION_MS;
            progress = MathHelper.clamp(progress, 0.0f, 1.0f);
            currentRadius = baseRadius * progress;
            if (progress >= 1.0f) {
                animationStartTime = -1;
            }
        }

        double dist = Math.sqrt(Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2));

        selectedIndex = -1;
        if (currentRadius > 0 && dist > currentRadius * Configurations.CANCEL_ZONE_SCALE) {
            // Calculate selected index based on angle
            // Angle is 0 to 2PI, starting at Right (3 o'clock)
            int seg = (int) (mouseAngle / anglePerSlice);
            if (seg >= 0 && seg < numConflicts) {
                selectedIndex = seg;
            }
        }

        // TODO: Implement proper pie slice rendering when BufferRenderer/BufferUploader API is clarified.
        // For now, we just draw the text and a highlight box.

        if (currentRadius > baseRadius * 0.5f) {
            for (int i = 0; i < numConflicts; i++) {
                Text name = formatName(conflicts.get(i));

                // Calculate position using radians as per snippet logic
                float rad = (i + 0.5f) * anglePerSlice;

                // Use a slightly larger radius for text to push it outside
                float textRadius = currentRadius * 1.1f;
                float xp = centerX + (float) Math.cos(rad) * textRadius;
                float yp = centerY + (float) Math.sin(rad) * textRadius;

                // Dynamic text alignment logic from snippet
                int textWidth = textRenderer.getWidth(name);
                int textHeight = textRenderer.fontHeight;

                // If left of center, shift left. If right, shift slightly left (padding).
                float textX = xp - (xp < centerX ? textWidth - 8 : 4);
                // If above center, shift up.
                float textY = yp - (yp < centerY ? 9 : 0);

                int color = 0xFFFFFFFF;
                if (i == selectedIndex) {
                    color = 0xFFFFFF00; // Yellow highlight
                    // Draw a small background box for the selected item
                    // Adjusted to match the dynamic text position
                    ctx.fill((int)textX - 2, (int)textY - 2, (int)textX + textWidth + 2, (int)textY + textHeight + 2, 0x80000000);
                }

                ctx.drawTextWithShadow(textRenderer, name, (int)textX, (int)textY, color);
            }
        }
    }

    private static double mouseAngle(int x, int y, int mx, int my) {
        return (Math.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }

    public void onKeyRelease() {
        handleSelectionFinish();
    }

    private void handleSelectionFinish() {
        if (selectedIndex != -1) {
            KeyBinding kb = conflicts.get(selectedIndex);
            KeybindsGalore.activePulseTarget = kb;
            KeybindsGalore.pulseTimer = 5;
            ((KeyBindingAccessor) kb).setPressed(true);
            ((KeyBindingAccessor) kb).setTimesPressed(1);
            if (kb.equals(MinecraftClient.getInstance().options.attackKey) && Configurations.ENABLE_ATTACK_WORKAROUND) {
                ((MinecraftClientAccessor) MinecraftClient.getInstance()).setAttackCooldown(0);
            }
        }
        closeMenu();
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

    private void closeMenu() {
        MinecraftClient.getInstance().setScreen(null);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
