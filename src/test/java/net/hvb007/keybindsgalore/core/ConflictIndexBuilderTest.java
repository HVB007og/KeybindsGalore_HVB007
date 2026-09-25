package net.hvb007.keybindsgalore.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConflictIndexBuilderTest {
    private final ConflictIndexBuilder builder = new ConflictIndexBuilder();

    @Test
    void groupsBindingsByPhysicalKeyInEncounterOrder() {
        List<BindingSnapshot> bindings = List.of(
                new BindingSnapshot("key.a", "first", "movement"),
                new BindingSnapshot("key.b", "alone", "movement"),
                new BindingSnapshot("key.a", "second", "debug")
        );

        List<ConflictGroup> groups = builder.build(bindings, List.of());

        assertEquals(1, groups.size());
        assertEquals("key.a", groups.get(0).physicalKeyId());
        assertEquals(List.of(0, 2), groups.get(0).bindingIndexes());
    }

    @Test
    void filtersCategoriesWithoutCaseSensitivity() {
        List<BindingSnapshot> bindings = List.of(
                new BindingSnapshot("key.a", "first", "MOVEMENT"),
                new BindingSnapshot("key.a", "second", "movement")
        );

        List<ConflictGroup> groups = builder.build(bindings, List.of("Movement"));

        assertEquals(List.of(), groups);
    }

    @Test
    void acceptsNamespacedIdsAndLegacyLabels() {
        List<BindingSnapshot> bindings = List.of(
                new BindingSnapshot("key.a", "first", "minecraft:movement", "Movement"),
                new BindingSnapshot("key.a", "second", "minecraft:movement", "Movement")
        );

        assertEquals(List.of(), builder.build(bindings, List.of("minecraft:movement")));
        assertEquals(List.of(), builder.build(bindings, List.of("Movement")));
        assertEquals(1, builder.build(bindings, List.of()).size());
    }

    @Test
    void removesSingletonGroups() {
        List<BindingSnapshot> bindings = List.of(
                new BindingSnapshot("key.a", "first", "movement"),
                new BindingSnapshot("key.b", "second", "movement"),
                new BindingSnapshot("key.b", "third", "movement")
        );

        List<ConflictGroup> groups = builder.build(bindings, List.of());

        assertEquals(1, groups.size());
        assertEquals("key.b", groups.get(0).physicalKeyId());
        assertEquals(List.of(1, 2), groups.get(0).bindingIndexes());
    }

    @Test
    void returnsNoGroupsForNoBindings() {
        assertEquals(List.of(), builder.build(List.of(), List.of()));
    }
}
