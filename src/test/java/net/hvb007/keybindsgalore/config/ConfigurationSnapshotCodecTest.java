package net.hvb007.keybindsgalore.config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationSnapshotCodecTest {
    @Test
    void roundTripsSnapshotValues() {
        ConfigurationSnapshot snapshot = ConfigurationSnapshot.defaults();

        ConfigurationSnapshot restored = ConfigurationSnapshotCodec.fromMap(
                ConfigurationSnapshotCodec.toMap(snapshot)
        );

        assertEquals(snapshot, restored);
    }

    @Test
    void usesFallbackForMissingValues() {
        ConfigurationSnapshot restored = ConfigurationSnapshotCodec.fromMap(
                Map.of("DEBUG", "true"),
                ConfigurationSnapshot.defaults()
        );

        assertTrue(restored.debug());
        assertEquals(120, restored.circleVertices());
    }

    @Test
    void serializesColorsAndAlphaUsingLegacyConventions() {
        var lines = ConfigurationSnapshotCodec.toLines(ConfigurationSnapshot.defaults());

        assertTrue(lines.contains("PIE_MENU_COLOR=0x00404040"));
        assertTrue(lines.contains("PIE_MENU_ALPHA=64"));
    }
}
