package io.wispforest.owo.braid.widgets.basic;

import io.wispforest.owo.braid.core.BraidGraphics;
import io.wispforest.owo.braid.framework.instance.SingleChildWidgetInstance;
import io.wispforest.owo.braid.framework.widget.SingleChildInstanceWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.ui.renderstate.BlurQuadElementRenderState;
import net.minecraft.class_8030;
import org.joml.Matrix3x2f;

public class Blur extends SingleChildInstanceWidget {

    public final float quality;
    public final float size;
    public final boolean blurChild;

    public Blur(float quality, float size, boolean blurChild, Widget child) {
        super(child);
        this.quality = quality;
        this.size = size;
        this.blurChild = blurChild;
    }

    @Override
    public SingleChildWidgetInstance<Blur> instantiate() {
        return new Instance(this);
    }

    public static class Instance extends SingleChildWidgetInstance.ShrinkWrap<Blur> {

        public Instance(Blur widget) {
            super(widget);
        }

        @Override
        public void draw(BraidGraphics graphics) {
            if (!this.widget.blurChild) {
                this.drawBlur(graphics);
            }

            super.draw(graphics);

            if (this.widget.blurChild) {
                this.drawBlur(graphics);
            }
        }

        private void drawBlur(BraidGraphics ctx) {
            ctx.field_59826.method_70919(new BlurQuadElementRenderState(
                new Matrix3x2f(ctx.method_51448()),
                new class_8030(0, 0, (int) this.transform.width(), (int) this.transform.height()),
                ctx.field_44659.method_70863(),
                16, this.widget.quality, this.widget.size
            ));
        }
    }
}
