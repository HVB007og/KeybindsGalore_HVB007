package io.wispforest.owo.ui.layers;

import io.wispforest.owo.mixin.ui.layers.AbstractContainerScreenAccessor;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.util.pond.OwoScreenExtension;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_8133;

public class Layer<S extends class_437, R extends ParentUIComponent> {

    protected final BiFunction<Sizing, Sizing, R> rootComponentMaker;
    protected final Consumer<Layer<S, R>.Instance> instanceInitializer;

    protected Layer(BiFunction<Sizing, Sizing, R> rootComponentMaker, Consumer<Layer<S, R>.Instance> instanceInitializer) {
        this.rootComponentMaker = rootComponentMaker;
        this.instanceInitializer = instanceInitializer;
    }

    public Instance instantiate(S screen) {
        return new Instance(screen);
    }

    public Instance getInstance(S screen) {
        return ((OwoScreenExtension) screen).owo$getInstance(this);
    }

    public class Instance {

        /**
         * The screen this instance is attached to
         */
        public final S screen;

        /**
         * The UI adapter of this instance - get the {@link OwoUIAdapter#rootComponent}
         * from this to start building your UI tree
         */
        public final OwoUIAdapter<R> adapter;

        /**
         * Whether this layer should aggressively update widget-relative
         * positioning every frame - useful if the targeted widget moves frequently
         */
        public boolean aggressivePositioning = false;

        protected final List<Runnable> layoutUpdaters = new ArrayList<>();

        protected Instance(S screen) {
            this.screen = screen;
            this.adapter = OwoUIAdapter.createWithoutScreen(0, 0, screen.field_22789, screen.field_22790, Layer.this.rootComponentMaker);
            Layer.this.instanceInitializer.accept(this);
        }

        @ApiStatus.Internal
        public void resize(int width, int height) {
            this.adapter.moveAndResize(0, 0, width, height);
        }

        /**
         * Find a widget in the attached screen's widget tree
         *
         * @param locator A predicate to match which identifies the targeted widget
         * @return The targeted widget, or {@link null} if the predicate was never matched
         */
        public @Nullable class_339 queryWidget(Predicate<class_339> locator) {
            var widgets = new ArrayList<class_339>();
            for (var element : this.screen.method_25396()) collectChildren(element, widgets);

            class_339 widget = null;
            for (var candidate : widgets) {
                if (!locator.test(candidate)) continue;
                widget = candidate;
                break;
            }

            return widget;
        }

        /**
         * Align the given component to a widget in the attached screen's
         * widget tree. The widget is located by passing the locator predicate to
         * {@link #queryWidget(Predicate)} and getting the position of the resulted widget.
         * <p>
         * If no widget can be found, the component gets positioned at 0,0
         *
         * @param locator       A predicate to match which identifies the targeted widget
         * @param anchor        On which side of the targeted widget to anchor the component
         * @param justification How far along the anchor side of the widget in positive axis direction
         *                      to position the component
         * @param component     The component to position
         */
        public void alignComponentToWidget(Predicate<class_339> locator, AnchorSide anchor, float justification, UIComponent component) {
            this.layoutUpdaters.add(() -> {
                var widget = this.queryWidget(locator);

                if (widget == null) {
                    component.positioning(Positioning.absolute(0, 0));
                    return;
                }

                var size = component.fullSize();
                switch (anchor) {
                    case TOP -> component.positioning(Positioning.absolute(
                            (int) (widget.method_46426() + (widget.method_25368() - size.width()) * justification),
                            widget.method_46427() - size.height()
                    ));
                    case RIGHT -> component.positioning(Positioning.absolute(
                            widget.method_46426() + widget.method_25368(),
                            (int) (widget.method_46427() + (widget.method_25364() - size.height()) * justification)
                    ));
                    case BOTTOM -> component.positioning(Positioning.absolute(
                            (int) (widget.method_46426() + (widget.method_25368() - size.width()) * justification),
                            widget.method_46427() + widget.method_25364()
                    ));
                    case LEFT -> component.positioning(Positioning.absolute(
                            widget.method_46426() - size.width(),
                            (int) (widget.method_46427() + (widget.method_25364() - size.height()) * justification)
                    ));
                }
            });
        }

        /**
         * Align the given component relative to the handled screen coordinates
         * as used by vanilla for positioning slots
         * <p>
         * For obvious reasons, this method may only be invoked on layers which are
         * pushed onto instances of {@link class_465}
         *
         * @param component The component to position
         * @param x         The X coordinate of the component, relative to the handled screen's origin
         * @param y         The Y coordinate of the component, relative to the handled screen's origin
         */
        public void alignComponentToHandledScreenCoordinates(UIComponent component, int x, int y) {
            if (!(this.screen instanceof class_465<?> handledScreen)) {
                throw new IllegalStateException("Handled screen coordinates only exist on screens which extend HandledScreen<?>");
            }

            this.layoutUpdaters.add(() -> {
                component.positioning(Positioning.absolute(
                        ((AbstractContainerScreenAccessor) handledScreen).owo$getRootX() + x,
                        ((AbstractContainerScreenAccessor) handledScreen).owo$getRootY() + y
                ));
            });
        }

        @ApiStatus.Internal
        public void dispatchLayoutUpdates() {
            this.layoutUpdaters.forEach(Runnable::run);
        }

        private static void collectChildren(class_364 element, List<class_339> children) {
            if (element instanceof class_339 widget) children.add(widget);
            if (element instanceof class_8133 layout) {
                layout.method_48206(child -> collectChildren(child, children));
            }
        }

        public enum AnchorSide {
            TOP, BOTTOM, LEFT, RIGHT
        }
    }

}