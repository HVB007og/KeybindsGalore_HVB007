package net.hvb007.keybindsgalore.api;

import java.util.Objects;

/**
 * A single invocable action.
 *
 * @param id            stable identifier, for example {@code key.jump}
 * @param displayName   human-readable label, for example {@code Jump}
 * @param categoryId    machine category id, for example {@code minecraft:movement}
 * @param categoryLabel English category label, for example {@code Movement}
 */
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
