package io.wispforest.owo.braid.widgets.vanilla;

import com.mojang.blaze3d.opengl.GlStateManager;
import io.wispforest.owo.braid.core.BraidGraphics;
import io.wispforest.owo.braid.core.Constraints;
import io.wispforest.owo.braid.core.KeyModifiers;
import io.wispforest.owo.braid.framework.instance.LeafWidgetInstance;
import io.wispforest.owo.braid.framework.instance.MouseListener;
import io.wispforest.owo.braid.framework.widget.LeafInstanceWidget;
import java.util.OptionalDouble;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_11910;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_4068;
import net.minecraft.class_8021;

public class VanillaWidgetWrapper<T extends class_4068 & class_364> extends LeafInstanceWidget {

    public final T wrapped;

    public VanillaWidgetWrapper(T wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public LeafWidgetInstance<?> instantiate() {
        return new Instance(this);
    }

    public static class Instance extends LeafWidgetInstance<VanillaWidgetWrapper<?>> implements MouseListener {
        private int draggingMouseButton = 0;

        private double x, y;

        public Instance(VanillaWidgetWrapper<?> widget) {
            super(widget);
        }

        @Override
        protected void doLayout(Constraints constraints) {
            if (widget.wrapped instanceof class_8021 layoutElement) {
                layoutElement.method_48229(0, 0);
            }

            var size = constraints.hasBoundedWidth() && constraints.hasBoundedHeight()
                ? constraints.maxSize()
                : constraints.minSize();

            if (widget.wrapped instanceof class_339 abstractWidget) {
                abstractWidget.method_25358((int) size.width());
                abstractWidget.method_53533((int) size.height());
            }

            this.transform.setSize(size);
        }

        @Override
        protected double measureIntrinsicWidth(double height) {
            return 0;
        }

        @Override
        protected double measureIntrinsicHeight(double width) {
            return 0;
        }

        @Override
        protected OptionalDouble measureBaselineOffset() {
            return OptionalDouble.empty();
        }

        @Override
        public void draw(BraidGraphics graphics) {
            widget.wrapped.method_25394(graphics, (int) x, (int) y, host().client().method_61966().method_60637(false));

            GlStateManager._enableScissorTest();
        }

        public boolean onKeyDown(int keyCode, KeyModifiers modifiers) {
            return widget.wrapped.method_25404(new class_11908(keyCode, 0, modifiers.bitMask()));
        }

        public boolean onKeyUp(int keyCode, KeyModifiers modifiers) {
            return widget.wrapped.method_16803(new class_11908(keyCode, 0, modifiers.bitMask()));
        }

        public boolean onChar(int charCode, KeyModifiers modifiers) {
            return widget.wrapped.method_25400(new class_11905(charCode, modifiers.bitMask()));
        }

        public void onFocusGained() {
            this.widget.wrapped.method_25365(true);
        }

        public void onFocusLost() {
            this.widget.wrapped.method_25365(false);
        }

        @Override
        public boolean onMouseDown(double x, double y, int button, KeyModifiers modifiers) {
            return widget.wrapped.method_25402(new class_11909(x, y, new class_11910(button, modifiers.bitMask())), false);
        }

        @Override
        public boolean onMouseUp(double x, double y, int button, KeyModifiers modifiers) {
            return widget.wrapped.method_25406(new class_11909(x, y, new class_11910(button, modifiers.bitMask())));
        }

        @Override
        public void onMouseMove(double toX, double toY) {
            this.x = toX;
            this.y = toY;
        }

        @Override
        public void onMouseDragStart(int button, KeyModifiers modifiers) {
            draggingMouseButton = button;
        }

        @Override
        public void onMouseDrag(double x, double y, double dx, double dy) {
            this.widget.wrapped.method_25403(new class_11909(x, y, new class_11910(draggingMouseButton, 0)), (int) dx, (int) dy);
        }

        @Override
        public boolean onMouseScroll(double x, double y, double horizontal, double vertical) {
            return widget.wrapped.method_25401(x, y, horizontal, vertical);
        }
    }
}
