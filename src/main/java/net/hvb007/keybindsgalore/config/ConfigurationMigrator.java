package net.hvb007.keybindsgalore.config;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ConfigurationMigrator {
    private ConfigurationMigrator() {
    }

    public static Map<String, String> migrate(Map<String, String> values) {
        Map<String, String> migrated = new LinkedHashMap<>(values);
        migrateFilterDebugKeys(migrated);
        return migrated;
    }

    private static void migrateFilterDebugKeys(Map<String, String> values) {
        String legacyValue = values.remove("FILTER_DEBUG_KEYS");
        if (legacyValue != null && !values.containsKey("FILTERED_CATEGORY_KEYS")) {
            values.put("FILTERED_CATEGORY_KEYS", Boolean.parseBoolean(legacyValue) ? "[Debug]" : "[]");
        }
    }
}
