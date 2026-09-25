package net.hvb007.keybindsgalore.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public final class ConfigurationPersistence {
    private ConfigurationPersistence() {
    }

    public static void write(Path target, List<String> lines) throws IOException {
        Path absoluteTarget = target.toAbsolutePath();
        Path parent = absoluteTarget.getParent();
        if (parent == null) {
            throw new IOException("Configuration target has no parent: " + target);
        }
        Files.createDirectories(parent);
        Path temporary = Files.createTempFile(parent, absoluteTarget.getFileName().toString() + ".", ".tmp");
        try {
            Files.writeString(
                    temporary,
                    String.join("\n", lines) + "\n",
                    StandardCharsets.UTF_8
            );
            try {
                Files.move(
                        temporary,
                        absoluteTarget,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, absoluteTarget, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
