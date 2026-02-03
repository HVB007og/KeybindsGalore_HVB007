package io.wispforest.owo.braid.widgets.slider.slider;

import static net.minecraft.class_3532.field_29849;

import net.minecraft.class_3532;

public interface SliderFunction {
    double normalize(double value, double min, double max);

    double deNormalize(double normalizedValue, double min, double max);

    SliderFunction LINEAR = new SliderFunction() {
        @Override
        public double normalize(double value, double min, double max) {
            return (value - min) / (max - min);
        }

        @Override
        public double deNormalize(double normalizedValue, double min, double max) {
            return min + normalizedValue * (max - min);
        }
    };

    SliderFunction LOGARITHMIC = new SliderFunction() {

        @Override
        public double normalize(double value, double min, double max) {
            if (min <= 0) {
                var offset = field_29849 - min;
                min += offset;
                max += offset;
                value += offset;
            }

            value = class_3532.method_15350(value, min, max);

            var logMin = Math.log(min);
            var logMax = Math.log(max);

            if (logMin >= logMax) return (value - min) / (max - min);

            return (Math.log(value) - logMin) / (logMax - logMin);
        }

        @Override
        public double deNormalize(double normalizedValue, double min, double max) {
            if (min <= 0) {
                var offset = field_29849 - min;
                min += offset;
                max += offset;
            }

            var logMin = Math.log(min);
            var logMax = Math.log(max);

            var expValue = Math.exp(logMin + normalizedValue * (logMax - logMin));

            if (min <= 0 && max > min) expValue -= (field_29849 - min);

            return expValue;
        }
    };
}
