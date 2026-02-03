package io.wispforest.owo.braid.widgets.object;

import io.wispforest.owo.braid.core.BraidGraphics;
import io.wispforest.owo.braid.core.Constraints;
import io.wispforest.owo.braid.core.Size;
import io.wispforest.owo.braid.core.element.BraidBlockElement;
import io.wispforest.owo.braid.framework.instance.LeafWidgetInstance;
import io.wispforest.owo.braid.framework.widget.LeafInstanceWidget;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

import java.util.OptionalDouble;
import java.util.function.Consumer;
import net.minecraft.class_11954;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_7833;

/// A widget that renders a [BlockState] and optionally a [BlockEntity]
public class RawBlockWidget extends LeafInstanceWidget {

    public final class_2680 blockState;
    public final @Nullable class_2586 blockEntity;
    public final @Nullable Consumer<Matrix4f> transform;

    public RawBlockWidget(class_2680 blockState, @Nullable class_2586 blockEntity, @Nullable Consumer<Matrix4f> transform) {
        this.blockState = blockState;
        this.blockEntity = blockEntity;
        this.transform = transform;
    }

    @Override
    public LeafWidgetInstance<?> instantiate() {
        return new Instance(this);
    }

    // ---

    public static class Instance extends LeafWidgetInstance<RawBlockWidget> {

        public static final Size DEFAULT_SIZE = Size.square(16);

        public Instance(RawBlockWidget widget) {
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
            var drawTransform = new Matrix4f();
            drawTransform.scale(40 * (float) (this.transform.width() / 64f), -40 * (float) (this.transform.height() / 64f), -40);

            if (this.widget.transform != null) {
                this.widget.transform.accept(drawTransform);
            } else {
                drawTransform.rotate(class_7833.field_40714.rotationDegrees(30));
                drawTransform.rotate(class_7833.field_40716.rotationDegrees(45 + 180));
            }

            drawTransform.translate(-.5f, -.5f, -.5f);

            class_11954 entity = null;
            if (this.widget.blockEntity != null) {
                var renderer = class_310.method_1551().method_31975().method_3550(this.widget.blockEntity);
                if (renderer != null) {
                    entity = renderer.method_74335();
                    renderer.method_74331(
                        this.widget.blockEntity, entity, 0, class_243.field_1353, null
                    );
                }
            }

            graphics.field_59826.method_70922(new BraidBlockElement(
                this.widget.blockState,
                entity,
                drawTransform,
                new Matrix3x2f(graphics.method_51448()),
                this.transform.width(),
                this.transform.height(),
                graphics.field_44659.method_70863()
            ));
        }
    }
}
