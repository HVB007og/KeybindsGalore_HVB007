package net.hvb007.keybindsgalore.core;

import java.util.Objects;

public record PriorityCandidate(String actionId, String categoryId, String categoryLabel) {
    public PriorityCandidate {
        Objects.requireNonNull(actionId, "actionId");
        Objects.requireNonNull(categoryId, "categoryId");
        Objects.requireNonNull(categoryLabel, "categoryLabel");
    }

    public PriorityCandidate(String actionId, String categoryId) {
        this(actionId, categoryId, categoryId);
    }
}
