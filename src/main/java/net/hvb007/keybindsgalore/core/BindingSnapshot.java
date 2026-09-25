package net.hvb007.keybindsgalore.core;

import java.util.Objects;

public record BindingSnapshot(String physicalKeyId, String actionId, String categoryId, String categoryLabel) {
    public BindingSnapshot {
        Objects.requireNonNull(physicalKeyId, "physicalKeyId");
        Objects.requireNonNull(actionId, "actionId");
        Objects.requireNonNull(categoryId, "categoryId");
        Objects.requireNonNull(categoryLabel, "categoryLabel");
    }

    public BindingSnapshot(String physicalKeyId, String actionId, String categoryId) {
        this(physicalKeyId, actionId, categoryId, categoryId);
    }
}
