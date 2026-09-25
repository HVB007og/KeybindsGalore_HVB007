package net.hvb007.keybindsgalore.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ConfigurationDocument {
    private ConfigurationDocument() {
    }

    public static ParseResult parse(List<String> lines) {
        Map<String, String> values = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();

        for (String line : lines) {
            if (line.trim().startsWith("#") || line.isBlank()) {
                continue;
            }

            String[] entry = line.split("=", 2);
            if (entry.length < 2) {
                continue;
            }

            String key = entry[0].trim().toUpperCase(java.util.Locale.ROOT);
            String value = entry[1].trim();
            if (key.isEmpty()) {
                errors.add("Configuration entry has an empty key: " + line);
                continue;
            }
            values.put(key, value);
        }

        return new ParseResult(values, errors);
    }

    public record ParseResult(Map<String, String> values, List<String> errors) {
        public ParseResult {
            values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
            errors = List.copyOf(errors);
        }
    }
}
