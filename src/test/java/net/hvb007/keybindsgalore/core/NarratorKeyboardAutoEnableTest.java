package net.hvb007.keybindsgalore.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NarratorKeyboardAutoEnableTest {

    @Test
    void enablesForANarratorUserWhoCannotOtherwiseReachTheMenu() {
        assertTrue(NarratorKeyboardAutoEnable.shouldEnable(true, true, false));
    }

    @Test
    void doesNothingWhenKeyboardModeIsAlreadyOn() {
        // Would otherwise tell the player we changed a setting they had already chosen themselves.
        assertFalse(NarratorKeyboardAutoEnable.shouldEnable(true, true, true));
    }

    @Test
    void respectsTheOptOut() {
        assertFalse(NarratorKeyboardAutoEnable.shouldEnable(true, false, false));
    }

    @Test
    void onlyRespondsToNarratorAll() {
        // Chat and System are narrower than All and do not imply the player is navigating menus
        // by ear, so neither should silently change a gameplay setting.
        assertFalse(NarratorKeyboardAutoEnable.shouldEnable(false, true, false));
    }
}
