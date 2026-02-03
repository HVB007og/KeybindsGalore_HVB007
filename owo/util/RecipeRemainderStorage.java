package io.wispforest.owo.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public final class RecipeRemainderStorage {

    private RecipeRemainderStorage() {}

    private static final Map<class_2960, Map<class_1792, class_1799>> REMAINDERS = new HashMap<>();

    public static void store(class_2960 recipe, Map<class_1792, class_1799> remainders) {
        REMAINDERS.put(recipe, remainders);
    }

    public static boolean has(class_2960 recipe) {
        return REMAINDERS.containsKey(recipe);
    }

    public static Map<class_1792, class_1799> get(class_2960 recipe) {
        return REMAINDERS.get(recipe);
    }

    static {
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> REMAINDERS.clear());
    }
}
