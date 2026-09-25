package net.hvb007.keybindsgalore.ui.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConflictSelectionModelTest {
    @Test
    void selectsAndFinalizesOneAction() {
        ConflictSelectionModel<String> model = new ConflictSelectionModel<>(List.of("a", "b"));

        model.select(1);

        assertEquals(1, model.selectedIndex());
        assertEquals("b", model.selected());
        assertTrue(model.beginFinalization());
        assertFalse(model.beginFinalization());
        assertTrue(model.isFinalized());
    }

    @Test
    void invalidSelectionClearsPreviousSelection() {
        ConflictSelectionModel<String> model = new ConflictSelectionModel<>(List.of("a"));
        model.select(0);
        model.select(4);

        assertEquals(-1, model.selectedIndex());
        assertNull(model.selected());
    }

    @Test
    void cancelFinalizesWithoutSelection() {
        ConflictSelectionModel<String> model = new ConflictSelectionModel<>(List.of("a"));
        model.select(0);
        model.cancel();

        assertEquals(-1, model.selectedIndex());
        assertNull(model.selected());
        assertTrue(model.isFinalized());
    }
}
