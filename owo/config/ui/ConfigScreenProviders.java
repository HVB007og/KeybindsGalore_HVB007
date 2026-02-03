package io.wispforest.owo.config.ui;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.class_437;

public class ConfigScreenProviders {

    private static final Map<String, Function<class_437, ? extends class_437>> PROVIDERS = new HashMap<>();
    private static final Map<String, Function<class_437, ? extends ConfigScreen>> OWO_SCREEN_PROVIDERS = new HashMap<>();

    /**
     * Register the given config screen provider. This is primarily
     * used for making a config screen available in ModMenu and to the
     * {@code /owo-config} command, although other places my use it as well
     *
     * @param modId    The mod id for which to supply a config screen
     * @param supplier The supplier to register - this gets the parent screen
     *                 as argument
     * @throws IllegalArgumentException If a config screen provider is
     *                                  already registered for the given mod id
     */
    public static <S extends class_437> void register(String modId, Function<class_437, S> supplier) {
        if (PROVIDERS.put(modId, supplier) != null) {
            throw new IllegalArgumentException("Tried to register config screen provider for mod id " + modId + " twice");
        }
    }

    /**
     * Get the config screen provider associated with
     * the given mod id
     *
     * @return The associated config screen provider, or {@code null} if
     * none is registered
     */
    public static @Nullable Function<class_437, ? extends class_437> get(String modId) {
        return PROVIDERS.get(modId);
    }

    public static void forEach(BiConsumer<String, Function<class_437, ? extends class_437>> action) {
        PROVIDERS.forEach(action);
    }
}
