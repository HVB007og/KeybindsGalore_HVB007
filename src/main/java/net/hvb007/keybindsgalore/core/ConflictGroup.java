package net.hvb007.keybindsgalore.core;

import java.util.List;
import java.util.Objects;

public record ConflictGroup(String physicalKeyId, List<Integer> bindingIndexes) {
    public ConflictGroup {
        Objects.requireNonNull(physicalKeyId, "physicalKeyId");
        bindingIndexes = List.copyOf(bindingIndexes);
    }
}
