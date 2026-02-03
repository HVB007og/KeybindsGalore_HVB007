package io.wispforest.owo.braid.util;

import io.wispforest.owo.Owo;
import io.wispforest.owo.braid.core.AppState;
import io.wispforest.owo.braid.core.EventBinding;
import io.wispforest.owo.braid.core.Surface;
import io.wispforest.owo.braid.framework.widget.Widget;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.jetbrains.annotations.Nullable;

public class BraidHudElement implements HudElement {

    public final Widget widget;
    private AppState app;

    public BraidHudElement(Widget widget) {
        this.widget = widget;

        ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, packetSender, minecraftClient) -> {
            this.setupAppState();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> {
            this.resetAppState();
        });
    }

    public @Nullable AppState app() {
        return this.app;
    }

    @Override
    public void render(class_332 graphics, class_9779 deltaTracker) {
        if (this.app == null) {
            if (!Owo.DEBUG) {
                return;
            }

            throw new IllegalStateException("tried to render a BraidHudElement before it was initialized");
        }

        this.app.processEvents(deltaTracker.method_60636());
        this.app.draw(graphics);
    }

    protected void setupAppState() {
        this.app = new AppState(
            null,
            AppState.formatName("BraidHudElement", widget),
            class_310.method_1551(),
            new Surface.Default(),
            new EventBinding.Headless(),
            widget
        );
    }

    protected void resetAppState() {
        this.app.dispose();
        this.app = null;
    }
}
