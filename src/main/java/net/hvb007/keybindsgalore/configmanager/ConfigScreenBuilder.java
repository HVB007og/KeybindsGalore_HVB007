package net.hvb007.keybindsgalore.configmanager;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.hvb007.keybindsgalore.Configurations;
import net.hvb007.keybindsgalore.KeybindManager;
import net.hvb007.keybindsgalore.KeybindsGalore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class ConfigScreenBuilder {

    public static Screen buildConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.keybindsgalore.config"));

        builder.setSavingRunnable(() -> {
            KeybindsGalore.configManager.saveConfigFile();
        });

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("category.keybindsgalore.general"));
        ConfigCategory visual = builder.getOrCreateCategory(Component.translatable("category.keybindsgalore.visual"));
        ConfigCategory behavior = builder.getOrCreateCategory(Component.translatable("category.keybindsgalore.behavior"));
        
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // --- General ---
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.keybindsgalore.debug"), Configurations.DEBUG)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> Configurations.DEBUG = newValue)
                .build());

        // --- Behavior ---
        behavior.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.keybindsgalore.use_circular_menu"), Configurations.USE_CIRCULAR_MENU)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> Configurations.USE_CIRCULAR_MENU = newValue)
                .build());

        behavior.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.keybindsgalore.show_conflict_warnings"), Configurations.SHOW_CONFLICT_WARNINGS)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> Configurations.SHOW_CONFLICT_WARNINGS = newValue)
                .build());

        behavior.addEntry(entryBuilder.startStrList(Component.translatable("option.keybindsgalore.filtered_categories"), Configurations.FILTERED_CATEGORY_KEYS)
                .setDefaultValue(new ArrayList<>())
                .setSaveConsumer(newValue -> Configurations.FILTERED_CATEGORY_KEYS = new ArrayList<>(newValue))
                .build());

        behavior.addEntry(entryBuilder.startStrList(Component.translatable("option.keybindsgalore.priority_keybinds"), Configurations.PRIORITY_KEYBINDS)
                .setDefaultValue(new ArrayList<>())
                .setSaveConsumer(newValue -> Configurations.PRIORITY_KEYBINDS = new ArrayList<>(newValue))
                .setTooltip(Component.translatable("option.keybindsgalore.priority_keybinds.tooltip"))
                .build());

        behavior.addEntry(entryBuilder.startTextDescription(Component.translatable("text.keybindsgalore.capture_hotkey_info", Component.translatable(KeybindsGalore.openCaptureKey.getName())))
                .build());

        // --- Visual (Pie Menu Colors) ---
        visual.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.keybindsgalore.darkened_background"), Configurations.DARKENED_BACKGROUND)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> Configurations.DARKENED_BACKGROUND = newValue)
                .build());

        visual.addEntry(entryBuilder.startAlphaColorField(Component.translatable("option.keybindsgalore.color_even"), Configurations.PIE_MENU_SECTOR_COLOR_EVEN)
                .setDefaultValue(0xC0606060)
                .setSaveConsumer(newValue -> Configurations.PIE_MENU_SECTOR_COLOR_EVEN = newValue)
                .build());

        visual.addEntry(entryBuilder.startAlphaColorField(Component.translatable("option.keybindsgalore.color_odd"), Configurations.PIE_MENU_SECTOR_COLOR_ODD)
                .setDefaultValue(0xC0808080)
                .setSaveConsumer(newValue -> Configurations.PIE_MENU_SECTOR_COLOR_ODD = newValue)
                .build());

        visual.addEntry(entryBuilder.startAlphaColorField(Component.translatable("option.keybindsgalore.color_selected"), Configurations.PIE_MENU_SECTOR_COLOR_SELECTED)
                .setDefaultValue(0xC0E0E0E0)
                .setSaveConsumer(newValue -> Configurations.PIE_MENU_SECTOR_COLOR_SELECTED = newValue)
                .build());

        visual.addEntry(entryBuilder.startAlphaColorField(Component.translatable("option.keybindsgalore.color_cancel"), Configurations.PIE_MENU_CANCEL_ZONE_COLOR)
                .setDefaultValue(0xC0000000)
                .setSaveConsumer(newValue -> Configurations.PIE_MENU_CANCEL_ZONE_COLOR = newValue)
                .build());

        visual.addEntry(entryBuilder.startAlphaColorField(Component.translatable("option.keybindsgalore.color_cancel_hover"), Configurations.PIE_MENU_CANCEL_ZONE_HOVER_COLOR)
                .setDefaultValue(0xC0B04232)
                .setSaveConsumer(newValue -> Configurations.PIE_MENU_CANCEL_ZONE_HOVER_COLOR = newValue)
                .build());

        visual.addEntry(entryBuilder.startFloatField(Component.translatable("option.keybindsgalore.pie_scale"), Configurations.PIE_MENU_SCALE)
                .setDefaultValue(0.6f)
                .setSaveConsumer(newValue -> Configurations.PIE_MENU_SCALE = newValue)
                .build());

        return builder.build();
    }
}
