package net.hvb007.keybindsgalore.configmanager;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.hvb007.keybindsgalore.Configurations;
import net.hvb007.keybindsgalore.KeybindsGalore;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigScreen {
    private final Screen parent;
    private final Map<String, Field> fields = new LinkedHashMap<>();
    private final Map<String, Object> workingCopy = new LinkedHashMap<>();

    public ConfigScreen(Screen parent) {
        this.parent = parent;
        for (Field field : Configurations.class.getDeclaredFields()) {
            try {
                fields.put(field.getName(), field);
                workingCopy.put(field.getName(), field.get(null));
            } catch (Exception ignored) {
            }
        }
    }

    public Screen build() {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.translatable("title.keybindsgalore.config"))
            .setSavingRunnable(this::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        buildCategory(builder, entryBuilder, "General",
            "DEBUG", "VERBOSE_DEBUG", "LAZY_CONFLICT_CHECK",
            "CIRCLE_VERTICES", "PULSE_TIMER_DURATION", "USE_KEYBIND_FIX",
            "ENABLE_ATTACK_WORKAROUND", "PIE_MENU_BLEND", "DARKENED_BACKGROUND",
            "LABEL_TEXT_SHADOW", "USE_CIRCULAR_MENU", "USE_SOFTWARE_RENDERING",
            "SHOW_CONFLICT_WARNINGS", "SECTOR_GRADATION", "ANIMATE_PIE_MENU");

        buildCategory(builder, entryBuilder, "Behaviour",
            "FILTERED_CATEGORY_KEYS", "IGNORED_KEYS", "INVERT_IGNORED_KEYS_LIST",
            "PRIORITY_CATEGORIES", "PRIORITY_KEYBINDS",
            "EXPANSION_FACTOR_WHEN_SELECTED", "PIE_MENU_MARGIN", "PIE_MENU_SCALE",
            "CANCEL_ZONE_SCALE", "PULSE_TIMER_DURATION");

        buildCategory(builder, entryBuilder, "Pie Menu Colours",
            "PIE_MENU_COLOR", "PIE_MENU_SELECT_COLOR", "PIE_MENU_HIGHLIGHT_COLOR",
            "PIE_MENU_SECTOR_COLOR_EVEN", "PIE_MENU_SECTOR_COLOR_ODD",
            "PIE_MENU_SECTOR_COLOR_SELECTED", "PIE_MENU_SECTOR_COLOR_LAST_ODD",
            "PIE_MENU_CANCEL_ZONE_COLOR", "PIE_MENU_CANCEL_ZONE_HOVER_COLOR",
            "PIE_MENU_COLOR_LIGHTEN_FACTOR", "PIE_MENU_ALPHA", "LABEL_TEXT_INSET");

        return builder.build();
    }

    private void buildCategory(ConfigBuilder builder, ConfigEntryBuilder entryBuilder, String title, String... fieldNames) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal(title));
        for (String name : fieldNames) {
            Field field = fields.get(name);
            if (field == null) continue;
            Object value = workingCopy.get(name);
            if (value == null) continue;

            Component displayName = Component.literal(name);
            AbstractConfigListEntry<?> entry = buildEntry(entryBuilder, field, name, value, displayName);
            if (entry != null) {
                category.addEntry(entry);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private AbstractConfigListEntry<?> buildEntry(ConfigEntryBuilder eb, Field field, String name, Object value, Component displayName) {
        Class<?> type = field.getType();
        try {
            if (type == boolean.class) {
                return eb.startBooleanToggle(displayName, (boolean) value)
                    .setDefaultValue(field.getBoolean(null))
                    .setSaveConsumer(v -> workingCopy.put(name, v))
                    .build();
            }
            if (type == int.class) {
                int intVal = (int) value;
                if (name.startsWith("PIE_MENU_COLOR") || name.startsWith("PIE_MENU_SECTOR") || name.startsWith("PIE_MENU_CANCEL") || name.startsWith("PIE_MENU_HIGHLIGHT") || name.equals("PIE_MENU_SELECT_COLOR") || name.equals("PIE_MENU_COLOR_LIGHTEN_FACTOR")) {
                    return eb.startAlphaColorField(displayName, intVal)
                        .setDefaultValue(field.getInt(null))
                        .setSaveConsumer(v -> workingCopy.put(name, v))
                        .build();
                }
                return eb.startIntField(displayName, intVal)
                    .setDefaultValue(field.getInt(null))
                    .setSaveConsumer(v -> workingCopy.put(name, v))
                    .build();
            }
            if (type == float.class) {
                return eb.startFloatField(displayName, (float) value)
                    .setDefaultValue(field.getFloat(null))
                    .setSaveConsumer(v -> workingCopy.put(name, v))
                    .build();
            }
            if (type == short.class) {
                int shortVal = (short) value;
                return eb.startIntField(displayName, shortVal)
                    .setDefaultValue(field.getShort(null))
                    .setSaveConsumer(v -> workingCopy.put(name, (short) (int) v))
                    .build();
            }
            if (type == ArrayList.class) {
                ArrayList<?> list = (ArrayList<?>) value;
                if (list.isEmpty() || list.get(0) instanceof String) {
                    List<String> strList = ((ArrayList<String>) value);
                    return eb.startStrList(displayName, new ArrayList<>(strList))
                        .setDefaultValue((List<String>) field.get(null))
                        .setSaveConsumer(v -> workingCopy.put(name, new ArrayList<>(v)))
                        .build();
                }
                if (list.get(0) instanceof Integer) {
                    List<Integer> intList = ((ArrayList<Integer>) value);
                    return eb.startIntList(displayName, new ArrayList<>(intList))
                        .setDefaultValue((List<Integer>) field.get(null))
                        .setSaveConsumer(v -> workingCopy.put(name, new ArrayList<>(v)))
                        .build();
                }
            }
        } catch (Exception e) {
            KeybindsGalore.LOGGER.error("Failed to build entry for {}", name, e);
        }
        return null;
    }

    private void save() {
        for (Map.Entry<String, Object> e : workingCopy.entrySet()) {
            Field field = fields.get(e.getKey());
            if (field == null) continue;
            try {
                field.setAccessible(true);
                Object value = e.getValue();
                if (field.getType() == int.class) {
                    field.setInt(null, (int) value);
                } else if (field.getType() == float.class) {
                    field.setFloat(null, (float) value);
                } else if (field.getType() == boolean.class) {
                    field.setBoolean(null, (boolean) value);
                } else if (field.getType() == short.class) {
                    field.setShort(null, (short) value);
                } else {
                    field.set(null, value);
                }
            } catch (Exception ignored) {
            }
        }
        KeybindsGalore.configManager.saveConfigFile();
    }

    public static Screen create(Screen parent) {
        return new ConfigScreen(parent).build();
    }
}
