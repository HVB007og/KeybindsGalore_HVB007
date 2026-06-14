package net.hvb007.keybindsgalore.configmanager;

import net.hvb007.keybindsgalore.Configurations;
import net.hvb007.keybindsgalore.KeybindsGalore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final Map<String, Field> fields = new LinkedHashMap<>();
    private final Map<String, Object> workingValues = new LinkedHashMap<>();
    private int scrollOffset = 0;

    public ConfigScreen(Screen parent) {
        super(Component.translatable("title.keybindsgalore.config"));
        this.parent = parent;

        for (Field field : Configurations.class.getDeclaredFields()) {
            try {
                fields.put(field.getName(), field);
                workingValues.put(field.getName(), field.get(null));
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        int y = 30 - scrollOffset;
        for (String name : fields.keySet()) {
            if (y < -20 || y > height) {
                y += 22;
                continue;
            }
            ctx.drawString(font, name, 10, y + 5, 0xFFFFFF);
            Object val = workingValues.get(name);
            ctx.drawString(font, val != null ? val.toString() : "null", width / 2, y + 5, 0xAAAAAA);
            y += 22;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scrollOffset = (int) Math.max(0, Math.min(scrollOffset - scrollY * 10, Math.max(0, fields.size() * 22 - height + 60)));
        return true;
    }

    private void save() {
        for (Map.Entry<String, Object> e : workingValues.entrySet()) {
            Field field = fields.get(e.getKey());
            if (field == null) continue;
            try {
                field.setAccessible(true);
                Object value = e.getValue();
                if (field.getType() == int.class && value instanceof String) {
                    String s = (String) value;
                    field.setInt(null, s.startsWith("0x") ? (int) Long.parseLong(s.substring(2), 16) : Integer.parseInt(s));
                } else if (field.getType() == float.class && value instanceof String) {
                    field.setFloat(null, Float.parseFloat((String) value));
                } else if (field.getType() == boolean.class && value instanceof String) {
                    field.setBoolean(null, Boolean.parseBoolean((String) value));
                } else if (field.getType() == short.class && value instanceof String) {
                    String s = (String) value;
                    field.setShort(null, s.startsWith("0x") ? Short.parseShort(s.substring(2), 16) : Short.parseShort(s));
                } else if (value instanceof ArrayList) {
                    field.set(null, value);
                } else {
                    field.set(null, value);
                }
            } catch (Exception ignored) {
            }
        }
        KeybindsGalore.configManager.saveConfigFile();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static void open(Screen parent) {
        Minecraft.getInstance().setScreen(new ConfigScreen(parent));
    }
}
