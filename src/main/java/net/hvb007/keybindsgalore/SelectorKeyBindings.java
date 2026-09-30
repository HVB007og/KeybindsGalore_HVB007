package net.hvb007.keybindsgalore;

import com.mojang.blaze3d.platform.InputConstants;
import net.hvb007.keybindsgalore.ui.model.ConflictInputActions;

/**
 * Maps physical keys onto the abstract actions the selectors understand.
 *
 * <p>Kept apart from the input layer so the mapping can change without touching selection logic,
 * and so a controller can supply the same actions through a different route entirely.
 */
public final class SelectorKeyBindings {
    private SelectorKeyBindings() {
    }

    /**
     * @return the action for a key, or {@code null} when the key is not a navigation key and
     *         should be left to vanilla.
     */
    public static ConflictInputActions.Action toAction(InputConstants.Key key) {
        // 26.3 stores SDL scancodes, and the constant names do not all match the old GLFW ones.
        // KEY_RETURN is the name vanilla uses for the main Enter key; KEY_ENTER does not exist.
        int value = key.getValue();
        return switch (value) {
            case InputConstants.KEY_UP, InputConstants.KEY_W, InputConstants.KEY_LEFT,
                 InputConstants.KEY_A -> ConflictInputActions.Action.MOVE_PREVIOUS;
            // Tab moves forward here too, because it does that in every other Minecraft menu and
            // a player will try it without thinking. This screen has no focusable widgets, so
            // vanilla tab traversal has nothing to move and would otherwise do nothing at all.
            case InputConstants.KEY_DOWN, InputConstants.KEY_S, InputConstants.KEY_RIGHT,
                 InputConstants.KEY_D, InputConstants.KEY_TAB -> ConflictInputActions.Action.MOVE_NEXT;
            case InputConstants.KEY_RETURN, InputConstants.KEY_SPACE,
                 InputConstants.KEY_NUMPADENTER -> ConflictInputActions.Action.COMMIT;
            case InputConstants.KEY_ESCAPE -> ConflictInputActions.Action.CANCEL;
            default -> null;
        };
    }
}
