package io.wispforest.owo.ui.component;

import io.wispforest.owo.braid.core.AppState;
import io.wispforest.owo.braid.core.EventBinding;
import io.wispforest.owo.braid.core.Surface;
import io.wispforest.owo.braid.core.cursor.CursorStyle;
import io.wispforest.owo.braid.core.cursor.SystemCursorStyle;
import io.wispforest.owo.braid.core.events.*;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Sized;
import io.wispforest.owo.braid.widgets.drag.DragArena;
import io.wispforest.owo.braid.widgets.drag.DragArenaElement;
import io.wispforest.owo.ui.base.BaseUIComponent;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.Size;
import org.lwjgl.glfw.GLFW;

import java.lang.ref.Cleaner;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_310;

public class BraidComponent extends BaseUIComponent {

    private static final Cleaner APP_CLEANER = Cleaner.create();

    private final AppState appState;
    private final EventBinding eventBinding = new EventBinding.Default();

    private BraidWidget.State braidWidgetState;

    private CursorStyle cursorStyle = CursorStyle.NONE;

    public BraidComponent(Widget braidWidget) {
        this.appState = new AppState(
            null,
            AppState.formatName("BraidComponent", braidWidget),
            class_310.method_1551(),
            new EmbedSurface(this),
            eventBinding,
            new BraidWidget(
                state -> braidWidgetState = state,
                braidWidget
            )
        );

        APP_CLEANER.register(this, new AppCleanCallback(appState));
    }

    @Override
    public void inflate(Size space) {
        super.inflate(space);
        braidWidgetState.setState(() -> {
            braidWidgetState.width = this.width;
            braidWidgetState.height = this.height;
        });
    }

    @Override
    public void updateX(int x) {
        super.updateX(x);
        braidWidgetState.setState(() -> braidWidgetState.x = x);
    }

    @Override
    public void updateY(int y) {
        super.updateY(y);
        braidWidgetState.setState(() -> braidWidgetState.y = y);
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);

        eventBinding.add(new MouseMoveEvent(mouseX, mouseY));
        appState.processEvents(
            delta
        );
    }

    @Override
    public void draw(OwoUIGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        appState.draw(graphics);
    }

    @Override
    public boolean onMouseDown(class_11909 click, boolean doubled) {
        eventBinding.add(new MouseButtonPressEvent(click.method_74245(), click.comp_4797()));
        return true;
    }

    @Override
    public boolean onMouseUp(class_11909 click) {
        eventBinding.add(new MouseButtonReleaseEvent(click.method_74245(), click.comp_4797()));
        return true;
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        // TODO: reconsider
        eventBinding.add(new MouseScrollEvent(0, amount));
        return true;
    }

    @Override
    public boolean onKeyPress(class_11908 input) {
        this.eventBinding.add(new KeyPressEvent(input.comp_4795(), input.comp_4796(), input.comp_4797()));
        this.eventBinding.add(new KeyReleaseEvent(input.comp_4795(), input.comp_4796(), input.comp_4797()));
        return true;
    }

    @Override
    public boolean onCharTyped(class_11905 input) {
        this.eventBinding.add(new CharInputEvent((char) input.comp_4793(), input.comp_4794()));
        return true;
    }

    @Override
    public io.wispforest.owo.ui.core.CursorStyle cursorStyle() {
        if (!(cursorStyle instanceof SystemCursorStyle system)) return io.wispforest.owo.ui.core.CursorStyle.NONE;

        return switch (system.glfwId) {
            case GLFW.GLFW_ARROW_CURSOR -> io.wispforest.owo.ui.core.CursorStyle.POINTER;
            case GLFW.GLFW_IBEAM_CURSOR -> io.wispforest.owo.ui.core.CursorStyle.TEXT;
            case GLFW.GLFW_HAND_CURSOR -> io.wispforest.owo.ui.core.CursorStyle.HAND;
            case GLFW.GLFW_RESIZE_ALL_CURSOR -> io.wispforest.owo.ui.core.CursorStyle.MOVE;
            case GLFW.GLFW_CROSSHAIR_CURSOR -> io.wispforest.owo.ui.core.CursorStyle.CROSSHAIR;
            case GLFW.GLFW_HRESIZE_CURSOR -> io.wispforest.owo.ui.core.CursorStyle.HORIZONTAL_RESIZE;
            case GLFW.GLFW_VRESIZE_CURSOR -> io.wispforest.owo.ui.core.CursorStyle.VERTICAL_RESIZE;
            case GLFW.GLFW_RESIZE_NWSE_CURSOR -> io.wispforest.owo.ui.core.CursorStyle.NWSE_RESIZE;
            case GLFW.GLFW_RESIZE_NESW_CURSOR -> io.wispforest.owo.ui.core.CursorStyle.NESW_RESIZE;
            case GLFW.GLFW_NOT_ALLOWED_CURSOR -> io.wispforest.owo.ui.core.CursorStyle.NOT_ALLOWED;

            default -> io.wispforest.owo.ui.core.CursorStyle.NONE;
        };
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }

    private record AppCleanCallback(AppState app) implements Runnable {
        @Override
        public void run() {
            this.app.dispose();
        }
    }

    public static class EmbedSurface extends Surface.Default {
        // this is a weak reference so that the AppState can get properly collected
        private final WeakReference<BraidComponent> parent;

        public EmbedSurface(BraidComponent parent) {
            this.parent = new WeakReference<>(parent);
        }

        @Override
        public CursorStyle currentCursorStyle() {
            //noinspection DataFlowIssue
            return parent.get().cursorStyle;
        }

        @Override
        public void setCursorStyle(CursorStyle style) {
            //noinspection DataFlowIssue
            parent.get().cursorStyle = style;
        }
    }

    public static class BraidWidget extends StatefulWidget {

        public Consumer<State> stateConsumer;

        public final Widget child;

        public BraidWidget(Consumer<State> stateConsumer, Widget child) {
            this.stateConsumer = stateConsumer;
            this.child = child;
        }

        @Override
        public WidgetState<BraidWidget> createState() {
            var state = new State();

            this.stateConsumer.accept(state);
            this.stateConsumer = null;

            return state;
        }

        public static class State extends WidgetState<BraidWidget> {
            private int x, y, width, height;

            @Override
            public Widget build(BuildContext context) {
                return new DragArena(
                    new DragArenaElement(
                        x, y,
                        new Sized(
                            width, height,
                            this.widget().child
                        )
                    )
                );
            }
        }
    }
}
