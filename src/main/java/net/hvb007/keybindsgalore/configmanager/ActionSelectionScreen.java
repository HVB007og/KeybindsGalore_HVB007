package net.hvb007.keybindsgalore.configmanager;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

public class ActionSelectionScreen extends Screen {
    private final Screen parent;
    private final List<KeyMapping> options;
    private final Consumer<KeyMapping> callback;
    private final Runnable removeCallback;

    public ActionSelectionScreen(Screen parent, List<KeyMapping> options, Consumer<KeyMapping> callback, Runnable removeCallback) {
        super(Component.translatable("title.keybindsgalore.select_action"));
        this.parent = parent;
        this.options = options;
        this.callback = callback;
        this.removeCallback = removeCallback;
    }

    @Override
    protected void init() {
        super.init();
        int y = this.height / 2 - (options.size() * 25) / 2;

        this.addRenderableWidget(Button.builder(Component.translatable("button.keybindsgalore.remove_priority"), button -> {
            removeCallback.run();
            Minecraft.getInstance().setScreen(parent);
        }).bounds(this.width / 2 - 100, y - 30, 200, 20).build());

        for (KeyMapping kb : options) {
            this.addRenderableWidget(Button.builder(Component.translatable(kb.getCategory()).append(": ").append(Component.translatable(kb.getName())), button -> {
                callback.accept(kb);
                Minecraft.getInstance().setScreen(parent);
            }).bounds(this.width / 2 - 100, y, 200, 20).build());
            y += 25;
        }

        this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> {
            Minecraft.getInstance().setScreen(parent);
        }).bounds(this.width / 2 - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}
