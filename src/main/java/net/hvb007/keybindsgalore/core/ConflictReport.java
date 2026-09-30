package net.hvb007.keybindsgalore.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Formats the conflict table into log lines.
 *
 * <p>Pure Java with no Minecraft dependency, so the output can be asserted in a unit test. The
 * alternative was building the strings inline in a logger call, which means the only way to check
 * the shape of the dump is to run the game and read a log.
 *
 * <p>Rows are sorted by physical key so that two runs produce comparable output. That matters
 * because the point of the dump is to diff vanilla's default conflicts against a later change.
 */
public final class ConflictReport {
    private ConflictReport() {
    }

    /**
     * One physical key and every action bound to it.
     *
     * @param resolvedByPriority a direct or category priority already decides this key, so the
     *                           menu will never open for it. Worth flagging, because a dump that
     *                           lists a conflict as live when it is silently handled is misleading.
     */
    public record Row(String keyName, List<String> bindings, boolean resolvedByPriority) {
        public Row {
            bindings = List.copyOf(bindings);
        }
    }

    private static final String SEPARATOR = "  <->  ";
    private static final int KEY_COLUMN = 32;

    public static String header(int registeredBindings, List<Row> rows) {
        int pairs = 0;
        for (Row row : rows) {
            pairs += Math.max(0, row.bindings().size() - 1);
        }
        return "registered keybinds=" + registeredBindings
                + "  conflicting physical keys=" + rows.size()
                + "  conflicting pairs=" + pairs;
    }

    public static String render(Row row) {
        StringBuilder line = new StringBuilder();
        line.append(pad(row.keyName()));
        line.append(String.join(SEPARATOR, row.bindings()));
        if (row.resolvedByPriority()) {
            line.append("   [already resolved by a priority: no menu will open]");
        }
        return line.toString();
    }

    /** Sorted by physical key, so successive dumps are directly comparable. */
    public static List<String> renderAll(List<Row> rows) {
        List<Row> sorted = new ArrayList<>(rows);
        sorted.sort(Comparator.comparing(Row::keyName));
        List<String> lines = new ArrayList<>(sorted.size());
        for (Row row : sorted) {
            lines.add(render(row));
        }
        return lines;
    }

    private static String pad(String keyName) {
        if (keyName.length() >= KEY_COLUMN) {
            return keyName + " ";
        }
        return keyName + " ".repeat(KEY_COLUMN - keyName.length());
    }
}
