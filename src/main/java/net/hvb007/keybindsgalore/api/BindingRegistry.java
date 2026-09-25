package net.hvb007.keybindsgalore.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public final class BindingRegistry implements KeybindApi {
    private final List<BindingSource> sources = new CopyOnWriteArrayList<>();

    @Override
    public ApiVersion version() {
        return ApiVersion.current();
    }

    @Override
    public void registerSource(BindingSource source) {
        Objects.requireNonNull(source, "source");
        String sourceId = source.sourceId();
        if (sourceId == null || sourceId.isBlank()) {
            throw new IllegalArgumentException("sourceId must not be blank");
        }
        for (BindingSource registered : sources) {
            if (sourceId.equals(registered.sourceId())) {
                throw new IllegalStateException("Binding source already registered: " + sourceId);
            }
        }
        sources.add(source);
    }

    @Override
    public boolean unregisterSource(String sourceId) {
        Objects.requireNonNull(sourceId, "sourceId");
        return sources.removeIf(source -> sourceId.equals(source.sourceId()));
    }

    @Override
    public List<BindingDescriptor> discoverBindings() {
        List<BindingDescriptor> bindings = new ArrayList<>();
        Set<BindingKey> seen = new HashSet<>();
        for (BindingSource source : sources) {
            List<BindingDescriptor> sourceBindings = source.discoverBindings();
            if (sourceBindings == null) {
                throw new IllegalStateException("Binding source returned null: " + source.sourceId());
            }
            for (BindingDescriptor binding : sourceBindings) {
                validateBinding(source, binding);
                BindingKey key = new BindingKey(binding.sourceId(), binding.physicalKeyId(), binding.action().id());
                if (!seen.add(key)) {
                    throw new IllegalStateException("Duplicate binding: " + key);
                }
                bindings.add(binding);
            }
        }
        return List.copyOf(bindings);
    }

    @Override
    public List<ConflictDescriptor> discoverConflicts() {
        Map<String, List<BindingDescriptor>> groups = new LinkedHashMap<>();
        for (BindingDescriptor binding : discoverBindings()) {
            groups.computeIfAbsent(binding.physicalKeyId(), ignored -> new ArrayList<>()).add(binding);
        }

        List<ConflictDescriptor> conflicts = new ArrayList<>();
        for (List<BindingDescriptor> group : groups.values()) {
            if (group.size() > 1) {
                conflicts.add(new ConflictDescriptor(group.getFirst().physicalKeyId(), group));
            }
        }
        return List.copyOf(conflicts);
    }

    @Override
    public InvocationResult invoke(InvocationRequest request) {
        Objects.requireNonNull(request, "request");
        BindingSource source = findSource(request.sourceId());
        if (source == null) {
            return InvocationResult.UNKNOWN_SOURCE;
        }

        boolean knownBinding = discoverBindings().stream()
                .anyMatch(binding -> binding.sourceId().equals(request.sourceId())
                        && binding.action().id().equals(request.actionId())
                        && binding.physicalKeyId().equals(request.physicalKeyId()));
        if (!knownBinding) {
            return InvocationResult.UNKNOWN_BINDING;
        }

        try {
            InvocationResult result = source.invoke(request);
            return result == null ? InvocationResult.REJECTED : result;
        } catch (RuntimeException ignored) {
            return InvocationResult.REJECTED;
        }
    }

    private BindingSource findSource(String sourceId) {
        for (BindingSource source : sources) {
            if (sourceId.equals(source.sourceId())) {
                return source;
            }
        }
        return null;
    }

    private void validateBinding(BindingSource source, BindingDescriptor binding) {
        Objects.requireNonNull(binding, "binding");
        if (!source.sourceId().equals(binding.sourceId())) {
            throw new IllegalStateException("Binding source ID does not match registered source: " + source.sourceId());
        }
    }

    private record BindingKey(String sourceId, String physicalKeyId, String actionId) {
    }
}
