package net.hvb007.keybindsgalore.api;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BindingRegistryTest {
    @Test
    void registersSourcesAndDiscoversConflicts() {
        TestSource first = source("first", binding("first", "key.jump", "space"));
        TestSource second = source("second", binding("second", "key.forward", "space"));
        BindingRegistry registry = new BindingRegistry();

        registry.registerSource(first);
        registry.registerSource(second);

        assertEquals(new ApiVersion(1, 0), registry.version());
        assertEquals(1, registry.discoverConflicts().size());
        assertEquals(2, registry.discoverConflicts().getFirst().bindings().size());
    }

    @Test
    void invokesOnlyKnownExactBinding() {
        TestSource source = source("first", binding("first", "key.jump", "space"));
        BindingRegistry registry = new BindingRegistry();
        registry.registerSource(source);

        InvocationRequest request = new InvocationRequest("first", "key.jump", "space");
        assertEquals(InvocationResult.SUCCEEDED, registry.invoke(request));
        assertEquals(List.of(request), source.requests);

        assertEquals(InvocationResult.UNKNOWN_BINDING,
                registry.invoke(new InvocationRequest("first", "key.jump", "enter")));
        assertEquals(1, source.requests.size());
    }

    @Test
    void rejectsDuplicateSourceIds() {
        BindingRegistry registry = new BindingRegistry();
        registry.registerSource(source("first"));

        assertThrows(IllegalStateException.class,
                () -> registry.registerSource(source("first")));
    }

    @Test
    void rejectsDuplicateBindingsFromOneSource() {
        TestSource source = source("first",
                binding("first", "key.jump", "space"),
                binding("first", "key.jump", "space"));
        BindingRegistry registry = new BindingRegistry();
        registry.registerSource(source);

        assertThrows(IllegalStateException.class, registry::discoverBindings);
    }

    @Test
    void unregisterStopsDiscovery() {
        TestSource source = source("first", binding("first", "key.jump", "space"));
        BindingRegistry registry = new BindingRegistry();
        registry.registerSource(source);

        assertTrue(registry.unregisterSource("first"));
        assertFalse(registry.unregisterSource("first"));
        assertTrue(registry.discoverBindings().isEmpty());
    }

    private static TestSource source(String id, BindingDescriptor... bindings) {
        return new TestSource(id, List.of(bindings));
    }

    private static BindingDescriptor binding(String sourceId, String actionId, String physicalKeyId) {
        return new BindingDescriptor(
                sourceId,
                physicalKeyId,
                new ActionDescriptor(actionId, actionId, "test", "Test")
        );
    }

    private static final class TestSource implements BindingSource {
        private final String id;
        private final List<BindingDescriptor> bindings;
        private final List<InvocationRequest> requests = new ArrayList<>();

        private TestSource(String id, List<BindingDescriptor> bindings) {
            this.id = id;
            this.bindings = bindings;
        }

        @Override
        public String sourceId() {
            return id;
        }

        @Override
        public List<BindingDescriptor> discoverBindings() {
            return bindings;
        }

        @Override
        public InvocationResult invoke(InvocationRequest request) {
            requests.add(request);
            return InvocationResult.SUCCEEDED;
        }
    }
}
