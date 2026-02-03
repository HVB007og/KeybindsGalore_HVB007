package io.wispforest.owo.braid.widgets.object;

import io.wispforest.owo.braid.core.BraidGraphics;
import io.wispforest.owo.braid.core.Constraints;
import io.wispforest.owo.braid.core.Size;
import io.wispforest.owo.braid.core.element.BraidItemElement;
import io.wispforest.owo.braid.framework.instance.LeafWidgetInstance;
import io.wispforest.owo.braid.framework.widget.LeafInstanceWidget;
import io.wispforest.owo.braid.framework.widget.WidgetSetupCallback;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

import java.util.OptionalDouble;
import java.util.function.Consumer;
import net.minecraft.class_10444;
import net.minecraft.class_1799;
import net.minecraft.class_811;

/// A widget that renders an [ItemStack]
///
/// The stack is rendered using the specified [ItemDisplayContext]
/// and can show overlay information (item bar, count, cooldown progress, etc.)
public class ItemStackWidget extends LeafInstanceWidget {

    public final class_1799 stack;
    protected boolean showOverlay = true;
    protected class_811 displayContext = class_811.field_4317;
    protected @Nullable LightOverride lightOverride = null;
    protected @Nullable Consumer<Matrix4f> transform;

    public ItemStackWidget(class_1799 stack, @Nullable WidgetSetupCallback<ItemStackWidget> setupCallback) {
        this.stack = stack;
        if (setupCallback != null) setupCallback.setup(this);
    }

    public ItemStackWidget(class_1799 stack) {
        this(stack, null);
    }

    public ItemStackWidget showOverlay(boolean showOverlay) {
        this.assertMutable();
        this.showOverlay = showOverlay;
        return this;
    }

    public boolean showOverlay() {
        return this.showOverlay;
    }

    public ItemStackWidget displayContext(class_811 displayContext) {
        this.assertMutable();
        this.displayContext = displayContext;
        this.showOverlay = false;
        return this;
    }

    public class_811 displayContext() {
        return this.displayContext;
    }

    public ItemStackWidget lightOverride(@Nullable LightOverride lightOverride) {
        this.assertMutable();
        this.lightOverride = lightOverride;
        return this;
    }

    public @Nullable LightOverride lightOverride() {
        return this.lightOverride;
    }

    public ItemStackWidget transform(@Nullable Consumer<Matrix4f> transform) {
        this.transform = transform;
        return this;
    }

    public @Nullable Consumer<Matrix4f> transform() {
        return this.transform;
    }

    @Override
    public LeafWidgetInstance<?> instantiate() {
        return new Instance(this);
    }

    public static class Instance extends LeafWidgetInstance<ItemStackWidget> {

        public static final Size DEFAULT_SIZE = Size.square(16);

        public Instance(ItemStackWidget widget) {
            super(widget);
        }

        @Override
        protected void doLayout(Constraints constraints) {
            var size = DEFAULT_SIZE.constrained(constraints);
            this.transform.setSize(size);
        }

        @Override
        protected double measureIntrinsicWidth(double height) {
            return 16;
        }

        @Override
        protected double measureIntrinsicHeight(double width) {
            return 16;
        }

        @Override
        protected OptionalDouble measureBaselineOffset() {
            return OptionalDouble.empty();
        }

        @Override
        public void draw(BraidGraphics graphics) {
            if (this.transform.width() <= 16 && this.transform.height() <= 16 && this.widget.displayContext == class_811.field_4317 && this.widget.transform == null) {
                // scale according to widget size, since items assume a 16x16 window
                graphics.push().scale((float) (this.transform.width() / 16f), (float) (this.transform.height() / 16f));
                graphics.method_51427(this.widget.stack, 0, 0);
                graphics.pop();
            } else {
                var state = new class_10444();
                this.host().client().method_65386().method_65596(state, this.widget.stack, this.widget.displayContext, this.host().client().field_1687, this.host().client().field_1724, 0);

                var transformThisFrame = new Matrix4f();
                if (this.widget.transform != null) {
                    this.widget.transform.accept(transformThisFrame);
                }

                graphics.field_59826.method_70922(new BraidItemElement(
                    state,
                    this.transform.width(),
                    this.transform.height(),
                    graphics.field_44659.method_70863(),
                    transformThisFrame,
                    new Matrix3x2f(graphics.method_51448())
                ));
            }

            if (this.widget.showOverlay) {
                var popTransform = false;
                if (this.transform.width() != 16 || this.transform.height() != 16) {
                    popTransform = true;

                    graphics.push();
                    graphics.scale((float) (this.transform.width() / 16), (float) (this.transform.height() / 16));
                }

                graphics.method_51431(this.host().client().field_1772, this.widget.stack, 0, 0);

                if (popTransform) {
                    graphics.pop();
                }
            }
        }
    }

    public enum LightOverride {
        FRONT,
        SIDE
    }
}
