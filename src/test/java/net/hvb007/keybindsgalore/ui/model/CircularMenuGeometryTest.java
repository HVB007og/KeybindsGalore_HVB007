package net.hvb007.keybindsgalore.ui.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CircularMenuGeometryTest {
    @Test
    void selectsSectorFromPointerAngle() {
        CircularMenuGeometry.Selection right = CircularMenuGeometry.select(10, 0, 2, 4);
        CircularMenuGeometry.Selection down = CircularMenuGeometry.select(0, 10, 2, 4);

        assertEquals(0, right.sectorIndex());
        assertEquals(1, down.sectorIndex());
    }

    @Test
    void centerSelectsCancelZone() {
        CircularMenuGeometry.Selection selection = CircularMenuGeometry.select(0, 0, 2, 4);

        assertEquals(-1, selection.sectorIndex());
        assertTrue(selection.cancelZone());
    }

    @Test
    void noSectorsSelectsCancel() {
        CircularMenuGeometry.Selection selection = CircularMenuGeometry.select(10, 0, 2, 0);

        assertEquals(-1, selection.sectorIndex());
        assertTrue(selection.cancelZone());
    }
}
