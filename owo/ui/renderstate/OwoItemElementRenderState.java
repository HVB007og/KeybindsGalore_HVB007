package io.wispforest.owo.ui.renderstate;

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

public record OwoItemElementRenderState(
    class_10444 item,
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

    public static class Renderer extends class_11239<OwoItemElementRenderState> {

        public Renderer(class_4597.class_4598 vertexConsumers) {
            super(vertexConsumers);
        }

        @Override
        public Class<OwoItemElementRenderState> method_70903() {
            return OwoItemElementRenderState.class;
        }

        @Override
        protected void renderToTexture(OwoItemElementRenderState state, class_4587 matrices) {
            matrices.method_22905(state.bounds.comp_1196(), -state.bounds.comp_1197(), -Math.min(state.bounds.comp_1196(), state.bounds.comp_1197()));

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
