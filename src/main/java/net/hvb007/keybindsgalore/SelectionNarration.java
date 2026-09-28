package net.hvb007.keybindsgalore;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.hvb007.keybindsgalore.ui.minecraft.ConflictActionPresentation;

/**
 * Speaks the selection so a player using a screen reader can follow the menu.
 *
 * <p>This exists because the mod is currently worse for those players, not neutral to them:
 * a menu that opens silently is a menu they cannot perceive and cannot diagnose, and vanilla's
 * usual "you pressed a key and something happened" feedback is exactly what the conflict
 * intercept removes.
 */
public final class SelectionNarration {
    private SelectionNarration() {
    }

    public static void announceSelection(ConflictActionPresentation presentation, KeyMapping selected) {
        if (selected == null) {
            return;
        }
        speak(labelOf(presentation, selected));
    }

    public static void announceCommit(KeyMapping selected) {
        if (selected == null) {
            return;
        }
        speak("Chose " + selected.getName());
    }

    public static void announceCancel() {
        speak("Selection cancelled");
    }

    /**
     * Adds the current highlight to a screen's narration output, so a screen reader reads the
     * focused action on its own rather than only when it changes.
     */
    public static void narrateOutput(ConflictActionPresentation presentation, KeyMapping selected,
                                     NarrationElementOutput output) {
        if (selected == null) {
            return;
        }
        output.add(NarratedElementType.TITLE, labelOf(presentation, selected));
    }

    /** Uses the custom display name when one is configured, matching what is drawn on screen. */
    private static String labelOf(ConflictActionPresentation presentation, KeyMapping selected) {
        for (int i = 0; i < presentation.size(); i++) {
            if (presentation.action(i) == selected) {
                return presentation.label(i);
            }
        }
        return selected.getName();
    }

    private static void speak(String text) {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.getNarrator() == null) {
            return;
        }
        // Narration is a no-op when no narrator is active, so calling this is always safe.
        client.getNarrator().saySystemNow(Component.literal(text));
    }
}
