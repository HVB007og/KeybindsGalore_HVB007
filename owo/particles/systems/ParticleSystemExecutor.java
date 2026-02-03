package io.wispforest.owo.particles.systems;

import net.minecraft.class_1937;
import net.minecraft.class_243;

public interface ParticleSystemExecutor<T> {
    /**
     * Called when particles should be displayed
     * at the given position in the given level,
     * with the given data as additional context
     *
     * @param level The level to display in
     * @param pos   The position to display at
     * @param data  The data to display with
     */
    void executeParticleSystem(class_1937 level, class_243 pos, T data);
}
