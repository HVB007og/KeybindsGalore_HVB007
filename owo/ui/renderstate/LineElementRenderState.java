package io.wispforest.owo.ui.renderstate;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.class_11231;
import net.minecraft.class_11244;
import net.minecraft.class_4588;
import net.minecraft.class_8030;
import org.joml.Matrix3x2f;
import org.joml.Vector2d;

public record LineElementRenderState(
    RenderPipeline pipeline,
    Matrix3x2f pose,
    class_8030 scissorArea,
    int x0,
    int y0,
    int x1,
    int y1,
    double thiccness,
    Color color
) implements class_11244 {
    @Override
    public void method_70917(class_4588 vertices) {
        var offset = new Vector2d(this.x1 - this.x0, this.y1 - this.y0).perpendicular().normalize().mul(this.thiccness * .5d);

        int vColor = this.color.argb();
        vertices.method_70815(this.pose, (float) (x0 + offset.x), (float) (y0 + offset.y)).method_39415(vColor);
        vertices.method_70815(this.pose, (float) (x0 - offset.x), (float) (y0 - offset.y)).method_39415(vColor);
        vertices.method_70815(this.pose, (float) (x1 - offset.x), (float) (y1 - offset.y)).method_39415(vColor);
        vertices.method_70815(this.pose, (float) (x1 + offset.x), (float) (y1 + offset.y)).method_39415(vColor);
    }

    @Override
    public class_11231 comp_4056() {
        return class_11231.method_70899();
    }

    @Override
    public class_8030 comp_4274() {
        return new class_8030(this.x0, this.y0, this.x1 - this.x0, this.y1 - this.y0);
    }
}
