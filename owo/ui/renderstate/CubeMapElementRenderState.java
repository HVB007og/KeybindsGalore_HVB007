package io.wispforest.owo.ui.renderstate;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.class_11239;
import net.minecraft.class_11246;
import net.minecraft.class_11256;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_766;
import net.minecraft.class_8030;
import org.jetbrains.annotations.Nullable;

public record CubeMapElementRenderState(
    class_766 cubeMap,
    boolean rotate,
    class_8030 bounds,
    class_8030 scissorArea
) implements class_11256 {

    public static OutputOverride outputOverride = null;

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

    public static class Renderer extends class_11239<CubeMapElementRenderState> {

        private static class_332 dummyContext;

        protected Renderer(class_4597.class_4598 vertexConsumers) {
            super(vertexConsumers);
        }

        @Override
        public Class<CubeMapElementRenderState> method_70903() {
            return CubeMapElementRenderState.class;
        }

        @Override
        protected void renderToTexture(CubeMapElementRenderState state, class_4587 matrices) {
            if (dummyContext == null) {
                dummyContext = new class_332(class_310.method_1551(), new class_11246(), 0, 0);
            }

            dummyContext.field_59826.method_70926();

            try {
                CubeMapElementRenderState.outputOverride = new OutputOverride(
                    RenderSystem.outputColorTextureOverride,
                    RenderSystem.outputDepthTextureOverride,
                    0xFF000000
                );

                state.cubeMap.method_3317(dummyContext, state.bounds.comp_1196(), state.bounds.comp_1197(), state.rotate());
            } finally {
                CubeMapElementRenderState.outputOverride = null;
            }
        }

        @Override
        protected String method_70906() {
            return "owo-ui_cubemap";
        }
    }

    public record OutputOverride(GpuTextureView color, GpuTextureView depth, int resetColor) {}
}
