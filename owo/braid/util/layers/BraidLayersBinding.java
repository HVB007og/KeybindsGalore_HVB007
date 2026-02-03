package io.wispforest.owo.braid.util.layers;

import com.google.common.base.Suppliers;
import io.wispforest.owo.Owo;
import io.wispforest.owo.braid.core.AppState;
import io.wispforest.owo.braid.core.EventBinding;
import io.wispforest.owo.braid.core.Surface;
import io.wispforest.owo.braid.core.cursor.CursorStyle;
import io.wispforest.owo.braid.core.events.*;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.eventstream.BraidEventStream;
import io.wispforest.owo.braid.widgets.overlay.Overlay;
import io.wispforest.owo.braid.widgets.stack.Stack;
import io.wispforest.owo.util.pond.OwoScreenExtension;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.class_11875;
import net.minecraft.class_11876;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3902;
import net.minecraft.class_437;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BraidLayersBinding {

    public static void add(Predicate<class_437> screenPredicate, Widget widget) {
        LAYERS.add(new Layer(screenPredicate, widget));
    }

    // ---

    @ApiStatus.Internal
    public static boolean tryHandleEvent(class_437 screen, UserEvent event) {
        var app = ((OwoScreenExtension) screen).owo$getBraidLayersApp();
        if (app == null) {
            return false;
        }

        var slot = app.eventBinding.add(event);
        app.processEvents(0);

        return slot.handled();
    }

    @ApiStatus.Internal
    public static void renderLayers(class_437 screen, class_332 graphics, double mouseX, double mouseY) {
        var state = ((OwoScreenExtension) screen).owo$getBraidLayersState();
        if (state == null) {
            return;
        }

        state.refreshEvents.sink().onEvent(class_3902.field_17274);
        state.app.eventBinding.add(new MouseMoveEvent(mouseX, mouseY));

        state.app.processEvents(class_310.method_1551().method_61966().method_60636());
        state.app.draw(graphics);

        var cursorStyle = ((LayerSurface) state.app.surface).currentCursorStyle;
        if (cursorStyle != CursorStyle.NONE && CURSOR_MAPPINGS.get().containsKey(cursorStyle)) {
            graphics.method_74037(CURSOR_MAPPINGS.get().get(cursorStyle));
        }
    }

    private static void setupLayers(class_437 screen) {
        var widgets = LAYERS.stream().filter(layer -> layer.screenPredicate.test(screen)).map(Layer::widget).toList();
        if (widgets.isEmpty()) {
            return;
        }

        var refreshEvents = new BraidEventStream<class_3902>();
        var app = new AppState(
            null,
            "BraidLayersBinding",
            class_310.method_1551(),
            new LayerSurface(),
            new EventBinding.Default(),
            new LayerContext(
                refreshEvents.source(),
                screen,
                new Overlay(
                    new Stack(widgets)
                )
            )
        );

        ((OwoScreenExtension) screen).owo$setBraidLayersState(new LayersState(app, refreshEvents));
    }

    // ---

    public static final class_2960 INIT_PHASE = Owo.id("init-braid-layers");

    private static final List<Layer> LAYERS = new ArrayList<>();

    private record Layer(Predicate<class_437> screenPredicate, Widget widget) {}

    @ApiStatus.Internal
    public record LayersState(AppState app, BraidEventStream<class_3902> refreshEvents) {}

    private static class LayerSurface extends Surface.Default {

        public CursorStyle currentCursorStyle = CursorStyle.NONE;

        @Override
        public void setCursorStyle(CursorStyle style) {
            this.currentCursorStyle = style;
        }

        @Override
        public CursorStyle currentCursorStyle() {
            return this.currentCursorStyle;
        }
    }

    private static final Supplier<Map<CursorStyle, class_11875>> CURSOR_MAPPINGS = Suppliers.memoize(() -> Map.of(
        CursorStyle.POINTER, class_11876.field_62452,
        CursorStyle.TEXT, class_11876.field_62453,
        CursorStyle.CROSSHAIR, class_11876.field_62454,
        CursorStyle.HAND, class_11876.field_62455,
        CursorStyle.VERTICAL_RESIZE, class_11876.field_62456,
        CursorStyle.HORIZONTAL_RESIZE, class_11876.field_62457,
        CursorStyle.MOVE, class_11876.field_62458,
        CursorStyle.NOT_ALLOWED, class_11876.field_62459
    ));

    // ---

    static {
        ScreenEvents.AFTER_INIT.addPhaseOrdering(Event.DEFAULT_PHASE, INIT_PHASE);
        ScreenEvents.AFTER_INIT.register(INIT_PHASE, (client, screeen, scaledWidth, scaledHeight) -> {
            if (((OwoScreenExtension)screeen).owo$getBraidLayersState() == null) {
                setupLayers(screeen);
            }

            ScreenEvents.remove(screeen).register(screen -> {
                var app = ((OwoScreenExtension) screen).owo$getBraidLayersApp();
                if (app != null) {
                    app.dispose();
                }
            });

            ScreenMouseEvents.allowMouseClick(screeen).register((screen, click) -> {
                return !tryHandleEvent(screen, new MouseButtonPressEvent(click.method_74245(), click.comp_4797()));
            });

            ScreenMouseEvents.allowMouseRelease(screeen).register((screen, click) -> {
                return !tryHandleEvent(screen, new MouseButtonReleaseEvent(click.method_74245(), click.comp_4797()));
            });

            ScreenMouseEvents.allowMouseScroll(screeen).register((screen, mouseX, mouseY, horizontalAmount, verticalAmount) -> {
                return !tryHandleEvent(screen, new MouseScrollEvent(horizontalAmount, verticalAmount));
            });

            ScreenKeyboardEvents.allowKeyPress(screeen).register((screen, keyInput) -> {
                return !tryHandleEvent(screen, new KeyPressEvent(keyInput.comp_4795(), keyInput.comp_4796(), keyInput.comp_4797()));
            });

            ScreenKeyboardEvents.allowKeyRelease(screeen).register((screen, keyInput) -> {
                return !tryHandleEvent(screen, new KeyReleaseEvent(keyInput.comp_4795(), keyInput.comp_4796(), keyInput.comp_4797()));
            });
        });
    }
}
