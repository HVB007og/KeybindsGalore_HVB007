package net.hvb007.keybindsgalore.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ConflictIndexBuilder {
    public List<ConflictGroup> build(List<BindingSnapshot> bindings, Collection<String> filteredCategories) {
        if (bindings == null || bindings.isEmpty()) {
            return List.of();
        }

        Map<String, List<Integer>> indexesByPhysicalKey = new LinkedHashMap<>();
        for (int index = 0; index < bindings.size(); index++) {
            BindingSnapshot binding = bindings.get(index);
            if (matchesCategory(filteredCategories, binding)) {
                continue;
            }
            indexesByPhysicalKey.computeIfAbsent(binding.physicalKeyId(), ignored -> new ArrayList<>()).add(index);
        }

        List<ConflictGroup> groups = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : indexesByPhysicalKey.entrySet()) {
            if (entry.getValue().size() >= 2) {
                groups.add(new ConflictGroup(entry.getKey(), entry.getValue()));
            }
        }
        return List.copyOf(groups);
    }

    private boolean matchesCategory(Collection<String> values, BindingSnapshot binding) {
        return containsIgnoreCase(values, binding.categoryId())
                || containsIgnoreCase(values, binding.categoryLabel());
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
