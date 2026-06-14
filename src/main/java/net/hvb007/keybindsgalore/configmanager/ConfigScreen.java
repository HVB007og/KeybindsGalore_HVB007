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
import java.util.stream.Collectors;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final Map<String, Field> fields = new LinkedHashMap<>();
    private final Map<String, Object> workingValues = new LinkedHashMap<>();
    private int scrollOffset = 0;
    private EditBox activeEditBox = null;
    private String editingField = null;

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
    protected void init() {
        int y = this.height - 40;
        addRenderableWidget(Button.builder(Component.translatable("button.keybindsgalore.save"), btn -> {
            save();
            Minecraft.getInstance().setScreen(parent);
        }).bounds(this.width / 2 - 105, y, 100, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), btn -> {
            Minecraft.getInstance().setScreen(parent);
        }).bounds(this.width / 2 + 5, y, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        int y = 30 - scrollOffset;
        for (String name : fields.keySet()) {
            if (y < -20 || y > height - 50) {
                y += 22;
                continue;
            }

            ctx.drawString(font, name, 10, y + 5, 0xFFFFFF);

            if (editingField != null && editingField.equals(name) && activeEditBox != null) {
                activeEditBox.render(ctx, mouseX, mouseY, delta);
            } else {
                Object val = workingValues.get(name);
                String display = formatValue(val);
                ctx.drawString(font, display, this.width / 2, y + 5, 0xAAAAAA);
            }
            y += 22;
        }
    }

    private String formatValue(Object val) {
        if (val == null) return "null";
        if (val instanceof ArrayList) {
            return ((ArrayList<?>) val).stream().map(Object::toString).collect(Collectors.joining(", "));
        }
        if (val instanceof Integer || val instanceof Short) {
            int v = val instanceof Integer ? (Integer) val : (Short) val;
            return v > 0xFFFFFF ? String.valueOf(v) : "0x" + Integer.toHexString(v).toUpperCase();
        }
        return val.toString();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (activeEditBox != null) {
            if (!activeEditBox.mouseClicked(mouseX, mouseY, button)) {
                finishEditing();
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        int y = 30 - scrollOffset;
        for (String name : fields.keySet()) {
            if (y < -20 || y > height - 50) {
                y += 22;
                continue;
            }
            if (mouseX >= width / 2 && mouseX <= width - 10 && mouseY >= y + 2 && mouseY <= y + 18) {
                startEditing(name);
                return true;
            }
            y += 22;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void startEditing(String name) {
        Field field = fields.get(name);
        if (field == null) return;

        Class<?> type = field.getType();
        if (type == boolean.class) {
            try {
                boolean current = (boolean) workingValues.get(name);
                workingValues.put(name, !current);
            } catch (Exception ignored) {
            }
            return;
        }

        editingField = name;
        Object val = workingValues.get(name);
        String text = val != null ? val.toString() : "";

        int y = getFieldY(name);
        if (y < 0) return;

        activeEditBox = new EditBox(font, this.width / 2, y + 2, this.width / 2 - 20, 16, Component.literal(""));
        activeEditBox.setValue(text);
        activeEditBox.setFocused(true);
        activeEditBox.setMaxLength(512);
        addRenderableWidget(activeEditBox);
    }

    private int getFieldY(String name) {
        int y = 30 - scrollOffset;
        for (String n : fields.keySet()) {
            if (n.equals(name)) return y;
            y += 22;
        }
        return -1;
    }

    private void finishEditing() {
        if (activeEditBox != null && editingField != null) {
            String text = activeEditBox.getValue();
            Field field = fields.get(editingField);
            if (field != null) {
                Class<?> type = field.getType();
                Object parsed = parseValue(text, type);
                if (parsed != null) {
                    workingValues.put(editingField, parsed);
                }
            }
            removeWidget(activeEditBox);
            activeEditBox = null;
            editingField = null;
        }
    }

    private Object parseValue(String text, Class<?> type) {
        try {
            if (type == int.class) {
                return text.startsWith("0x") ? (int) Long.parseLong(text.substring(2), 16) : Integer.parseInt(text);
            }
            if (type == float.class) return Float.parseFloat(text);
            if (type == short.class) {
                return text.startsWith("0x") ? Short.parseShort(text.substring(2), 16) : Short.parseShort(text);
            }
            if (type == boolean.class) return Boolean.parseBoolean(text);
            if (type == ArrayList.class) {
                ArrayList<String> list = new ArrayList<>();
                for (String s : text.split(",")) {
                    String trimmed = s.trim();
                    if (!trimmed.isEmpty()) list.add(trimmed);
                }
                return list;
            }
        } catch (Exception ignored) {
        }
        return text;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (activeEditBox != null) {
            if (keyCode == 257 || keyCode == 335) {
                finishEditing();
                return true;
            }
            if (keyCode == 256) {
                finishEditing();
                return true;
            }
            return activeEditBox.keyPressed(keyCode, scanCode, modifiers);
        }
        if (keyCode == 256) {
            Minecraft.getInstance().setScreen(parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (activeEditBox != null) {
            return activeEditBox.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
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
