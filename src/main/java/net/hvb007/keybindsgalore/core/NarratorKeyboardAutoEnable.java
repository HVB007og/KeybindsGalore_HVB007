package net.hvb007.keybindsgalore.core;

/**
 * Decides whether Keyboard Control Mode should be turned on for a player using a screen reader.
 *
 * <p>A narrator is only useful if the player can hear the menu change, and the menu can only be
 * changed by keyboard when the option is on. A screen-reader user with the option off therefore
 * gets a menu that opens silently and cannot be moved, which is the exact situation narration was
 * added to fix. Turning the option on for them removes that contradiction.
 *
 * <p>Kept as a pure predicate so the rule is unit-tested rather than only observable in game.
 * Callers supply the three facts because reading the narrator setting needs Minecraft.
 */
public final class NarratorKeyboardAutoEnable {
    private NarratorKeyboardAutoEnable() {
    }

    /**
     * @param narratorSetToAll    the player chose Narrator "All", not just Chat or System
     * @param optionEnabled       our own opt-out is still on; it is cleared after the first run so
     *                            this fires once rather than on every world join
     * @param keyboardModeIsOn    the player already has Keyboard Control Mode on, so there is
     *                            nothing to do and no reason to claim we changed something
     */
    public static boolean shouldEnable(boolean narratorSetToAll, boolean optionEnabled,
                                       boolean keyboardModeIsOn) {
        return narratorSetToAll && optionEnabled && !keyboardModeIsOn;
    }
}
