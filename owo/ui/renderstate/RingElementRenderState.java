package io.wispforest.owo.ui.renderstate;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.class_11231;
import net.minecraft.class_11244;
import net.minecraft.class_4588;
import net.minecraft.class_8029;
import net.minecraft.class_8030;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record RingElementRenderState(
    RenderPipeline pipeline,
    Matrix3x2f pose,
    class_8030 scissorArea,
    int centerX,
    int centerY,
    double angleFrom,
    double angleTo,
    int segments,
    double innerRadius,
    double outerRadius,
    Color innerColor,
    Color outerColor
) implements class_11244 {
    @Override
    public void method_70917(class_4588 vertices) {
        double angleStep = Math.toRadians(this.angleTo - this.angleFrom) / this.segments;
        int inColor = this.innerColor.argb();
        int outColor = this.outerColor.argb();

        for (int i = 0; i <= this.segments; i++) {
            double theta = Math.toRadians(this.angleFrom) + i * angleStep;

            vertices.method_70815(this.pose, (float) (this.centerX - Math.cos(theta) * this.outerRadius), (float) (this.centerY - Math.sin(theta) * this.outerRadius))
                .method_39415(outColor);
            vertices.method_70815(this.pose, (float) (this.centerX - Math.cos(theta) * this.innerRadius), (float) (this.centerY - Math.sin(theta) * this.innerRadius))
                .method_39415(inColor);
        }
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
    public class_8030 comp_4274() {
        var screenRect = new class_8030(
            new class_8029((int) (this.centerX - this.outerRadius), (int) (this.centerY - this.outerRadius)),
            (int) Math.ceil(this.outerRadius * 2),
            (int) Math.ceil(this.outerRadius * 2)
        ).method_71523(this.pose);

        return this.scissorArea != null ? this.scissorArea.method_49701(screenRect) : screenRect;
    }
}
