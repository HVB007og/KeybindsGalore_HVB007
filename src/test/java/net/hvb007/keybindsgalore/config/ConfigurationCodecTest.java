package net.hvb007.keybindsgalore.config;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigurationCodecTest {
    @SuppressWarnings("unused")
    private ArrayList<String> stringList;
    @SuppressWarnings("unused")
    private ArrayList<Integer> integerList;

    @Test
    void parsesScalarValues() {
        assertEquals((short) 64, ConfigurationCodec.parseValue(short.class, null, "0x40"));
        assertEquals(120, ConfigurationCodec.parseValue(int.class, null, "120"));
        assertEquals(0.6f, ConfigurationCodec.parseValue(float.class, null, "0.6"));
        assertEquals(true, ConfigurationCodec.parseValue(boolean.class, null, "true"));
    }

    @Test
    void parsesTypedLists() throws NoSuchFieldException {
        Field strings = getClass().getDeclaredField("stringList");
        Field integers = getClass().getDeclaredField("integerList");

        assertEquals(List.of("Debug", "Movement"), ConfigurationCodec.parseValue(strings.getType(), strings.getGenericType(), "[Debug, Movement]"));
        assertEquals(List.of(1, 2), ConfigurationCodec.parseValue(integers.getType(), integers.getGenericType(), "[1,2]"));
    }

    @Test
    void formatsValuesUsingExistingPropertyConventions() {
        assertEquals("PIE_MENU_COLOR=0xC0606060", ConfigurationCodec.formatValue("PIE_MENU_COLOR", 0xC0606060));
        assertEquals("PIE_MENU_ALPHA=64", ConfigurationCodec.formatValue("PIE_MENU_ALPHA", (short) 64));
        assertEquals("IGNORED_KEYS=[1, 2]", ConfigurationCodec.formatValue("IGNORED_KEYS", new ArrayList<>(List.of(1, 2))));
    }

    @Test
    void rejectsUnsupportedTypes() {
        assertThrows(IllegalArgumentException.class,
                () -> ConfigurationCodec.parseValue(String.class, null, "value"));
    }
}
