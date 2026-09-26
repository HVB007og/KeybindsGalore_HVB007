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
     *
     * <p>{@code LABEL_TEXT_INSET} is retired because labels now always sit a fixed
     * distance outside the pie and are clamped so they cannot be clipped.
     *
     * <p>{@code PIE_MENU_SELECT_COLOR} was superseded by
     * {@code PIE_MENU_SECTOR_COLOR_SELECTED}, which is what the hovered wedge actually
     * uses. {@code PIE_MENU_BLEND} duplicated what the per-sector colours already express.
     *
     * <p>{@code IGNORED_KEYS} and {@code INVERT_IGNORED_KEYS_LIST} were a
     * suppress-these-keys feature that conflict detection never consulted; prioritising
     * those keys achieves the same goal for the player, so the list is redundant.
     */
    private static final Set<String> RETIRED_KEYS = Set.of(
            "PIE_MENU_COLOR_LIGHTEN_FACTOR",
            "PIE_MENU_SELECT_COLOR",
            "PIE_MENU_BLEND",
            "LABEL_TEXT_INSET",
            "IGNORED_KEYS",
            "INVERT_IGNORED_KEYS_LIST"
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
