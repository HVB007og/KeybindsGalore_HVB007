package net.hvb007.keybindsgalore.core;

/** Which input path currently owns a physical key press. */
public enum InputOwnershipState {
    /** No mod-owned input is active. */
    IDLE,
    /** A conflict selector is on screen and awaiting a choice. */
    SELECTOR_OPEN,
    /** A choice was made and its action is being pulsed. */
    SELECTED,
    /** A configured priority action is holding the key. */
    PRIORITY_ACTIVE,
    /** A previously active input has ended. */
    RELEASED,
    /** A selection was abandoned without choosing. */
    CANCELLED
}
