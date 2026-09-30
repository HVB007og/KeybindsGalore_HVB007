package net.hvb007.keybindsgalore.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConflictReportTest {

    private static ConflictReport.Row row(String key, String... bindings) {
        return new ConflictReport.Row(key, List.of(bindings), false);
    }

    @Test
    void headerCountsPairsNotBindings() {
        // Three actions on one key is three pairs, not two. Undercounting here would make a
        // dump look smaller than the conflict count the mod actually reports at runtime.
        String header = ConflictReport.header(800, List.of(
                row("key.keyboard.w", "Jump", "Pick Block", "Mod: Attack"),
                row("key.keyboard.j", "Open Journal", "Screenshot")));

        assertTrue(header.contains("registered keybinds=800"), header);
        assertTrue(header.contains("conflicting physical keys=2"), header);
        assertTrue(header.contains("conflicting pairs=3"), header);
    }

    @Test
    void everyBindingOnAKeyIsListed() {
        String line = ConflictReport.render(row("key.keyboard.w", "Jump", "Pick Block"));

        assertTrue(line.contains("Jump"), line);
        assertTrue(line.contains("Pick Block"), line);
        assertTrue(line.contains("<->"), line);
    }

    @Test
    void priorityResolvedKeysAreMarked() {
        // Without this a dump lists conflicts that are silently handled by a priority, and the
        // reader cannot tell which rows would actually open a menu.
        String line = ConflictReport.render(
                new ConflictReport.Row("key.keyboard.space", List.of("Jump", "Use"), true));

        assertTrue(line.contains("no menu will open"), line);
    }

    @Test
    void unmarkedRowCarriesNoPriorityNote() {
        String line = ConflictReport.render(row("key.keyboard.w", "Jump", "Pick Block"));

        assertTrue(!line.contains("no menu will open"), line);
    }

    @Test
    void rowsAreSortedByPhysicalKey() {
        List<String> lines = ConflictReport.renderAll(List.of(
                row("key.keyboard.z", "A", "B"),
                row("key.keyboard.a", "C", "D"),
                row("key.keyboard.m", "E", "F")));

        assertEquals("key.keyboard.a", lines.get(0).trim().split(" ")[0]);
        assertEquals("key.keyboard.m", lines.get(1).trim().split(" ")[0]);
        assertEquals("key.keyboard.z", lines.get(2).trim().split(" ")[0]);
    }

    @Test
    void singleBindingIsNotCountedAsAConflictPair() {
        // Defensive: a row with one binding has zero pairs, and a negative count in the header
        // would be nonsense.
        String header = ConflictReport.header(10, List.of(row("key.keyboard.q", "Only")));

        assertTrue(header.contains("conflicting pairs=0"), header);
    }
}
