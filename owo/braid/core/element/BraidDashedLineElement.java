package io.wispforest.owo.braid.core.element;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.wispforest.owo.braid.core.Color;
import net.minecraft.class_11231;
import net.minecraft.class_11244;
import net.minecraft.class_4588;
import net.minecraft.class_8030;
import org.joml.Matrix3x2f;
import org.joml.Vector2d;

public record BraidDashedLineElement(
    Color color,
    double thiccness,
    double segmentLength,
    RenderPipeline pipeline,
    Matrix3x2f pose,
    class_8030 bounds,
    class_8030 scissorArea
) implements class_11244 {

    @Override
    public void method_70917(class_4588 buffer) {
        var colorArgb = this.color.argb();

        var begin = new Vector2d(this.bounds.method_49620(), this.bounds.method_49618());
        var end = new Vector2d(this.bounds.method_49621(), this.bounds.method_49619());

        var step = end.sub(begin, new Vector2d()).normalize().mul(this.segmentLength);
        var segmentCount = (int) ((end.distance(begin) + this.segmentLength) / (this.segmentLength * 2));

        var offset = end.sub(begin, new Vector2d()).perpendicular().normalize().mul(this.thiccness * .5d);
        end.set(begin).add(step);

        step.mul(2);

        for (var i = 0; i < segmentCount; i++) {
            buffer.method_70815(this.pose, (float) (begin.x + offset.x), (float) (begin.y + offset.y)).method_39415(colorArgb);
            buffer.method_70815(this.pose, (float) (begin.x - offset.x), (float) (begin.y - offset.y)).method_39415(colorArgb);
            buffer.method_70815(this.pose, (float) (end.x - offset.x), (float) (end.y - offset.y)).method_39415(colorArgb);
            buffer.method_70815(this.pose, (float) (end.x + offset.x), (float) (end.y + offset.y)).method_39415(colorArgb);

            begin.add(step);
            end.add(step);
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
    public class_8030 comp_4069() {
        return this.scissorArea;
    }

    @Override
    public class_8030 comp_4274() {
        var bounds = this.bounds.method_71523(this.pose);
        return this.scissorArea != null ? this.scissorArea.method_49701(bounds) : bounds;
    }
}
