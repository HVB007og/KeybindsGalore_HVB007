package net.hvb007.keybindsgalore.config;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void dropsRetiredKeysSoOldConfigsDoNotTripTheUnknownKeyError() {
        Map<String, String> migrated = ConfigurationMigrator.migrate(
                Map.of("PIE_MENU_COLOR_LIGHTEN_FACTOR", "0x191919"));

        assertFalse(migrated.containsKey("PIE_MENU_COLOR_LIGHTEN_FACTOR"));
    }

    @Test
    void keepsEverySurvivingKeyRecognisableByTheSnapshot() {
        Map<String, String> old = new LinkedHashMap<>();
        old.put("PIE_MENU_COLOR_LIGHTEN_FACTOR", "0x191919");
        old.put("USE_CIRCULAR_MENU", "true");

        Map<String, String> migrated = ConfigurationMigrator.migrate(old);

        for (String key : migrated.keySet()) {
            assertTrue(ConfigurationSnapshotCodec.isKnownKey(key), key + " should be a known config key");
        }
    }
}
