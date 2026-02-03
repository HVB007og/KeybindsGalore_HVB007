package io.wispforest.owo.compat.modmenu;

import com.google.common.collect.ForwardingMap;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.wispforest.owo.config.ui.ConfigScreenProviders;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_156;

@ApiStatus.Internal
public class OwoModMenuPlugin implements ModMenuApi {

    private static final Map<String, ConfigScreenFactory<?>> OWO_FACTORIES = new ForwardingMap<>() {
        @Override
        protected @NotNull Map<String, ConfigScreenFactory<?>> delegate() {
            return class_156.method_654(
                    new HashMap<>(),
                    map -> ConfigScreenProviders.forEach((s, provider) -> map.put(s, provider::apply))
            );
        }
    };

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return OWO_FACTORIES;
    }
}
