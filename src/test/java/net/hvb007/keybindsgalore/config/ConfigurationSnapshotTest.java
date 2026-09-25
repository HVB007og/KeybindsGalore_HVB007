package net.hvb007.keybindsgalore.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigurationSnapshotTest {
    @Test
    void exposesImmutableDefaults() {
        ConfigurationSnapshot defaults = ConfigurationSnapshot.defaults();

        assertEquals(List.of(), defaults.filteredCategoryKeys());
        assertEquals(List.of("Movement"), defaults.priorityCategories());
        assertEquals(7, defaults.priorityKeybinds().size());
        assertEquals((short) 0x40, defaults.pieMenuAlpha());
        assertThrows(UnsupportedOperationException.class,
                () -> defaults.filteredCategoryKeys().add("Other"));
    }
}
