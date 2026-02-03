package io.wispforest.owo.ui.renderstate;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.class_11231;
import net.minecraft.class_11244;
import net.minecraft.class_4588;
import net.minecraft.class_8030;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record GradientQuadElementRenderState(
    RenderPipeline pipeline,
    Matrix3x2f pose,
    class_8030 bounds,
    class_8030 scissorArea,
    Color colorTL,
    Color colorTR,
    Color colorBL,
    Color colorBR
) implements class_11244 {

    @Override
    public void method_70917(class_4588 vertices) {
        vertices.method_70815(this.pose(), (float) this.bounds.method_49620(), (float) this.bounds.method_49618()).method_39415(this.colorTL.argb());
        vertices.method_70815(this.pose(), (float) this.bounds.method_49620(), (float) this.bounds.method_49619()).method_39415(this.colorBL.argb());
        vertices.method_70815(this.pose(), (float) this.bounds.method_49621(), (float) this.bounds.method_49619()).method_39415(this.colorBR.argb());
        vertices.method_70815(this.pose(), (float) this.bounds.method_49621(), (float) this.bounds.method_49618()).method_39415(this.colorTR.argb());
    }

    @Override
    public RenderPipeline comp_4055() {
        return this.pipeline;
    }

    @Override
    public class_11231 comp_4056() {
        return class_11231.method_70899();
    }

    @Override
    public @Nullable class_8030 comp_4069() {
        return this.scissorArea;
    }

    @Override
    public @Nullable class_8030 comp_4274() {
        return this.scissorArea != null ? this.scissorArea.method_49701(this.bounds) : this.bounds;
    }
}
