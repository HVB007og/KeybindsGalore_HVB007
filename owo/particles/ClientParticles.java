package io.wispforest.owo.particles;

import io.wispforest.owo.util.VectorRandomUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_310;

/**
 * A wrapper for vanilla's terrible particle system that allows for easier
 * and more complex multi-particle operations
 */
@Environment(EnvType.CLIENT)
public final class ClientParticles {

    private static int particleCount = 1;
    private static boolean persist = false;

    private static class_243 velocity = new class_243(0, 0, 0);
    private static boolean randomizeVelocity = false;
    private static double randomVelocityScalar = 0;
    private static class_2350.class_2351 randomizationAxis = null;

    private ClientParticles() {}

    /**
     * Marks the values set by {@link ClientParticles#setParticleCount(int)} and {@link ClientParticles#setVelocity(class_243)} to be persistent
     */
    public static void persist() {
        ClientParticles.persist = true;
    }

    /**
     * How many particles to spawn per operation
     * <br><b>
     * Volatile unless {@link ClientParticles#persist()} is called before the next operation
     * </b>
     */
    public static void setParticleCount(int particleCount) {
        ClientParticles.particleCount = particleCount;
    }

    /**
     * The velocity added to each spawned particle
     * <br><b>
     * Volatile unless {@link ClientParticles#persist()} is called before the next operation
     * </b>
     */
    public static void setVelocity(class_243 velocity) {
        ClientParticles.velocity = velocity;
    }

    /**
     * Makes the system use a random velocity for each particle
     * <br><b>
     * Volatile unless {@link ClientParticles#persist()} is called before the next operation
     * </b>
     *
     * @param scalar The scalar to use for the generated velocities which
     *               nominally range from -0.5 to 0.5 on each axis
     */
    public static void randomizeVelocity(double scalar) {
        randomizeVelocity = true;
        randomVelocityScalar = scalar;
        randomizationAxis = null;
    }

    /**
     * Makes the system use a random velocity for each particle
     * <br><b>
     * Volatile unless {@link ClientParticles#persist()} is called before the next operation
     * </b>
     *
     * @param scalar The scalar to use for the generated velocities which
     *               nominally range from -0.5 to 0.5 on each axis
     * @param axis   The axis on which to apply random velocity
     */
    public static void randomizeVelocityOnAxis(double scalar, class_2350.class_2351 axis) {
        randomizeVelocity = true;
        randomVelocityScalar = scalar;
        randomizationAxis = axis;
    }

    /**
     * Forces a reset of velocity and particleCount
     */
    public static void reset() {
        persist = false;
        clearState();
    }

    private static void clearState() {
        if (persist) return;

        particleCount = 1;
        velocity = new class_243(0, 0, 0);

        randomizeVelocity = false;
    }

    private static void addParticle(class_2394 particle, class_1937 world, class_243 location) {
        if (randomizeVelocity) {
            if (randomizationAxis == null) {
                velocity = VectorRandomUtils.getRandomOffset(world, class_243.field_1353, randomVelocityScalar);
            } else {
                final var stopIt_getSomeHelp = (world.field_9229.method_43058() * 2 - 1) * randomVelocityScalar;
                velocity = switch (randomizationAxis) {
                    case field_11048 -> new class_243(stopIt_getSomeHelp, 0, 0);
                    case field_11052 -> new class_243(0, stopIt_getSomeHelp, 0);
                    case field_11051 -> new class_243(0, 0, stopIt_getSomeHelp);
                };
            }
        }

        world.method_8406(particle, location.field_1352, location.field_1351, location.field_1350, velocity.field_1352, velocity.field_1351, velocity.field_1350);
    }

    /**
     * Spawns particles with a maximum offset of {@code deviation} from the center of {@code pos}
     *
     * @param particle  The particle to spawn
     * @param world     The world to spawn the particles in, must be {@link net.minecraft.class_638}
     * @param pos       The block to center on
     * @param deviation The maximum deviation from the center of pos
     */
    public static void spawnCenteredOnBlock(class_2394 particle, class_1937 world, class_2338 pos, double deviation) {
        class_243 location;

        for (int i = 0; i < particleCount; i++) {
            location = VectorRandomUtils.getRandomCenteredOnBlock(world, pos, deviation);
            addParticle(particle, world, location);
        }

        clearState();
    }

    /**
     * Spawns particles randomly distributed within {@code pos}
     *
     * @param particle The particle to spawn
     * @param world    The world to spawn the particles in, must be {@link net.minecraft.class_638}
     * @param pos      The block to spawn particles in
     */
    public static void spawnWithinBlock(class_2394 particle, class_1937 world, class_2338 pos) {
        class_243 location;

        for (int i = 0; i < particleCount; i++) {
            location = VectorRandomUtils.getRandomWithinBlock(world, pos);
            addParticle(particle, world, location);
        }

        clearState();
    }

    /**
     * Spawns particles with a maximum offset of {@code deviation} from {@code pos + offset}
     *
     * @param particle  The particle to spawn
     * @param world     The world to spawn the particles in, must be {@link net.minecraft.class_638}
     * @param pos       The base position
     * @param offset    The offset from {@code pos}
     * @param deviation The scalar for random distribution
     */
    public static void spawnWithOffsetFromBlock(class_2394 particle, class_1937 world, class_2338 pos, class_243 offset, double deviation) {
        class_243 location;
        offset = offset.method_1019(class_243.method_24954(pos));

        for (int i = 0; i < particleCount; i++) {
            location = VectorRandomUtils.getRandomOffset(world, offset, deviation);

            addParticle(particle, world, location);
        }

        clearState();
    }

