package net.hvb007.keybindsgalore.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigurationPersistenceTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void atomicallyWritesAndReplacesNestedFiles() throws Exception {
        Path target = temporaryDirectory.resolve("nested/keybindsgalore.properties");

        ConfigurationPersistence.write(target, List.of("DEBUG=true"));
        assertEquals("DEBUG=true\n", Files.readString(target));

        ConfigurationPersistence.write(target, List.of("DEBUG=false", "CIRCLE_VERTICES=120"));
        assertEquals("DEBUG=false\nCIRCLE_VERTICES=120\n", Files.readString(target));
    }
}
