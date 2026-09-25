package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.config.ConfigurationSnapshot;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Holds all the configuration fields for the mod.
 * These fields are populated by the ConfigManager from the .properties file.
 */
public class Configurations {
    // --- General ---
    public static boolean DEBUG = false;
    public static boolean VERBOSE_DEBUG = false;

    // --- Performance ---
    public static boolean LAZY_CONFLICT_CHECK = true;
    public static int CIRCLE_VERTICES = 120;
    public static boolean PIE_MENU_BLEND = false;
    public static boolean DARKENED_BACKGROUND = true;
    public static boolean LABEL_TEXT_SHADOW = false;

    // --- Behaviour ---
    public static boolean USE_CIRCULAR_MENU = true;
    // Deprecated in 1.21.5 — Tesselator/BufferUploader removed by Mojang.
    // All GUI rendering now goes through BufferSource.getBuffer() with a RenderType layer.
    // This flag is kept for config compatibility but has no runtime effect.
    public static boolean USE_SOFTWARE_RENDERING = true;
    public static boolean SHOW_CONFLICT_WARNINGS = true; // Show conflict warnings in chat
    public static boolean ENABLE_ATTACK_WORKAROUND = true;
    public static ArrayList<String> FILTERED_CATEGORY_KEYS = new ArrayList<>(Arrays.asList("Debug"));
    public static ArrayList<Integer> IGNORED_KEYS = new ArrayList<>(Arrays.asList(340, 341, 87, 65, 83, 68, 32));
    public static boolean INVERT_IGNORED_KEYS_LIST = false;
    public static boolean USE_KEYBIND_FIX = true;
    public static int PULSE_TIMER_DURATION = 5;
    public static ArrayList<String> PRIORITY_CATEGORIES = new ArrayList<>(Arrays.asList("Movement"));
    public static ArrayList<String> PRIORITY_KEYBINDS = new ArrayList<>(Arrays.asList(
            "key.forward:key.keyboard.w",
            "key.left:key.keyboard.a",
            "key.back:key.keyboard.s",
            "key.right:key.keyboard.d",
            "key.jump:key.keyboard.space",
            "key.sneak:key.keyboard.left.shift",
            "key.sprint:key.keyboard.left.control"
    ));

    // --- Pie Menu Customisation ---
    public static float EXPANSION_FACTOR_WHEN_SELECTED = 0;
    public static int PIE_MENU_MARGIN = 0;
    public static float PIE_MENU_SCALE = 0.6f;
    public static float CANCEL_ZONE_SCALE = 0.25f;
    
    // Deprecated/Unused colors (kept for compatibility if needed, but we use specific ones now)
    public static int PIE_MENU_COLOR = 0x00404040; 
    public static int PIE_MENU_SELECT_COLOR = 0x00FFFFFF;
    public static int PIE_MENU_HIGHLIGHT_COLOR = 0x00EED202;
    
    // New Configurable Colors (Defaults with ~75% opacity C0)
    public static int PIE_MENU_SECTOR_COLOR_EVEN = 0xC0606060;
    public static int PIE_MENU_SECTOR_COLOR_ODD = 0xC0808080;
    public static int PIE_MENU_SECTOR_COLOR_SELECTED = 0xC0E0E0E0;
    public static int PIE_MENU_SECTOR_COLOR_LAST_ODD = 0xC0A0A0A0;
    public static int PIE_MENU_CANCEL_ZONE_COLOR = 0xC0000000;
    public static int PIE_MENU_CANCEL_ZONE_HOVER_COLOR = 0xC0B04232;

    public static int PIE_MENU_COLOR_LIGHTEN_FACTOR = 0x191919;
    public static short PIE_MENU_ALPHA = 0x40;
    public static boolean SECTOR_GRADATION = true;
    public static int LABEL_TEXT_INSET = 6;
    public static boolean ANIMATE_PIE_MENU = true;

