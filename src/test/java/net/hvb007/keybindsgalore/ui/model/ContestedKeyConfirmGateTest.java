package net.hvb007.keybindsgalore.ui.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContestedKeyConfirmGateTest {

    /**
     * The bug this guards: while the contested key is held, the operating system auto-repeats and
     * the mod sees every repeat as a press. Confirming on those closed the pie the instant it
     * opened, which is the flicker reported in game.
     */
    @Test
    void autoRepeatWhileHeldNeverConfirms() {
        ContestedKeyConfirmGate gate = new ContestedKeyConfirmGate();

        for (int i = 0; i < 40; i++) {
            assertFalse(gate.shouldConfirmOnPress(), "auto-repeat press " + i + " must not confirm");
        }
    }

    @Test
    void deliberateRepressAfterReleaseConfirmsExactlyOnce() {
        ContestedKeyConfirmGate gate = new ContestedKeyConfirmGate();

        gate.onRelease();

        assertTrue(gate.shouldConfirmOnPress());
        assertFalse(gate.shouldConfirmOnPress(), "the confirm must be consumed by that press");
    }

    @Test
    void alternatingPressAndReleaseIsTheDeliberateTapPattern() {
        ContestedKeyConfirmGate gate = new ContestedKeyConfirmGate();

        // Press opens the menu; auto-repeat presses while held do nothing.
        assertFalse(gate.shouldConfirmOnPress());
        assertFalse(gate.shouldConfirmOnPress());

        // Let go, then press again on purpose: that is the confirm.
        gate.onRelease();
        assertTrue(gate.shouldConfirmOnPress());
    }
}
