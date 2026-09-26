package net.hvb007.keybindsgalore.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ConfigurationSnapshotCodec {
    private ConfigurationSnapshotCodec() {
    }

    public static ConfigurationSnapshot fromMap(Map<String, String> values) {
        return fromMap(values, ConfigurationSnapshot.defaults());
    }

    public static ConfigurationSnapshot fromMap(Map<String, String> values, ConfigurationSnapshot fallback) {
        Map<String, String> merged = new LinkedHashMap<>(toMap(fallback));
        merged.putAll(values);
        return new ConfigurationSnapshot(
                booleanValue(merged, "DEBUG"),
                booleanValue(merged, "VERBOSE_DEBUG"),
                booleanValue(merged, "LAZY_CONFLICT_CHECK"),
                intValue(merged, "CIRCLE_VERTICES"),
                booleanValue(merged, "PIE_MENU_BLEND"),
                booleanValue(merged, "DARKENED_BACKGROUND"),
                intValue(merged, "DARKENED_BACKGROUND_STRENGTH"),
                booleanValue(merged, "LABEL_TEXT_SHADOW"),
                booleanValue(merged, "USE_CIRCULAR_MENU"),
                booleanValue(merged, "USE_SOFTWARE_RENDERING"),
                booleanValue(merged, "SHOW_CONFLICT_WARNINGS"),
                booleanValue(merged, "ENABLE_ATTACK_WORKAROUND"),
                stringList(merged, "FILTERED_CATEGORY_KEYS"),
                integerList(merged, "IGNORED_KEYS"),
                booleanValue(merged, "INVERT_IGNORED_KEYS_LIST"),
                booleanValue(merged, "USE_KEYBIND_FIX"),
                intValue(merged, "PULSE_TIMER_DURATION"),
                stringList(merged, "PRIORITY_CATEGORIES"),
                stringList(merged, "PRIORITY_KEYBINDS"),
                floatValue(merged, "EXPANSION_FACTOR_WHEN_SELECTED"),
                intValue(merged, "PIE_MENU_MARGIN"),
                floatValue(merged, "PIE_MENU_SCALE"),
                floatValue(merged, "CANCEL_ZONE_SCALE"),
                intValue(merged, "PIE_MENU_COLOR"),
                intValue(merged, "PIE_MENU_SELECT_COLOR"),
                intValue(merged, "PIE_MENU_HIGHLIGHT_COLOR"),
                intValue(merged, "PIE_MENU_SECTOR_COLOR_EVEN"),
                intValue(merged, "PIE_MENU_SECTOR_COLOR_ODD"),
                intValue(merged, "PIE_MENU_SECTOR_COLOR_SELECTED"),
                intValue(merged, "PIE_MENU_SECTOR_COLOR_LAST_ODD"),
                intValue(merged, "PIE_MENU_CANCEL_ZONE_COLOR"),
                intValue(merged, "PIE_MENU_CANCEL_ZONE_HOVER_COLOR"),
                intValue(merged, "PIE_MENU_COLOR_LIGHTEN_FACTOR"),
                shortValue(merged, "PIE_MENU_ALPHA"),
                booleanValue(merged, "SECTOR_GRADATION"),
                intValue(merged, "LABEL_TEXT_INSET"),
                booleanValue(merged, "ANIMATE_PIE_MENU")
        );
    }

    public static Map<String, String> toMap(ConfigurationSnapshot snapshot) {
        Map<String, String> values = new LinkedHashMap<>();
        put(values, "DEBUG", snapshot.debug());
        put(values, "VERBOSE_DEBUG", snapshot.verboseDebug());
        put(values, "LAZY_CONFLICT_CHECK", snapshot.lazyConflictCheck());
        put(values, "CIRCLE_VERTICES", snapshot.circleVertices());
        put(values, "PIE_MENU_BLEND", snapshot.pieMenuBlend());
        put(values, "DARKENED_BACKGROUND", snapshot.darkenedBackground());
        put(values, "DARKENED_BACKGROUND_STRENGTH", snapshot.darkenedBackgroundStrength());
        put(values, "LABEL_TEXT_SHADOW", snapshot.labelTextShadow());
        put(values, "USE_CIRCULAR_MENU", snapshot.useCircularMenu());
        put(values, "USE_SOFTWARE_RENDERING", snapshot.useSoftwareRendering());
        put(values, "SHOW_CONFLICT_WARNINGS", snapshot.showConflictWarnings());
        put(values, "ENABLE_ATTACK_WORKAROUND", snapshot.enableAttackWorkaround());
        put(values, "FILTERED_CATEGORY_KEYS", snapshot.filteredCategoryKeys());
        put(values, "IGNORED_KEYS", snapshot.ignoredKeys());
        put(values, "INVERT_IGNORED_KEYS_LIST", snapshot.invertIgnoredKeysList());
        put(values, "USE_KEYBIND_FIX", snapshot.useKeybindFix());
        put(values, "PULSE_TIMER_DURATION", snapshot.pulseTimerDuration());
        put(values, "PRIORITY_CATEGORIES", snapshot.priorityCategories());
        put(values, "PRIORITY_KEYBINDS", snapshot.priorityKeybinds());
        put(values, "EXPANSION_FACTOR_WHEN_SELECTED", snapshot.expansionFactorWhenSelected());
        put(values, "PIE_MENU_MARGIN", snapshot.pieMenuMargin());
        put(values, "PIE_MENU_SCALE", snapshot.pieMenuScale());
        put(values, "CANCEL_ZONE_SCALE", snapshot.cancelZoneScale());
        put(values, "PIE_MENU_COLOR", snapshot.pieMenuColor());
        put(values, "PIE_MENU_SELECT_COLOR", snapshot.pieMenuSelectColor());
        put(values, "PIE_MENU_HIGHLIGHT_COLOR", snapshot.pieMenuHighlightColor());
        put(values, "PIE_MENU_SECTOR_COLOR_EVEN", snapshot.pieMenuSectorColorEven());
        put(values, "PIE_MENU_SECTOR_COLOR_ODD", snapshot.pieMenuSectorColorOdd());
        put(values, "PIE_MENU_SECTOR_COLOR_SELECTED", snapshot.pieMenuSectorColorSelected());
        put(values, "PIE_MENU_SECTOR_COLOR_LAST_ODD", snapshot.pieMenuSectorColorLastOdd());
        put(values, "PIE_MENU_CANCEL_ZONE_COLOR", snapshot.pieMenuCancelZoneColor());
        put(values, "PIE_MENU_CANCEL_ZONE_HOVER_COLOR", snapshot.pieMenuCancelZoneHoverColor());
        put(values, "PIE_MENU_COLOR_LIGHTEN_FACTOR", snapshot.pieMenuColorLightenFactor());
        put(values, "PIE_MENU_ALPHA", snapshot.pieMenuAlpha());
        put(values, "SECTOR_GRADATION", snapshot.sectorGradation());
        put(values, "LABEL_TEXT_INSET", snapshot.labelTextInset());
        put(values, "ANIMATE_PIE_MENU", snapshot.animatePieMenu());
        return values;
    }

    public static List<String> toLines(ConfigurationSnapshot snapshot) {
        return toMap(snapshot).entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .toList();
    }

    public static boolean isKnownKey(String key) {
        return toMap(ConfigurationSnapshot.defaults()).containsKey(key);
    }

    private static void put(Map<String, String> values, String key, Object value) {
        values.put(key, ConfigurationCodec.formatRawValue(key, value));
    }

    private static boolean booleanValue(Map<String, String> values, String key) {
        return (Boolean) ConfigurationCodec.parseValue(boolean.class, null, values.get(key));
    }

    private static int intValue(Map<String, String> values, String key) {
        return (Integer) ConfigurationCodec.parseValue(int.class, null, values.get(key));
    }

    private static short shortValue(Map<String, String> values, String key) {
        return (Short) ConfigurationCodec.parseValue(short.class, null, values.get(key));
    }

    private static float floatValue(Map<String, String> values, String key) {
        return (Float) ConfigurationCodec.parseValue(float.class, null, values.get(key));
    }

    private static List<String> stringList(Map<String, String> values, String key) {
        return new ArrayList<>(ConfigurationCodec.parseStringList(values.get(key)));
    }

    private static List<Integer> integerList(Map<String, String> values, String key) {
        return new ArrayList<>(ConfigurationCodec.parseIntegerList(values.get(key)));
    }
}
