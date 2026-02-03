package io.wispforest.owo.braid.animation;

import io.wispforest.owo.braid.core.Alignment;
import net.minecraft.class_3532;

public class AlignmentLerp extends Lerp<Alignment> {

    public AlignmentLerp(Alignment start, Alignment end) {
        super(start, end);
    }

    @Override
    protected Alignment at(double t) {
        return Alignment.of(
            class_3532.method_16436(t, this.start.horizontal(), this.end.horizontal()),
            class_3532.method_16436(t, this.start.vertical(), this.end.vertical())
        );
    }
}
