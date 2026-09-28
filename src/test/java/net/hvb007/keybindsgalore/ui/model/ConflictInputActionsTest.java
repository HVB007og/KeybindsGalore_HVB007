package net.hvb007.keybindsgalore.ui.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConflictInputActionsTest {
    private static ConflictSelectionModel<String> model(int size) {
        List<String> actions = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            actions.add("action" + i);
        }
        return new ConflictSelectionModel<>(actions);
    }

    @Test
    void firstMoveHighlightsWithoutSkippingAnEntry() {
        ConflictSelectionModel<String> model = model(3);
        ConflictInputActions input = new ConflictInputActions(model);

        assertEquals(-1, model.selectedIndex());
        assertEquals(ConflictInputActions.Outcome.SELECTION_MOVED, input.apply(ConflictInputActions.Action.MOVE_NEXT));
        assertEquals(0, model.selectedIndex());

        assertEquals(ConflictInputActions.Outcome.SELECTION_MOVED, input.apply(ConflictInputActions.Action.MOVE_NEXT));
        assertEquals(1, model.selectedIndex());
    }

    @Test
    void firstMovePreviousLandsOnTheLastEntry() {
        ConflictSelectionModel<String> model = model(3);
        ConflictInputActions input = new ConflictInputActions(model);

        input.apply(ConflictInputActions.Action.MOVE_PREVIOUS);

        assertEquals(2, model.selectedIndex());
    }

    @Test
    void navigationWrapsInBothDirections() {
        ConflictSelectionModel<String> model = model(3);
        ConflictInputActions input = new ConflictInputActions(model);

        // Three actions, so three forward moves walk 0 -> 1 -> 2 and the fourth wraps to 0.
        input.apply(ConflictInputActions.Action.MOVE_NEXT);
        input.apply(ConflictInputActions.Action.MOVE_NEXT);
        input.apply(ConflictInputActions.Action.MOVE_NEXT);
        assertEquals(2, model.selectedIndex());
        input.apply(ConflictInputActions.Action.MOVE_NEXT);
        assertEquals(0, model.selectedIndex(), "past the last entry should wrap to the first");

        input.apply(ConflictInputActions.Action.MOVE_PREVIOUS);
        assertEquals(2, model.selectedIndex(), "before the first entry should wrap to the last");
    }

    @Test
    void navigationCanClampInsteadOfWrapping() {
        ConflictSelectionModel<String> model = model(3);
        ConflictInputActions input = new ConflictInputActions(model, false);

        input.apply(ConflictInputActions.Action.MOVE_NEXT);
        input.apply(ConflictInputActions.Action.MOVE_PREVIOUS);
        assertEquals(0, model.selectedIndex());
        assertEquals(ConflictInputActions.Outcome.IGNORED, input.apply(ConflictInputActions.Action.MOVE_PREVIOUS));
    }

    @Test
    void commitFinalisesTheHighlightedAction() {
        ConflictSelectionModel<String> model = model(3);
        ConflictInputActions input = new ConflictInputActions(model);

        input.apply(ConflictInputActions.Action.MOVE_NEXT);
        assertEquals("action0", model.selected());
        assertEquals(ConflictInputActions.Outcome.COMMITTED, input.apply(ConflictInputActions.Action.COMMIT));
        assertTrue(model.isFinalized());
        assertEquals(0, model.selectedIndex(), "committing must not clear the selection");
    }

    @Test
    void commitWithNothingHighlightedCancelsRatherThanGuessing() {
        ConflictSelectionModel<String> model = model(3);
        ConflictInputActions input = new ConflictInputActions(model);

        assertEquals(ConflictInputActions.Outcome.CANCELLED, input.apply(ConflictInputActions.Action.COMMIT));
        assertTrue(model.isFinalized());
        assertEquals(-1, model.selectedIndex());
    }

    @Test
    void cancelFinalisesWithoutASelection() {
        ConflictSelectionModel<String> model = model(3);
        ConflictInputActions input = new ConflictInputActions(model);

        input.apply(ConflictInputActions.Action.MOVE_NEXT);
        assertEquals(ConflictInputActions.Outcome.CANCELLED, input.apply(ConflictInputActions.Action.CANCEL));
        assertTrue(model.isFinalized());
        assertEquals(-1, model.selectedIndex());
    }

    @Test
    void nothingIsAcceptedAfterFinalisation() {
        ConflictSelectionModel<String> model = model(3);
        ConflictInputActions input = new ConflictInputActions(model);

        input.apply(ConflictInputActions.Action.MOVE_NEXT);
        input.apply(ConflictInputActions.Action.COMMIT);

        for (ConflictInputActions.Action action : ConflictInputActions.Action.values()) {
            assertEquals(ConflictInputActions.Outcome.IGNORED, input.apply(action));
        }
        assertEquals(0, model.selectedIndex());
    }

    @Test
    void anEmptyMenuIgnoresMovement() {
        ConflictSelectionModel<String> model = model(0);
        ConflictInputActions input = new ConflictInputActions(model);

        assertEquals(ConflictInputActions.Outcome.IGNORED, input.apply(ConflictInputActions.Action.MOVE_NEXT));
        assertEquals(ConflictInputActions.Outcome.IGNORED, input.apply(ConflictInputActions.Action.MOVE_PREVIOUS));
        assertEquals(ConflictInputActions.Outcome.CANCELLED, input.apply(ConflictInputActions.Action.CANCEL));
    }

    @Test
    void aSingleEntryMenuStaysOnThatEntry() {
        ConflictSelectionModel<String> model = model(1);
        ConflictInputActions input = new ConflictInputActions(model);

        input.apply(ConflictInputActions.Action.MOVE_NEXT);
        assertEquals(0, model.selectedIndex());
        assertEquals(ConflictInputActions.Outcome.IGNORED, input.apply(ConflictInputActions.Action.MOVE_NEXT));
        assertEquals(0, model.selectedIndex());
    }

    @Test
    void singleEntryMenuCancelsOnCommitWithNoHighlight() {
        ConflictSelectionModel<String> model = model(1);
        ConflictInputActions input = new ConflictInputActions(model);

        // Nothing highlighted yet, so commit must not pick the only entry by accident.
        assertFalse(model.selectedIndex() >= 0);
        assertEquals(ConflictInputActions.Outcome.CANCELLED, input.apply(ConflictInputActions.Action.COMMIT));
        assertEquals(-1, model.selectedIndex());
    }
}
