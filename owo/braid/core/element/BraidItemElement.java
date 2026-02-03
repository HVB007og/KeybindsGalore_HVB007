package io.wispforest.owo.braid.core.element;

import net.minecraft.class_10444;
import net.minecraft.class_11239;
import net.minecraft.class_11256;
import net.minecraft.class_308;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_765;
import net.minecraft.class_8030;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix4fc;

public record BraidItemElement(
    class_10444 item,
    double width,
    double height,
    class_8030 scissorArea,
    Matrix4fc transform,
    Matrix3x2f pose
) implements class_11256 {

    @Override
    public int comp_4122() {
        return 0;
    }

    @Override
    public int comp_4124() {
        return (int) this.width;
    }

    @Override
    public int comp_4123() {
        return 0;
    }

    @Override
    public int comp_4125() {
        return (int) this.height;
    }

    @Override
    public float comp_4133() {
        return 1;
    }

    @Override
    public Matrix3x2f method_72127() {
        return this.pose;
    }

    @Override
    public @Nullable class_8030 comp_4128() {
        return this.scissorArea;
    }

    @Override
    public @Nullable class_8030 comp_4274() {
        var bounds = new class_8030(0, 0, (int) this.width, (int) this.height).method_71523(this.pose);

        return this.scissorArea != null
            ? this.scissorArea.method_49701(bounds)
            : bounds;
    }

    public static class Renderer extends class_11239<BraidItemElement> {

        public Renderer(class_4597.class_4598 vertexConsumers) {
            super(vertexConsumers);
        }

        @Override
        public Class<BraidItemElement> method_70903() {
            return BraidItemElement.class;
        }

        @Override
        protected void renderToTexture(BraidItemElement state, class_4587 matrices) {
            matrices.method_22905((float) state.width, (float) -state.height, (float) -Math.min(state.width, state.height));
            matrices.method_34425(state.transform);

            var notSideLit = !state.item.method_65608();
            if (notSideLit) {
                class_310.method_1551().field_1773.method_71114().method_71034(class_308.class_11274.field_60026);
            } else {
                class_310.method_1551().field_1773.method_71114().method_71034(class_308.class_11274.field_60027);
            }

            var dispatcher = class_310.method_1551().field_1773.method_72911();
            state.item.method_65604(matrices, dispatcher.method_73003(), class_765.field_32767, class_4608.field_21444, 0);
            dispatcher.method_73002();
        }

        @Override
        protected float method_70907(int height, int windowScaleFactor) {
            return height / 2f;
        }

        @Override
        protected String method_70906() {
            return "owo-item";
        }
    }
}
