package net.hvb007.keybindsgalore.configmanager;

import com.mojang.blaze3d.platform.InputConstants;
import net.hvb007.keybindsgalore.KeybindManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
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
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, Component.translatable("text.keybindsgalore.press_any_key"), this.width / 2, this.height / 2 - 20, 0xFFFFFFFF);
        context.centeredText(this.font, Component.translatable("text.keybindsgalore.capture_instruction"), this.width / 2, this.height / 2, 0xAAAAAA);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == 256) { // ESC
            Minecraft.getInstance().setScreen(parent);
            return true;
        }

        InputConstants.Key key = InputConstants.Type.KEYSYM.getOrCreate(event.key());
        List<KeyMapping> conflicts = new ArrayList<>();
        
        for (KeyMapping kb : Minecraft.getInstance().options.keyMappings) {
            if (((net.hvb007.keybindsgalore.mixin.KeyMappingAccessor) kb).getKey().equals(key)) {
                conflicts.add(kb);
            }
        }

        if (!conflicts.isEmpty()) {
            callback.accept(key, conflicts);
        } else {
            // No keybinds on this key. Just go back.
            Minecraft.getInstance().setScreen(parent);
        }
        
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(event.button());
        List<KeyMapping> conflicts = new ArrayList<>();
        
        for (KeyMapping kb : Minecraft.getInstance().options.keyMappings) {
            if (((net.hvb007.keybindsgalore.mixin.KeyMappingAccessor) kb).getKey().equals(key)) {
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
