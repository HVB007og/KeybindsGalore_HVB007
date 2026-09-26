package net.hvb007.keybindsgalore.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ConfigurationMigrator {
    /**
     * Keys that used to be valid options and are now retired.
     *
     * <p>They are stripped during load so that an existing config file does not trip the
     * "no matching config field" error in {@code ConfigManager}, and so the next save
     * rewrites the file without them. The option each one replaced is still present, so
     * no user-visible setting is lost by retiring them.
     */
    private static final Set<String> RETIRED_KEYS = Set.of(
            "PIE_MENU_COLOR_LIGHTEN_FACTOR"
    );

    private ConfigurationMigrator() {
    }

    public static Map<String, String> migrate(Map<String, String> values) {
        Map<String, String> migrated = new LinkedHashMap<>(values);
        migrateFilterDebugKeys(migrated);
        dropRetiredKeys(migrated);
        return migrated;
    }

    private static void migrateFilterDebugKeys(Map<String, String> values) {
        String legacyValue = values.remove("FILTER_DEBUG_KEYS");
        if (legacyValue != null && !values.containsKey("FILTERED_CATEGORY_KEYS")) {
            values.put("FILTERED_CATEGORY_KEYS", Boolean.parseBoolean(legacyValue) ? "[Debug]" : "[]");
        }
    }

    private static void dropRetiredKeys(Map<String, String> values) {
        RETIRED_KEYS.forEach(values::remove);
    }
}
