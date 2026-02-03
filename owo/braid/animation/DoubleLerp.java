package io.wispforest.owo.braid.animation;

import net.minecraft.class_3532;

public class DoubleLerp extends Lerp<Double> {

    public DoubleLerp(Double start, Double end) {
        super(start, end);
    }

    @Override
    protected Double at(double t) {
        return class_3532.method_16436(t, this.start, this.end);
    }
}
