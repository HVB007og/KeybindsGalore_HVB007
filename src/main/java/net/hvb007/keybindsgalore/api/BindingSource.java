package net.hvb007.keybindsgalore.api;

import java.util.List;

public interface BindingSource {
    String sourceId();

    List<BindingDescriptor> discoverBindings();

    InvocationResult invoke(InvocationRequest request);
}
