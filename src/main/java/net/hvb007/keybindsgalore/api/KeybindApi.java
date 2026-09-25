package net.hvb007.keybindsgalore.api;

import java.util.List;

public interface KeybindApi {
    ApiVersion version();

    void registerSource(BindingSource source);

    boolean unregisterSource(String sourceId);

    List<BindingDescriptor> discoverBindings();

    List<ConflictDescriptor> discoverConflicts();

    InvocationResult invoke(InvocationRequest request);
}
