package net.hvb007.keybindsgalore.api;

import java.util.Objects;

public record InvocationRequest(String sourceId, String actionId, String physicalKeyId) {
    public InvocationRequest {
        sourceId = requireText(sourceId, "sourceId");
        actionId = requireText(actionId, "actionId");
        physicalKeyId = requireText(physicalKeyId, "physicalKeyId");
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
