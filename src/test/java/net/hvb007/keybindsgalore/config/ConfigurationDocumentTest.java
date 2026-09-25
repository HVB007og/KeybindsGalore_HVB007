package net.hvb007.keybindsgalore.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationDocumentTest {
    @Test
    void parsesCommentsAndValuesInOrder() {
        ConfigurationDocument.ParseResult result = ConfigurationDocument.parse(List.of(
                "# comment",
                "",
                "debug=true",
                "IGNORED_KEYS=[1, 2]"
        ));

        assertEquals(List.of("DEBUG", "IGNORED_KEYS"), List.copyOf(result.values().keySet()));
        assertEquals("true", result.values().get("DEBUG"));
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void reportsEmptyKeysAndSkipsMalformedLines() {
        ConfigurationDocument.ParseResult result = ConfigurationDocument.parse(List.of(
                "=value",
                "missing-value"
        ));

        assertEquals(1, result.errors().size());
        assertTrue(result.values().isEmpty());
    }
}
