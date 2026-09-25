package net.hvb007.keybindsgalore.ui.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConflictListLayoutTest {
    @Test
    void placesOddCountAcrossTwoRows() {
        ConflictListLayout layout = new ConflictListLayout();

        ConflictListLayout.Result result = layout.calculate(
                List.of("one", "two", "three"),
                100,
                80,
                400,
                10,
                3,
                3,
                3,
                50,
                label -> 10
        );

        assertEquals(1, result.halfCount());
        assertEquals(63, result.topStartY());
        assertEquals(81, result.bottomStartY());
        assertEquals(3, result.boxes().size());
        assertEquals(92, result.boxes().get(0).x());
        assertEquals(16, result.boxes().get(0).width());
        assertEquals(16, result.boxes().get(1).width());
    }

    @Test
    void returnsNoBoxesForNoActions() {
        ConflictListLayout.Result result = new ConflictListLayout().calculate(
                List.of(),
                100,
                80,
                400,
                10,
                3,
                3,
                3,
                50,
                String::length
        );

        assertEquals(List.of(), result.boxes());
        assertEquals(0, result.halfCount());
    }
}
