package net.hvb007.keybindsgalore.core;

/**
 * Tracks which part of the mod currently owns physical input.
 *
 * <p>Platform-neutral and side-effect free: the caller performs the actual Minecraft
 * work, this only records the ownership decision so that competing paths (selector,
 * direct priority, pulse) can be reasoned about and tested in isolation.
 */
public final class InputOwnershipStateMachine {
    private InputOwnershipState state = InputOwnershipState.IDLE;

    public InputOwnershipState state() {
        return state;
    }

    public void selectorOpened() {
        state = InputOwnershipState.SELECTOR_OPEN;
    }

    public void selectionMade() {
        if (state == InputOwnershipState.SELECTOR_OPEN || state == InputOwnershipState.SELECTED) {
            state = InputOwnershipState.SELECTED;
        }
    }

    public void priorityActivated() {
        state = InputOwnershipState.PRIORITY_ACTIVE;
    }

    public void released() {
        if (state != InputOwnershipState.IDLE) {
            state = InputOwnershipState.RELEASED;
        }
    }

    public void cancelled() {
        state = InputOwnershipState.CANCELLED;
    }

    public void reset() {
        state = InputOwnershipState.IDLE;
    }
}
