package net.hvb007.keybindsgalore.configmanager;

import com.mojang.blaze3d.platform.InputConstants;
import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;
import net.hvb007.keybindsgalore.KeybindManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KeyCaptureScreen extends Screen {
    private final Screen parent;
    private final BiConsumer<InputConstants.Key, List<KeyMapping>> callback;

    public KeyCaptureScreen(Screen parent, BiConsumer<InputConstants.Key, List<KeyMapping>> callback) {
        super(Component.translatable("title.keybindsgalore.capture"));
        this.parent = parent;
        this.callback = callback;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, Component.translatable("text.keybindsgalore.press_any_key"), this.width / 2, this.height / 2 - 20, 0xFFFFFFFF);
        context.drawCenteredString(this.font, Component.translatable("text.keybindsgalore.capture_instruction"), this.width / 2, this.height / 2, 0xAAAAAA);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            Minecraft.getInstance().setScreen(parent);
            return true;
        }

        InputConstants.Key key = InputConstants.Type.KEYSYM.getOrCreate(keyCode);
        List<KeyMapping> conflicts = new ArrayList<>();

        for (KeyMapping kb : Minecraft.getInstance().options.keyMappings) {
            if (((KeyMappingAccessor) kb).getKey().equals(key)) {
                conflicts.add(kb);
            }
        }

        if (!conflicts.isEmpty()) {
            callback.accept(key, conflicts);
        } else {
            Minecraft.getInstance().setScreen(parent);
        }

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(button);
        List<KeyMapping> conflicts = new ArrayList<>();

        for (KeyMapping kb : Minecraft.getInstance().options.keyMappings) {
            if (((KeyMappingAccessor) kb).getKey().equals(key)) {
                conflicts.add(kb);
            }
        }

        if (!conflicts.isEmpty()) {
            callback.accept(key, conflicts);
        } else {
            Minecraft.getInstance().setScreen(parent);
        }
        return true;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}
