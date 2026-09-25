package net.hvb007.keybindsgalore.core;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriorityResolverTest {
    private final PriorityResolver resolver = new PriorityResolver();

    @Test
    void directPriorityWinsAfterCategoryCandidate() {
        List<PriorityCandidate> candidates = List.of(
                new PriorityCandidate("category.action", "movement"),
                new PriorityCandidate("key.jump", "movement")
        );

        OptionalInt result = resolver.resolve(
                candidates,
                "key.keyboard.space",
                List.of("KEY.JUMP:KEY.KEYBOARD.SPACE"),
                List.of("movement")
        );

        assertEquals(OptionalInt.of(1), result);
    }

    @Test
    void firstCategoryCandidateIsUsedAsFallback() {
        List<PriorityCandidate> candidates = List.of(
                new PriorityCandidate("first.action", "movement"),
                new PriorityCandidate("second.action", "movement")
        );

        OptionalInt result = resolver.resolve(
                candidates,
                "key.keyboard.w",
                List.of(),
                List.of("MOVEMENT")
        );

        assertEquals(OptionalInt.of(0), result);
    }

    @Test
    void acceptsNamespacedIdsAndLegacyLabels() {
        List<PriorityCandidate> candidates = List.of(
                new PriorityCandidate("first.action", "minecraft:movement", "Movement")
        );

        OptionalInt byId = resolver.resolve(
                candidates,
                "key.keyboard.w",
                List.of(),
                List.of("minecraft:movement")
        );
        OptionalInt byLabel = resolver.resolve(
                candidates,
                "key.keyboard.w",
                List.of(),
                List.of("Movement")
        );

        assertEquals(OptionalInt.of(0), byId);
        assertEquals(OptionalInt.of(0), byLabel);
    }

    @Test
    void returnsEmptyWhenNoPriorityMatches() {
        List<PriorityCandidate> candidates = List.of(
                new PriorityCandidate("first.action", "movement"),
                new PriorityCandidate("second.action", "debug")
        );

        OptionalInt result = resolver.resolve(
                candidates,
                "key.keyboard.w",
                List.of("other.action:key.keyboard.w"),
                List.of("other")
        );

        assertEquals(OptionalInt.empty(), result);
    }

    @Test
    void returnsEmptyForNoCandidates() {
        OptionalInt result = resolver.resolve(
                List.of(),
                "key.keyboard.w",
                List.of("action:key.keyboard.w"),
                List.of()
        );

        assertEquals(OptionalInt.empty(), result);
    }
}
