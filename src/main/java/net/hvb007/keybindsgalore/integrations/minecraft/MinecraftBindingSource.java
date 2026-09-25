package net.hvb007.keybindsgalore.integrations.minecraft;

import net.hvb007.keybindsgalore.api.ActionDescriptor;
import net.hvb007.keybindsgalore.api.BindingDescriptor;
import net.hvb007.keybindsgalore.api.BindingSource;
import net.hvb007.keybindsgalore.api.InvocationRequest;
import net.hvb007.keybindsgalore.api.InvocationResult;
import net.hvb007.keybindsgalore.core.BindingSnapshot;
import net.hvb007.keybindsgalore.input.minecraft.MinecraftBindingCatalog;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class MinecraftBindingSource implements BindingSource {
    public static final String SOURCE_ID = "minecraft";

    private final KeyMapping[] keyMappings;
    private final MinecraftBindingCatalog catalog = new MinecraftBindingCatalog();

    public MinecraftBindingSource(KeyMapping[] keyMappings) {
        this.keyMappings = keyMappings.clone();
    }

    @Override
    public String sourceId() {
        return SOURCE_ID;
    }

    @Override
    public List<BindingDescriptor> discoverBindings() {
        return catalog.collect(keyMappings).stream()
                .map(entry -> toBinding(entry.snapshot()))
                .toList();
    }

    @Override
    public InvocationResult invoke(InvocationRequest request) {
        return InvocationResult.REJECTED;
    }

    private BindingDescriptor toBinding(BindingSnapshot snapshot) {
        return new BindingDescriptor(
                SOURCE_ID,
                snapshot.physicalKeyId(),
                new ActionDescriptor(
                        snapshot.actionId(),
                        Component.translatable(snapshot.actionId()).getString(),
                        snapshot.categoryId(),
                        snapshot.categoryLabel()
                )
        );
    }
}
