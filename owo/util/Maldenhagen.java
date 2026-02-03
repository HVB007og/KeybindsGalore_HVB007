package io.wispforest.owo.util;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.class_2248;

/**
 * A simple utility class for making ore blocks update after they are generated.
 * This is especially useful for ores that are supposed to glow, as with the normal
 * ore feature they won't do that since lighting is never calculated for them
 */
public final class Maldenhagen {

    private Maldenhagen() {}

    private static final Set<class_2248> COPIUM_INJECTED = new HashSet<>();

    /**
     * Marks a block for update after generation
     *
     * @param block The block to update
     */
    public static void injectCopium(class_2248 block) {
        COPIUM_INJECTED.add(block);
    }

    /**
     * @param block The block to test
     * @return {@code true} if the block should update after generation
     */
    public static boolean isOnCopium(class_2248 block) {
        return COPIUM_INJECTED.contains(block);
    }

}
