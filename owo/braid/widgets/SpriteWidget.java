package io.wispforest.owo.braid.widgets;

import io.wispforest.owo.braid.core.BraidGraphics;
import io.wispforest.owo.braid.core.Constraints;
import io.wispforest.owo.braid.core.Size;
import io.wispforest.owo.braid.framework.instance.LeafWidgetInstance;
import io.wispforest.owo.braid.framework.widget.LeafInstanceWidget;
import java.util.OptionalDouble;
import net.minecraft.class_1058;
import net.minecraft.class_1060;
import net.minecraft.class_10799;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4730;

public class SpriteWidget extends LeafInstanceWidget {

    public static final class_2960 GUI_ATLAS_ID = class_2960.method_60656("textures/atlas/gui.png");

    public final class_4730 spriteIdentifier;

    public SpriteWidget(class_4730 spriteIdentifier) {
        this.spriteIdentifier = spriteIdentifier;
    }

    public SpriteWidget(class_2960 spriteIdentifier) {
        this.spriteIdentifier = new class_4730(GUI_ATLAS_ID, spriteIdentifier);
    }

    @Override
    public LeafWidgetInstance<SpriteWidget> instantiate() {
        return new Instance(this);
    }

    public static class Instance extends LeafWidgetInstance<SpriteWidget> {

        protected class_1058 sprite;

        public Instance(SpriteWidget widget) {
            super(widget);
        }

        @Override
        public void setWidget(SpriteWidget widget) {
            if (this.widget.spriteIdentifier.equals(widget.spriteIdentifier)) return;

            super.setWidget(widget);
            this.markNeedsLayout();
        }

        protected class_1058 findSprite() {
            try {
                this.sprite = class_310.method_1551().method_72703().method_73030(this.widget.spriteIdentifier);
            } catch (IllegalArgumentException ignored) {
                this.sprite = class_310.method_1551().method_72703().method_73030(new class_4730(GUI_ATLAS_ID, class_1060.field_5285));
            }

            return this.sprite;
        }

        @Override
        protected void doLayout(Constraints constraints) {
            this.sprite = this.findSprite();

            var size = Size.of(
                this.sprite.method_45851().method_45807(),
                this.sprite.method_45851().method_45815()
            ).constrained(constraints);

            this.transform.setSize(size);
        }

        @Override
        protected double measureIntrinsicWidth(double height) {
            return this.findSprite().method_45851().method_45807();
        }

        @Override
        protected double measureIntrinsicHeight(double width) {
            return this.findSprite().method_45851().method_45815();
        }

        @Override
        protected OptionalDouble measureBaselineOffset() {
            return OptionalDouble.empty();
        }

        @Override
        public void draw(BraidGraphics graphics) {
            graphics.method_52709(
                class_10799.field_56883,
                this.sprite,
                0,
                0,
                (int) this.transform.width(),
                (int) this.transform.height()
            );
        }
    }
}
