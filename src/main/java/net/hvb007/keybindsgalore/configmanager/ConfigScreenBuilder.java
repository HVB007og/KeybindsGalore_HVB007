package net.hvb007.keybindsgalore.configmanager;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.hvb007.keybindsgalore.Configurations;
import net.hvb007.keybindsgalore.KeybindsGalore;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

/**
 * Builds the ModMenu and Cloth Config settings screen.
 *
 * <p>Entries are grouped into three tabs. Anything that only changes how the pie menu
 * looks or feels belongs in the Visual tab, including its geometry and text options;
 * anything that changes conflict detection or priority resolution belongs in Behaviour.
 */
public class ConfigScreenBuilder {

    public static Screen buildConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.keybindsgalore.config"))
                .setSavingRunnable(KeybindsGalore::saveConfigAndRefresh);

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("category.keybindsgalore.general"));
        ConfigCategory behavior = builder.getOrCreateCategory(Component.translatable("category.keybindsgalore.behaviour"));
        ConfigCategory visual = builder.getOrCreateCategory(Component.translatable("category.keybindsgalore.visual"));
        ConfigCategory listVisual = builder.getOrCreateCategory(Component.translatable("category.keybindsgalore.visual_list"));

        ConfigEntryBuilder eb = builder.entryBuilder();

        addGeneral(eb, general);
        addBehavior(eb, behavior);
        addPieLayout(eb, visual);
        addPieColors(eb, visual);
        addPieEffects(eb, visual);
        addPieLabels(eb, visual);
        addListVisual(eb, listVisual);
        return builder.build();
    }

    private static void addGeneral(ConfigEntryBuilder eb, ConfigCategory general) {
        general.addEntry(eb.startBooleanToggle(Component.translatable("option.keybindsgalore.debug"), Configurations.DEBUG)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("option.keybindsgalore.debug.tooltip"))
                .setSaveConsumer(v -> Configurations.DEBUG = v)
                .build());

        general.addEntry(eb.startBooleanToggle(Component.translatable("option.keybindsgalore.verbose_debug"), Configurations.VERBOSE_DEBUG)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("option.keybindsgalore.verbose_debug.tooltip"))
                .setSaveConsumer(v -> Configurations.VERBOSE_DEBUG = v)
                .build());

        general.addEntry(eb.startBooleanToggle(Component.translatable("option.keybindsgalore.lazy_conflict_check"), Configurations.LAZY_CONFLICT_CHECK)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("option.keybindsgalore.lazy_conflict_check.tooltip"))
                .setSaveConsumer(v -> Configurations.LAZY_CONFLICT_CHECK = v)
                .build());

        general.addEntry(eb.startIntSlider(Component.translatable("option.keybindsgalore.pulse_timer_duration"), Configurations.PULSE_TIMER_DURATION, 0, 60)
                .setDefaultValue(5)
                .setTooltip(Component.translatable("option.keybindsgalore.pulse_timer_duration.tooltip"))
                .setSaveConsumer(v -> Configurations.PULSE_TIMER_DURATION = v)
                .build());

        general.addEntry(eb.startBooleanToggle(Component.translatable("option.keybindsgalore.use_keybind_fix"), Configurations.USE_KEYBIND_FIX)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("option.keybindsgalore.use_keybind_fix.tooltip"))
                .setSaveConsumer(v -> Configurations.USE_KEYBIND_FIX = v)
                .build());

        general.addEntry(eb.startBooleanToggle(Component.translatable("option.keybindsgalore.enable_attack_workaround"), Configurations.ENABLE_ATTACK_WORKAROUND)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("option.keybindsgalore.enable_attack_workaround.tooltip"))
                .setSaveConsumer(v -> Configurations.ENABLE_ATTACK_WORKAROUND = v)
                .build());

        // One control only. Both selectors read DARKENED_BACKGROUND and
        // DARKENED_BACKGROUND_STRENGTH, so a second toggle in the List tab would let the
        // two widgets disagree and silently revert each other on save.
        general.addEntry(eb.startBooleanToggle(Component.translatable("option.keybindsgalore.darkened_background"), Configurations.DARKENED_BACKGROUND)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("option.keybindsgalore.darkened_background.tooltip"))
                .setSaveConsumer(v -> Configurations.DARKENED_BACKGROUND = v)
                .build());

        general.addEntry(eb.startIntSlider(Component.translatable("option.keybindsgalore.darkened_background_strength"), Configurations.DARKENED_BACKGROUND_STRENGTH, 0, 255)
                .setDefaultValue(0x60)
                .setTooltip(Component.translatable("option.keybindsgalore.darkened_background_strength.tooltip"))
                .setSaveConsumer(v -> Configurations.DARKENED_BACKGROUND_STRENGTH = v)
                .build());
    }

    private static void addBehavior(ConfigEntryBuilder eb, ConfigCategory behavior) {
        behavior.addEntry(eb.startBooleanToggle(Component.translatable("option.keybindsgalore.use_circular_menu"), Configurations.USE_CIRCULAR_MENU)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("option.keybindsgalore.use_circular_menu.tooltip"))
                .setSaveConsumer(v -> Configurations.USE_CIRCULAR_MENU = v)
                .build());

        behavior.addEntry(eb.startBooleanToggle(Component.translatable("option.keybindsgalore.show_conflict_warnings"), Configurations.SHOW_CONFLICT_WARNINGS)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("option.keybindsgalore.show_conflict_warnings.tooltip"))
                .setSaveConsumer(v -> Configurations.SHOW_CONFLICT_WARNINGS = v)
                .build());

        behavior.addEntry(eb.startStrList(Component.translatable("option.keybindsgalore.filtered_categories"), Configurations.FILTERED_CATEGORY_KEYS)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("option.keybindsgalore.filtered_categories.tooltip"))
                .setSaveConsumer(v -> Configurations.FILTERED_CATEGORY_KEYS = new ArrayList<>(v))
                .build());

        behavior.addEntry(eb.startStrList(Component.translatable("option.keybindsgalore.priority_categories"), Configurations.PRIORITY_CATEGORIES)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("option.keybindsgalore.priority_categories.tooltip"))
                .setSaveConsumer(v -> Configurations.PRIORITY_CATEGORIES = new ArrayList<>(v))
                .build());

        behavior.addEntry(eb.startStrList(Component.translatable("option.keybindsgalore.priority_keybinds"), Configurations.PRIORITY_KEYBINDS)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("option.keybindsgalore.priority_keybinds.tooltip"))
                .setSaveConsumer(v -> Configurations.PRIORITY_KEYBINDS = new ArrayList<>(v))
                .build());

        behavior.addEntry(eb.startIntList(Component.translatable("option.keybindsgalore.ignored_keys"), Configurations.IGNORED_KEYS)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("option.keybindsgalore.ignored_keys.tooltip"))
                .setSaveConsumer(v -> Configurations.IGNORED_KEYS = new ArrayList<>(v))
                .build());

        behavior.addEntry(eb.startBooleanToggle(Component.translatable("option.keybindsgalore.invert_ignored_keys_list"), Configurations.INVERT_IGNORED_KEYS_LIST)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("option.keybindsgalore.invert_ignored_keys_list.tooltip"))
                .setSaveConsumer(v -> Configurations.INVERT_IGNORED_KEYS_LIST = v)
                .build());
    }

    private static void addPieLayout(ConfigEntryBuilder eb, ConfigCategory visual) {
        visual.addEntry(eb.startIntSlider(Component.translatable("option.keybindsgalore.circle_vertices"), Configurations.CIRCLE_VERTICES, 12, 360)
                .setDefaultValue(120)
                .setTooltip(Component.translatable("option.keybindsgalore.circle_vertices.tooltip"))
                .setSaveConsumer(v -> Configurations.CIRCLE_VERTICES = v)
                .build());

        visual.addEntry(eb.startIntSlider(Component.translatable("option.keybindsgalore.pie_menu_margin"), Configurations.PIE_MENU_MARGIN, 0, 200)
                .setDefaultValue(0)
                .setTooltip(Component.translatable("option.keybindsgalore.pie_menu_margin.tooltip"))
                .setSaveConsumer(v -> Configurations.PIE_MENU_MARGIN = v)
                .build());

        visual.addEntry(eb.startFloatField(Component.translatable("option.keybindsgalore.pie_scale"), Configurations.PIE_MENU_SCALE)
                .setDefaultValue(0.8f)
                .setMin(0.1f).setMax(1.0f)
                .setTooltip(Component.translatable("option.keybindsgalore.pie_scale.tooltip"))
                .setSaveConsumer(v -> Configurations.PIE_MENU_SCALE = v)
                .build());

        visual.addEntry(eb.startFloatField(Component.translatable("option.keybindsgalore.cancel_zone_scale"), Configurations.CANCEL_ZONE_SCALE)
                .setDefaultValue(0.2f)
                .setMin(0.05f).setMax(0.9f)
                .setTooltip(Component.translatable("option.keybindsgalore.cancel_zone_scale.tooltip"))
                .setSaveConsumer(v -> Configurations.CANCEL_ZONE_SCALE = v)
                .build());

        visual.addEntry(eb.startFloatField(Component.translatable("option.keybindsgalore.expansion_factor_when_selected"), Configurations.EXPANSION_FACTOR_WHEN_SELECTED)
                .setDefaultValue(0.06f)
                .setMin(0.0f).setMax(0.3f)
                .setTooltip(Component.translatable("option.keybindsgalore.expansion_factor_when_selected.tooltip"))
                .setSaveConsumer(v -> Configurations.EXPANSION_FACTOR_WHEN_SELECTED = v)
                .build());
    }

    private static void addPieColors(ConfigEntryBuilder eb, ConfigCategory visual) {
        visual.addEntry(eb.startColorField(Component.translatable("option.keybindsgalore.color_even"), Configurations.PIE_MENU_SECTOR_COLOR_EVEN)
                .setDefaultValue(0xC0606060)
                .setAlphaMode(true)
                .setTooltip(Component.translatable("option.keybindsgalore.color_even.tooltip"))
                .setSaveConsumer(v -> Configurations.PIE_MENU_SECTOR_COLOR_EVEN = v)
                .build());

        visual.addEntry(eb.startColorField(Component.translatable("option.keybindsgalore.color_odd"), Configurations.PIE_MENU_SECTOR_COLOR_ODD)
                .setDefaultValue(0xC0808080)
                .setAlphaMode(true)
                .setTooltip(Component.translatable("option.keybindsgalore.color_odd.tooltip"))
                .setSaveConsumer(v -> Configurations.PIE_MENU_SECTOR_COLOR_ODD = v)
                .build());

        visual.addEntry(eb.startColorField(Component.translatable("option.keybindsgalore.color_selected"), Configurations.PIE_MENU_SECTOR_COLOR_SELECTED)
                .setDefaultValue(0xC0E0E0E0)
                .setAlphaMode(true)
                .setTooltip(Component.translatable("option.keybindsgalore.color_selected.tooltip"))
                .setSaveConsumer(v -> Configurations.PIE_MENU_SECTOR_COLOR_SELECTED = v)
                .build());

        visual.addEntry(eb.startColorField(Component.translatable("option.keybindsgalore.color_last_odd"), Configurations.PIE_MENU_SECTOR_COLOR_LAST_ODD)
                .setDefaultValue(0xC0A0A0A0)
                .setAlphaMode(true)
                .setTooltip(Component.translatable("option.keybindsgalore.color_last_odd.tooltip"))
                .setSaveConsumer(v -> Configurations.PIE_MENU_SECTOR_COLOR_LAST_ODD = v)
                .build());

        visual.addEntry(eb.startColorField(Component.translatable("option.keybindsgalore.color_cancel"), Configurations.PIE_MENU_CANCEL_ZONE_COLOR)
                .setDefaultValue(0xC0000000)
                .setAlphaMode(true)
                .setTooltip(Component.translatable("option.keybindsgalore.color_cancel.tooltip"))
                .setSaveConsumer(v -> Configurations.PIE_MENU_CANCEL_ZONE_COLOR = v)
                .build());

        visual.addEntry(eb.startColorField(Component.translatable("option.keybindsgalore.color_cancel_hover"), Configurations.PIE_MENU_CANCEL_ZONE_HOVER_COLOR)
                .setDefaultValue(0xC0B04232)
                .setAlphaMode(true)
                .setTooltip(Component.translatable("option.keybindsgalore.color_cancel_hover.tooltip"))
                .setSaveConsumer(v -> Configurations.PIE_MENU_CANCEL_ZONE_HOVER_COLOR = v)
                .build());
    }

    private static void addPieEffects(ConfigEntryBuilder eb, ConfigCategory visual) {
        visual.addEntry(eb.startBooleanToggle(Component.translatable("option.keybindsgalore.sector_gradation"), Configurations.SECTOR_GRADATION)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("option.keybindsgalore.sector_gradation.tooltip"))
                .setSaveConsumer(v -> Configurations.SECTOR_GRADATION = v)
                .build());

        visual.addEntry(eb.startBooleanToggle(Component.translatable("option.keybindsgalore.animate_pie_menu"), Configurations.ANIMATE_PIE_MENU)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("option.keybindsgalore.animate_pie_menu.tooltip"))
                .setSaveConsumer(v -> Configurations.ANIMATE_PIE_MENU = v)
                .build());

        visual.addEntry(eb.startBooleanToggle(Component.translatable("option.keybindsgalore.pie_menu_blend"), Configurations.PIE_MENU_BLEND)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("option.keybindsgalore.pie_menu_blend.tooltip"))
                .setSaveConsumer(v -> Configurations.PIE_MENU_BLEND = v)
                .build());

        visual.addEntry(eb.startBooleanToggle(Component.translatable("option.keybindsgalore.use_software_rendering"), Configurations.USE_SOFTWARE_RENDERING)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("option.keybindsgalore.use_software_rendering.tooltip"))
                .setSaveConsumer(v -> Configurations.USE_SOFTWARE_RENDERING = v)
                .build());
    }

    private static void addPieLabels(ConfigEntryBuilder eb, ConfigCategory visual) {
        visual.addEntry(eb.startBooleanToggle(Component.translatable("option.keybindsgalore.label_text_shadow"), Configurations.LABEL_TEXT_SHADOW)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("option.keybindsgalore.label_text_shadow.tooltip"))
                .setSaveConsumer(v -> Configurations.LABEL_TEXT_SHADOW = v)
                .build());
    }

    private static void addListVisual(ConfigEntryBuilder eb, ConfigCategory listVisual) {
        listVisual.addEntry(eb.startColorField(Component.translatable("option.keybindsgalore.color"), Configurations.PIE_MENU_COLOR)
                .setDefaultValue(0x00404040)
                .setAlphaMode(true)
                .setTooltip(Component.translatable("option.keybindsgalore.color.tooltip"))
                .setSaveConsumer(v -> Configurations.PIE_MENU_COLOR = v)
                .build());

        listVisual.addEntry(eb.startColorField(Component.translatable("option.keybindsgalore.color_highlight"), Configurations.PIE_MENU_HIGHLIGHT_COLOR)
                .setDefaultValue(0x00EED202)
                .setAlphaMode(true)
                .setTooltip(Component.translatable("option.keybindsgalore.color_highlight.tooltip"))
                .setSaveConsumer(v -> Configurations.PIE_MENU_HIGHLIGHT_COLOR = v)
                .build());

        listVisual.addEntry(eb.startIntSlider(Component.translatable("option.keybindsgalore.pie_menu_alpha"), Configurations.PIE_MENU_ALPHA, 0, 255)
                .setDefaultValue(64)
                .setTooltip(Component.translatable("option.keybindsgalore.pie_menu_alpha.tooltip"))
                .setSaveConsumer(v -> Configurations.PIE_MENU_ALPHA = (short) (int) v)
                .build());
    }
}
