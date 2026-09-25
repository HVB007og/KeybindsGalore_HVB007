package net.hvb007.keybindsgalore.input.minecraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PulseControllerTest {
    @Test
    void ownsPulseDurationAndClearsItAfterExpiry() {
        PulseController controller = new PulseController();
        controller.start(null, 2);

        assertEquals(2, controller.ticksRemaining());
        controller.tick();
        assertEquals(1, controller.ticksRemaining());
        controller.tick();

        assertEquals(0, controller.ticksRemaining());
        assertNull(controller.target());
    }

    @Test
    void resetClearsPulseState() {
        PulseController controller = new PulseController();
        controller.start(null, 10);
        controller.reset();

        assertEquals(0, controller.ticksRemaining());
        assertNull(controller.target());
    }
}
