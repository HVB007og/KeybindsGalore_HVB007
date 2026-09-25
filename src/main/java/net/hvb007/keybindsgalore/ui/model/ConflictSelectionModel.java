package net.hvb007.keybindsgalore.ui.model;

import java.util.List;
import java.util.Objects;

public final class ConflictSelectionModel<T> {
    private final List<T> actions;
    private int selectedIndex = -1;
    private boolean finalized;

    public ConflictSelectionModel(List<T> actions) {
        this.actions = List.copyOf(Objects.requireNonNull(actions, "actions"));
    }

    public int size() {
        return actions.size();
    }

    public void select(int index) {
        if (finalized) {
            return;
        }
        selectedIndex = index >= 0 && index < actions.size() ? index : -1;
    }

    public int selectedIndex() {
        return selectedIndex;
    }

    public T selected() {
        return selectedIndex < 0 ? null : actions.get(selectedIndex);
    }

    public boolean beginFinalization() {
        if (finalized) {
            return false;
        }
        finalized = true;
        return true;
    }

    public void cancel() {
        selectedIndex = -1;
        finalized = true;
    }

    public boolean isFinalized() {
        return finalized;
    }
}
