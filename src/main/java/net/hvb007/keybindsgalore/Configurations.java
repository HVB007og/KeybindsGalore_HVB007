package net.hvb007.keybindsgalore;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Holds all the configuration fields for the mod.
 * These fields are populated by the ConfigManager from the .properties file.
 */
public class Configurations {
    // --- General ---
    public static boolean DEBUG = false;

    // --- Performance ---
    public static boolean LAZY_CONFLICT_CHECK = true;
    public static int CIRCLE_VERTICES = 120;
    public static boolean PIE_MENU_BLEND = false;
    public static boolean DARKENED_BACKGROUND = true;
    public static boolean LABEL_TEXT_SHADOW = false;

    // --- Behaviour ---
    public static boolean USE_CIRCULAR_MENU = false;
    public static boolean ENABLE_ATTACK_WORKAROUND = true;
    public static ArrayList<String> FILTERED_CATEGORY_KEYS = new ArrayList<>(Arrays.asList("Debug"));
    public static ArrayList<Integer> IGNORED_KEYS = new ArrayList<>(Arrays.asList(340, 341, 87, 65, 83, 68, 32));
    public static boolean INVERT_IGNORED_KEYS_LIST = false;
    public static boolean USE_KEYBIND_FIX = true;
    public static int PULSE_TIMER_DURATION = 5;
    public static ArrayList<String> PRIORITY_CATEGORIES = new ArrayList<>(Arrays.asList("Movement"));
    public static ArrayList<String> PRIORITY_KEYBINDS = new ArrayList<>(Arrays.asList("key.attack", "key.use"));

    // --- Pie Menu Customisation ---
    public static float EXPANSION_FACTOR_WHEN_SELECTED = 0;
    public static int PIE_MENU_MARGIN = 0;
    public static float PIE_MENU_SCALE = 0.6f;
    public static float CANCEL_ZONE_SCALE = 0.25f;
    public static int PIE_MENU_COLOR = 0x00404040;
    public static int PIE_MENU_SELECT_COLOR = 0x00FFFFFF;
    public static int PIE_MENU_HIGHLIGHT_COLOR = 0x00EED202;
    public static int PIE_MENU_COLOR_LIGHTEN_FACTOR = 0x191919;
    public static short PIE_MENU_ALPHA = 0x40;
    public static boolean SECTOR_GRADATION = true;
    public static int LABEL_TEXT_INSET = 6;
    public static boolean ANIMATE_PIE_MENU = true;
}
