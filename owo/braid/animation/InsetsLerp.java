package io.wispforest.owo.braid.animation;

import io.wispforest.owo.braid.core.Insets;
import net.minecraft.class_3532;

public class InsetsLerp extends Lerp<Insets> {

    public InsetsLerp(Insets start, Insets end) {
        super(start, end);
    }

    @Override
    protected Insets at(double t) {
        return Insets.of(
            class_3532.method_16436(t, this.start.top(), this.end.top()),
            class_3532.method_16436(t, this.start.bottom(), this.end.bottom()),
            class_3532.method_16436(t, this.start.left(), this.end.left()),
            class_3532.method_16436(t, this.start.right(), this.end.right())
        );
    }
}