    public static ConfigurationSnapshot snapshot() {
        return new ConfigurationSnapshot(
                DEBUG,
                VERBOSE_DEBUG,
                LAZY_CONFLICT_CHECK,
                CIRCLE_VERTICES,
                PIE_MENU_BLEND,
                DARKENED_BACKGROUND,
                LABEL_TEXT_SHADOW,
                USE_CIRCULAR_MENU,
                USE_SOFTWARE_RENDERING,
                SHOW_CONFLICT_WARNINGS,
                ENABLE_ATTACK_WORKAROUND,
                FILTERED_CATEGORY_KEYS,
                IGNORED_KEYS,
                INVERT_IGNORED_KEYS_LIST,
                USE_KEYBIND_FIX,
                PULSE_TIMER_DURATION,
                PRIORITY_CATEGORIES,
                PRIORITY_KEYBINDS,
                EXPANSION_FACTOR_WHEN_SELECTED,
                PIE_MENU_MARGIN,
                PIE_MENU_SCALE,
                CANCEL_ZONE_SCALE,
                PIE_MENU_COLOR,
                PIE_MENU_SELECT_COLOR,
                PIE_MENU_HIGHLIGHT_COLOR,
                PIE_MENU_SECTOR_COLOR_EVEN,
                PIE_MENU_SECTOR_COLOR_ODD,
                PIE_MENU_SECTOR_COLOR_SELECTED,
                PIE_MENU_SECTOR_COLOR_LAST_ODD,
                PIE_MENU_CANCEL_ZONE_COLOR,
                PIE_MENU_CANCEL_ZONE_HOVER_COLOR,
                PIE_MENU_COLOR_LIGHTEN_FACTOR,
                PIE_MENU_ALPHA,
                SECTOR_GRADATION,
                LABEL_TEXT_INSET,
                ANIMATE_PIE_MENU
        );
    }

    public static void apply(ConfigurationSnapshot snapshot) {
        DEBUG = snapshot.debug();
        VERBOSE_DEBUG = snapshot.verboseDebug();
        LAZY_CONFLICT_CHECK = snapshot.lazyConflictCheck();
        CIRCLE_VERTICES = snapshot.circleVertices();
        PIE_MENU_BLEND = snapshot.pieMenuBlend();
        DARKENED_BACKGROUND = snapshot.darkenedBackground();
        LABEL_TEXT_SHADOW = snapshot.labelTextShadow();
        USE_CIRCULAR_MENU = snapshot.useCircularMenu();
        USE_SOFTWARE_RENDERING = snapshot.useSoftwareRendering();
        SHOW_CONFLICT_WARNINGS = snapshot.showConflictWarnings();
        ENABLE_ATTACK_WORKAROUND = snapshot.enableAttackWorkaround();
        FILTERED_CATEGORY_KEYS = new ArrayList<>(snapshot.filteredCategoryKeys());
        IGNORED_KEYS = new ArrayList<>(snapshot.ignoredKeys());
        INVERT_IGNORED_KEYS_LIST = snapshot.invertIgnoredKeysList();
        USE_KEYBIND_FIX = snapshot.useKeybindFix();
        PULSE_TIMER_DURATION = snapshot.pulseTimerDuration();
        PRIORITY_CATEGORIES = new ArrayList<>(snapshot.priorityCategories());
        PRIORITY_KEYBINDS = new ArrayList<>(snapshot.priorityKeybinds());
        EXPANSION_FACTOR_WHEN_SELECTED = snapshot.expansionFactorWhenSelected();
        PIE_MENU_MARGIN = snapshot.pieMenuMargin();
        PIE_MENU_SCALE = snapshot.pieMenuScale();
        CANCEL_ZONE_SCALE = snapshot.cancelZoneScale();
        PIE_MENU_COLOR = snapshot.pieMenuColor();
        PIE_MENU_SELECT_COLOR = snapshot.pieMenuSelectColor();
        PIE_MENU_HIGHLIGHT_COLOR = snapshot.pieMenuHighlightColor();
        PIE_MENU_SECTOR_COLOR_EVEN = snapshot.pieMenuSectorColorEven();
        PIE_MENU_SECTOR_COLOR_ODD = snapshot.pieMenuSectorColorOdd();
        PIE_MENU_SECTOR_COLOR_SELECTED = snapshot.pieMenuSectorColorSelected();
        PIE_MENU_SECTOR_COLOR_LAST_ODD = snapshot.pieMenuSectorColorLastOdd();
        PIE_MENU_CANCEL_ZONE_COLOR = snapshot.pieMenuCancelZoneColor();
        PIE_MENU_CANCEL_ZONE_HOVER_COLOR = snapshot.pieMenuCancelZoneHoverColor();
        PIE_MENU_COLOR_LIGHTEN_FACTOR = snapshot.pieMenuColorLightenFactor();
        PIE_MENU_ALPHA = snapshot.pieMenuAlpha();
        SECTOR_GRADATION = snapshot.sectorGradation();
        LABEL_TEXT_INSET = snapshot.labelTextInset();
        ANIMATE_PIE_MENU = snapshot.animatePieMenu();
    }
}
