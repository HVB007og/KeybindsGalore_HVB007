package net.hvb007.keybindsgalore.configmanager;

import net.hvb007.keybindsgalore.Configurations;
import net.hvb007.keybindsgalore.config.ConfigurationSnapshot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigManagerCompatibilityTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadsMigratesAndSavesActiveConfiguration() throws Exception {
        ConfigurationSnapshot original = Configurations.snapshot();
        Path file = temporaryDirectory.resolve("keybindsgalore.properties");
        Files.writeString(file, "FILTER_DEBUG_KEYS=true\nCIRCLE_VERTICES=42\n", StandardCharsets.UTF_8);

        try {
            ConfigManager manager = new ConfigManager(
                    "test",
                    temporaryDirectory,
                    "keybindsgalore.properties",
                    Configurations.class,
                    null
            );

            assertFalse(manager.errorFlag);
            assertEquals(42, Configurations.CIRCLE_VERTICES);
            assertEquals(List.of("Debug"), Configurations.FILTERED_CATEGORY_KEYS);

            manager.saveConfigFile();
            String saved = Files.readString(file, StandardCharsets.UTF_8);
            assertTrue(saved.contains("CIRCLE_VERTICES=42"));
            assertTrue(saved.contains("FILTERED_CATEGORY_KEYS=[Debug]"));
            assertFalse(saved.contains("FILTER_DEBUG_KEYS"));
        } finally {
            Configurations.apply(original);
        }
    }
}
