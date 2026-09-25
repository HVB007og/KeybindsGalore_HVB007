package net.hvb007.keybindsgalore.config;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ConfigurationMigratorTest {
    @Test
    void migratesLegacyDebugFilterWhenEnabled() {
        Map<String, String> migrated = ConfigurationMigrator.migrate(Map.of("FILTER_DEBUG_KEYS", "true"));

        assertEquals("[Debug]", migrated.get("FILTERED_CATEGORY_KEYS"));
        assertFalse(migrated.containsKey("FILTER_DEBUG_KEYS"));
    }

    @Test
    void migratesLegacyDebugFilterWhenDisabled() {
        Map<String, String> migrated = ConfigurationMigrator.migrate(Map.of("FILTER_DEBUG_KEYS", "false"));

        assertEquals("[]", migrated.get("FILTERED_CATEGORY_KEYS"));
    }

    @Test
    void doesNotOverwriteCurrentCategoryFilter() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("FILTER_DEBUG_KEYS", "true");
        values.put("FILTERED_CATEGORY_KEYS", "[Movement]");

        Map<String, String> migrated = ConfigurationMigrator.migrate(values);

        assertEquals("[Movement]", migrated.get("FILTERED_CATEGORY_KEYS"));
    }
}
