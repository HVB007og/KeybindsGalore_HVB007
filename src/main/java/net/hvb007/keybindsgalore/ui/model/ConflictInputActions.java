package net.hvb007.keybindsgalore.ui.model;

/**
 * Translates an abstract input action into selection changes on a {@link ConflictSelectionModel}.
 *
 * <p>Both selectors drive their selection through this type rather than through literal key
 * codes, so keyboard, controller, and any future input device share one implementation and one
 * set of behaviours. Nothing here touches Minecraft, which is what makes it unit-testable.
 *
 * <p>Navigation wraps by default. A conflict menu is a closed loop, so moving past the last
 * action lands on the first rather than dead-ending, which is what a player expects from a
 * physical dial and what makes holding a direction key work.
 */
public final class ConflictInputActions {
    /** An input event expressed as intent, independent of the device that produced it. */
    public enum Action {
        MOVE_PREVIOUS,
        MOVE_NEXT,
        COMMIT,
        CANCEL
    }

    /** What an {@link Action} did, so callers can react without diffing the model. */
    public enum Outcome {
        /** Nothing happened: already finalised, or nothing was selected to act on. */
        IGNORED,
        /** The selected action changed. */
        SELECTION_MOVED,
        /** Finalisation began and a selection was captured. */
        COMMITTED,
        /** Finalisation began with no selection, or the selection was cancelled. */
        CANCELLED
    }

    private final ConflictSelectionModel<?> model;
    private boolean wrapping = true;

    public ConflictInputActions(ConflictSelectionModel<?> model) {
        this.model = model;
    }

    public ConflictInputActions(ConflictSelectionModel<?> model, boolean wrapping) {
        this.model = model;
        this.wrapping = wrapping;
    }

    /**
     * Applies an action and reports what it did.
     *
     * <p>Committing with nothing selected is treated as a cancel, so a player who opens a menu
     * and presses Enter without moving cannot accidentally finalise an arbitrary action. The
     * selectors' existing behaviour of finalising on key release is unaffected; this only covers
     * explicit commit input.
     */
    public Outcome apply(Action action) {
        if (model.isFinalized()) {
            return Outcome.IGNORED;
        }
        switch (action) {
            case MOVE_PREVIOUS:
                return move(-1);
            case MOVE_NEXT:
                return move(1);
            case COMMIT:
                if (model.selectedIndex() < 0) {
                    return cancel();
                }
                return model.beginFinalization() ? Outcome.COMMITTED : Outcome.IGNORED;
            case CANCEL:
            default:
                return cancel();
        }
    }

    private Outcome move(int delta) {
        int size = model.size();
        if (size == 0) {
            return Outcome.IGNORED;
        }
        int current = model.selectedIndex();
        int next;
        if (current < 0) {
            // Nothing highlighted yet, so the first move highlights rather than skipping one.
            next = delta >= 0 ? 0 : size - 1;
        } else if (wrapping) {
            next = Math.floorMod(current + delta, size);
        } else {
            next = Math.clamp(current + delta, 0, size - 1);
        }
        if (next == current) {
            // Nothing actually moved. Reporting a move here would re-trigger the hover
            // animation and, once narration lands, make the narrator repeat itself.
            return Outcome.IGNORED;
        }
        model.select(next);
        return Outcome.SELECTION_MOVED;
    }

    private Outcome cancel() {
        if (!model.beginFinalization()) {
            return Outcome.IGNORED;
        }
        model.cancel();
        return Outcome.CANCELLED;
    }
}
