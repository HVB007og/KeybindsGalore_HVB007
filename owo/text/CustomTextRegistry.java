package io.wispforest.owo.text;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_5699;
import net.minecraft.class_7417;

public final class CustomTextRegistry {

    private static final Map<String, MapCodec<? extends class_7417>> TYPES = new HashMap<>();
    private static class_5699.class_10388<String, MapCodec<? extends class_7417>> codecIdMapper;

    private CustomTextRegistry() {}

    public static void register(String triggerField, MapCodec<? extends class_7417> codec) {
        TYPES.put(triggerField, codec);
        if (codecIdMapper != null) {
            codecIdMapper.method_65325(triggerField, codec);
        }
    }

    @ApiStatus.Internal
    public static void inject(class_5699.class_10388<String, MapCodec<? extends class_7417>> mapper) {
        TYPES.forEach(mapper::method_65325);
        codecIdMapper = mapper;
    }
}
