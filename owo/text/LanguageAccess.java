package io.wispforest.owo.text;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;
import net.minecraft.class_2561;

@ApiStatus.Internal
public class LanguageAccess {
    public static final BiConsumer<String, class_2561> EMPTY_CONSUMER = (string, component) -> {};

    public static ThreadLocal<BiConsumer<String, class_2561>> textConsumer = ThreadLocal.withInitial(() -> EMPTY_CONSUMER);
}
