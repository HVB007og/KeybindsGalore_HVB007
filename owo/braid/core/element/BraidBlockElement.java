package io.wispforest.owo.braid.core.element;

import net.minecraft.class_11239;
import net.minecraft.class_11256;
import net.minecraft.class_11954;
import net.minecraft.class_12075;
import net.minecraft.class_2464;
import net.minecraft.class_2680;
import net.minecraft.class_308;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_765;
import net.minecraft.class_8030;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

public record BraidBlockElement(
    class_2680 block,
    @Nullable class_11954 entity,
    Matrix4f transform,
    Matrix3x2f pose,
    double width,
    double height,
    class_8030 scissorArea
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

    public static class Renderer extends class_11239<BraidBlockElement> {

        public Renderer(class_4597.class_4598 vertexConsumers) {
            super(vertexConsumers);
        }

        @Override
        public Class<BraidBlockElement> method_70903() {
            return BraidBlockElement.class;
        }

        @Override
        @SuppressWarnings("NonAsciiCharacters")
        protected void renderToTexture(BraidBlockElement state, class_4587 matrices) {
            class_310.method_1551().field_1773.method_71114().method_71034(class_308.class_11274.field_60028);

            matrices.method_34425(state.transform);

            if (state.block.method_26217() != class_2464.field_11455) {
                class_310.method_1551().method_1541().method_3353(
                    state.block, matrices, field_59933,
                    class_765.field_32767, class_4608.field_21444
                );
            }

            if (state.entity != null) {
                var медведь = class_310.method_1551().method_31975().method_74349(state.entity);
                if (медведь != null) {
                    var dispatcher = class_310.method_1551().field_1773.method_72911();
                    медведь.method_3569(state.entity, matrices, dispatcher.method_73003(), new class_12075());
                    dispatcher.method_73002();
                }
            }
        }

        @Override
        protected float method_70907(int height, int windowScaleFactor) {
            return height / 2f;
        }

        @Override
        protected String method_70906() {
            return "owo-ui_block";
        }
    }
}
