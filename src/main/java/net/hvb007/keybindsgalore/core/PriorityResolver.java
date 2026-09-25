package net.hvb007.keybindsgalore.core;

import java.util.Collection;
import java.util.List;
import java.util.OptionalInt;

public final class PriorityResolver {
    public OptionalInt resolve(
            List<PriorityCandidate> candidates,
            String physicalKeyName,
            Collection<String> directPriorities,
            Collection<String> priorityCategories) {
        if (candidates == null || candidates.isEmpty()) {
            return OptionalInt.empty();
        }

        Integer categoryIndex = null;
        for (int index = 0; index < candidates.size(); index++) {
            PriorityCandidate candidate = candidates.get(index);
            String directPair = candidate.actionId() + ":" + physicalKeyName;
            if (containsIgnoreCase(directPriorities, directPair)) {
                return OptionalInt.of(index);
            }

            if (categoryIndex == null && matchesCategory(priorityCategories, candidate)) {
                categoryIndex = index;
            }
        }

        return categoryIndex == null ? OptionalInt.empty() : OptionalInt.of(categoryIndex);
    }

    private boolean matchesCategory(Collection<String> values, PriorityCandidate candidate) {
        return containsIgnoreCase(values, candidate.categoryId())
                || containsIgnoreCase(values, candidate.categoryLabel());
    }

    private boolean containsIgnoreCase(Collection<String> values, String target) {
        if (values == null || target == null) {
            return false;
        }
        for (String value : values) {
            if (target.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
