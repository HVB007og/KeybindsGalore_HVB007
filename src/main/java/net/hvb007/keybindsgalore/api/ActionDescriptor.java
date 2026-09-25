package net.hvb007.keybindsgalore.api;

import java.util.Objects;

public record ActionDescriptor(String id, String displayName, String categoryId, String categoryLabel) {
    public ActionDescriptor {
        id = requireText(id, "id");
        displayName = requireText(displayName, "displayName");
        categoryId = requireText(categoryId, "categoryId");
        categoryLabel = requireText(categoryLabel, "categoryLabel");
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
