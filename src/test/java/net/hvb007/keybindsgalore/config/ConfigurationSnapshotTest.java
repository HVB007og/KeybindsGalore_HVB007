package net.hvb007.keybindsgalore.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationSnapshotTest {
    @Test
    void exposesImmutableDefaults() {
        ConfigurationSnapshot defaults = ConfigurationSnapshot.defaults();

        assertEquals(List.of(), defaults.filteredCategoryKeys());
        assertEquals(List.of("Movement"), defaults.priorityCategories());
        assertEquals(8, defaults.priorityKeybinds().size());
        assertEquals((short) 0x40, defaults.pieMenuAlpha());
        assertThrows(UnsupportedOperationException.class,
                () -> defaults.filteredCategoryKeys().add("Other"));
    }

    /**
     * defaults() is a positional argument list, so every boolean option is one interchangeable
     * compile-time error away from silently taking another option's default. That actually
     * happened while adding KEYBOARD_CONTROL_MODE: it landed before USE_CIRCULAR_MENU, which
     * defaulted the new mode on and the pie menu off, and the compiler could not see it. These
     * assertions exist to make that class of mistake loud.
     */
    @Test
    void pinsAdjacentBooleanDefaultsSoAPositionShiftCannotPassSilently() {
        ConfigurationSnapshot defaults = ConfigurationSnapshot.defaults();

        assertFalse(defaults.labelTextShadow());
        assertTrue(defaults.useCircularMenu());
        assertFalse(defaults.keyboardControlMode());
        assertTrue(defaults.narratorAutoEnable());
        assertTrue(defaults.showConflictWarnings());
        assertTrue(defaults.enableAttackWorkaround());
        assertTrue(defaults.sectorGradation());
    }

    /**
     * Keyboard control is opt-in, so an existing config that predates the option must come back
     * with it off rather than silently changing how the menu behaves.
     */
    @Test
    void keyboardControlModeIsOptIn() {
        assertFalse(ConfigurationSnapshot.defaults().keyboardControlMode());
    }

    /**
     * Middle mouse is a permanent vanilla conflict, so it must be resolved out of the box.
     *
     * <p>Vanilla binds both Pick Block and Spectator's Select On Hotbar to middle mouse. If this
     * entry is missing or misspelled, the conflict menu opens on every middle click, which is the
     * single most irritating way for the mod to introduce itself. The keys were read out of the
     * vanilla 26.3 language file rather than guessed, because a wrong action id silently never
     * matches and the entry would look present while doing nothing.
     */
    @Test
    void middleMouseDefaultsToPickBlockWinning() {
        assertTrue(ConfigurationSnapshot.defaults().priorityKeybinds()
                .contains("key.pickItem:key.mouse.middle"));
    }
}
