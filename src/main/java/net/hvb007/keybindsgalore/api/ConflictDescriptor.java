package net.hvb007.keybindsgalore.api;

import java.util.List;
import java.util.Objects;

public record ConflictDescriptor(String physicalKeyId, List<BindingDescriptor> bindings) {
    public ConflictDescriptor {
        Objects.requireNonNull(physicalKeyId, "physicalKeyId");
        bindings = List.copyOf(bindings);
        if (bindings.size() < 2) {
            throw new IllegalArgumentException("A conflict must contain at least two bindings");
        }
    }
}