    /**
     * Spawns particles at the given location with a maximum offset of {@code deviation}
     *
     * @param particle  The particle to spawn
     * @param world     The world to spawn the particles in, must be {@link net.minecraft.class_638}
     * @param pos       The base position
     * @param deviation The scalar from random distribution
     */
    public static void spawn(class_2394 particle, class_1937 world, class_243 pos, double deviation) {
        class_243 location;

        for (int i = 0; i < particleCount; i++) {
            location = VectorRandomUtils.getRandomOffset(world, pos, deviation);
            addParticle(particle, world, location);
        }

        clearState();
    }

    /**
     * Spawns particles at the given location with a maximum offset of {@code deviation}
     *
     * @param particle   The particle to spawn
     * @param world      The world to spawn the particles in, must be {@link net.minecraft.class_638}
     * @param pos        The base position
     * @param deviationX The scalar from random distribution on x
     * @param deviationY The scalar from random distribution on y
     * @param deviationZ The scalar from random distribution on z
     */
    public static void spawnPrecise(class_2394 particle, class_1937 world, class_243 pos, double deviationX, double deviationY, double deviationZ) {
        class_243 location;

        for (int i = 0; i < particleCount; i++) {
            location = VectorRandomUtils.getRandomOffsetSpecific(world, pos, deviationX, deviationY, deviationZ);
            addParticle(particle, world, location);
        }

        clearState();
    }

    /**
     * Spawns enchant particles travelling from origin to destination
     *
     * @param world       The world to spawn the particles in, must be {@link net.minecraft.class_638}
     * @param origin      The origin of the particle stream
     * @param destination The destination of the particle stream
     * @param deviation   The scalar for random distribution around {@code origin}
     */
    public static void spawnEnchantParticles(class_1937 world, class_243 origin, class_243 destination, float deviation) {

        class_243 location;
        class_243 particleVector = origin.method_1020(destination);

        for (int i = 0; i < particleCount; i++) {
            location = VectorRandomUtils.getRandomOffset(world, particleVector, deviation);
            world.method_8406(class_2398.field_11215, destination.field_1352, destination.field_1351, destination.field_1350, location.field_1352, location.field_1351, location.field_1350);
        }

        clearState();
    }

    /**
     * Spawns a particle at the given location with a lifetime of {@code maxAge}
     *
     * @param particleType The type of the particle to spawn
     * @param pos          The position to spawn at
     * @param maxAge       The maxAge to set for the spawned particle
     */
    @SuppressWarnings("ConstantConditions")
    public static <T extends class_2394> void spawnWithMaxAge(T particleType, class_243 pos, int maxAge) {
        var particle = class_310.method_1551().field_1713.method_3056(particleType, pos.field_1352, pos.field_1351, pos.field_1350, velocity.field_1352, velocity.field_1351, velocity.field_1350);
        if (particle == null) {
            return;
        }

        particle.method_3077(maxAge);
        clearState();
    }

    /**
     * Spawns a line of particles going from {@code start} to {@code end}
     *
     * @param particle  The particle to spawn
     * @param world     The world to spawn the particles in, must be {@link net.minecraft.class_638}
     * @param start     The line's origin
     * @param end       The line's end point
     * @param deviation A random offset from the line that particles can have
     */
    public static void spawnLine(class_2394 particle, class_1937 world, class_243 start, class_243 end, float deviation) {
        spawnLineInner(particle, world, start, end, deviation);
        clearState();
    }

    /**
     * Spawns a cube outline starting at {@code origin} and expanding by {@code size} in positive
     * direction on all axis
     *
     * @param particle  The particle to spawn
     * @param world     The world to spawn the particles in, must be {@link net.minecraft.class_638}
     * @param origin    The cube's origin
     * @param size      The cube's side length
     * @param deviation A random offset from the line that particles can have
     */
    public static void spawnCubeOutline(class_2394 particle, class_1937 world, class_243 origin, float size, float deviation) {

        spawnLineInner(particle, world, origin, origin.method_1031(size, 0, 0), deviation);
        spawnLineInner(particle, world, origin.method_1031(size, 0, 0), origin.method_1031(size, 0, size), deviation);

        spawnLineInner(particle, world, origin, origin.method_1031(0, 0, size), deviation);
        spawnLineInner(particle, world, origin.method_1031(0, 0, size), origin.method_1031(size, 0, size), deviation);

        origin = origin.method_1031(0, size, 0);

        spawnLineInner(particle, world, origin, origin.method_1031(size, 0, 0), deviation);
        spawnLineInner(particle, world, origin.method_1031(size, 0, 0), origin.method_1031(size, 0, size), deviation);

        spawnLineInner(particle, world, origin, origin.method_1031(0, 0, size), deviation);
        spawnLineInner(particle, world, origin.method_1031(0, 0, size), origin.method_1031(size, 0, size), deviation);

        spawnLineInner(particle, world, origin, origin.method_1031(0, -size, 0), deviation);
        spawnLineInner(particle, world, origin.method_1031(size, 0, 0), origin.method_1031(size, -size, 0), deviation);
        spawnLineInner(particle, world, origin.method_1031(0, 0, size), origin.method_1031(0, -size, size), deviation);
        spawnLineInner(particle, world, origin.method_1031(size, 0, size), origin.method_1031(size, -size, size), deviation);

        clearState();
    }

    private static void spawnLineInner(class_2394 particle, class_1937 world, class_243 start, class_243 end, float deviation) {
        class_243 increment = end.method_1020(start).method_1021(1f / (float) particleCount);

        for (int i = 0; i < particleCount; i++) {
            start = VectorRandomUtils.getRandomOffset(world, start, deviation);
            addParticle(particle, world, start);
            start = start.method_1019(increment);
        }
    }

}
