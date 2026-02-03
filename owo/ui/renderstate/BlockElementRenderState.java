package io.wispforest.owo.ui.renderstate;

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
import net.minecraft.class_7833;
import net.minecraft.class_8030;
import org.jetbrains.annotations.Nullable;

public record BlockElementRenderState(
    class_2680 state,
    @Nullable class_11954 entity,
    class_8030 bounds,
    class_8030 scissorArea
) implements class_11256 {

    @Override
    public int comp_4122() {
        return this.bounds.method_49620();
    }

    @Override
    public int comp_4124() {
        return this.bounds.method_49621();
    }

    @Override
    public int comp_4123() {
        return this.bounds.method_49618();
    }

    @Override
    public int comp_4125() {
        return this.bounds.method_49619();
    }

    @Override
    public float comp_4133() {
        return 1;
    }

    @Override
    public @Nullable class_8030 comp_4128() {
        return this.scissorArea;
    }

    @Override
    public @Nullable class_8030 comp_4274() {
        return this.scissorArea != null ? this.scissorArea.method_49701(this.bounds) : this.bounds;
    }

    public static class Renderer extends class_11239<BlockElementRenderState> {

        public Renderer(class_4597.class_4598 vertexConsumers) {
            super(vertexConsumers);
        }

        @Override
        public Class<BlockElementRenderState> method_70903() {
            return BlockElementRenderState.class;
        }

        @Override
        @SuppressWarnings("NonAsciiCharacters")
        protected void renderToTexture(BlockElementRenderState state, class_4587 matrices) {
            class_310.method_1551().field_1773.method_71114().method_71034(class_308.class_11274.field_60028);

            var width = state.bounds.comp_1196();
            var height = state.bounds.comp_1197();

            matrices.method_46416(0, -height / 2f, 100);
            matrices.method_22905(40 * width / 64f, -40 * height / 64f, -40);

            matrices.method_22907(class_7833.field_40714.rotationDegrees(30));
            matrices.method_22907(class_7833.field_40716.rotationDegrees(45 + 180));

            matrices.method_22904(-.5, -.5, -.5);

            if (state.state.method_26217() != class_2464.field_11455) {
                class_310.method_1551().method_1541().method_3353(
                    state.state, matrices, field_59933,
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
        protected String method_70906() {
            return "owo-ui_block";
        }
    }
}
