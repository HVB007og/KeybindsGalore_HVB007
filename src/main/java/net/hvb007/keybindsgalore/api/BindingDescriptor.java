package net.hvb007.keybindsgalore.api;

import java.util.Objects;

public record BindingDescriptor(String sourceId, String physicalKeyId, ActionDescriptor action) {
    public BindingDescriptor {
        sourceId = requireText(sourceId, "sourceId");
        physicalKeyId = requireText(physicalKeyId, "physicalKeyId");
        Objects.requireNonNull(action, "action");
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
