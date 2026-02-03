package io.wispforest.owo.util;

import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_243;

/**
 * Utility class for getting random offsets within a {@link class_1937}
 */
public final class VectorRandomUtils {

    private VectorRandomUtils() {}

    /**
     * Generates a random point centered on the given block
     *
     * @param level     The level to operate in
     * @param pos       The block position to take the center from
     * @param deviation The size of cube from which positions are picked
     * @return A random point no further than {@code deviation} from the center of {@code pos}
     */
    public static class_243 getRandomCenteredOnBlock(class_1937 level, class_2338 pos, double deviation) {
        return getRandomOffset(level, new class_243(pos.method_10263() + 0.5, pos.method_10264() + 0.5, pos.method_10260() + 0.5), deviation);
    }

    /**
     * Generates a random point within the given block
     *
     * @param level The level to operate in
     * @param pos   The block in which to pick a point
     * @return A random point somewhere within the bounding box of {@code pos}
     */
    public static class_243 getRandomWithinBlock(class_1937 level, class_2338 pos) {
        return getRandomOffset(level, class_243.method_24954(pos).method_1031(0.5, 0.5, 0.5), 0.5);
    }

    /**
     * Generates a random point
     *
     * @param level     The level to operate in
     * @param center    The center point
     * @param deviation The size of cube from which positions are picked
     * @return A random point within a cube with side length of {@code deviation} centered on {@code center}
     */
    public static class_243 getRandomOffset(class_1937 level, class_243 center, double deviation) {
        return getRandomOffsetSpecific(level, center, deviation, deviation, deviation);
    }

    /**
     * Generates a random point offset from {@code center}
     *
     * @param level      The level to operate in
     * @param center     The center position to start with
     * @param deviationX The length of the selection cuboid on the x-axis
     * @param deviationY The length of the selection cuboid on the y-axis
     * @param deviationZ The length of the selection cuboid on the z-axis
     * @return The generated point
     */
    public static class_243 getRandomOffsetSpecific(class_1937 level, class_243 center, double deviationX, double deviationY, double deviationZ) {

        final var r = level.method_8409();

        double x = center.method_10216() + (r.method_43058() - 0.5) * deviationX;
        double y = center.method_10214() + (r.method_43058() - 0.5) * deviationY;
        double z = center.method_10215() + (r.method_43058() - 0.5) * deviationZ;

        return new class_243(x, y, z);
    }

}
