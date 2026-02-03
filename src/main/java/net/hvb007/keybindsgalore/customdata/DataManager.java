package net.hvb007.keybindsgalore.customdata;

import net.hvb007.keybindsgalore.KeybindsGalore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Hashtable;

/**
 * Manages loading custom data for keybindings, such as custom names or colors.
 * This data is read from a separate file in a YAML-like format.
 */
public class DataManager {
    private final File dataFile;
    public final Hashtable<String, KeybindData> customData = new Hashtable<>();
    public boolean hasCustomData = true;

    public DataManager(Path dataFilePath, String dataFileName) {
        this.dataFile = dataFilePath.resolve(dataFileName).toFile();

        if (!this.dataFile.exists()) {
            this.hasCustomData = false;
            KeybindsGalore.LOGGER.warn("No custom keybind data file found!");
            return;
        }

        this.readDataFile();
    }

    /**
     * Reads the custom data file and populates the customData map.
     * The file format is expected to be:
     * "key.id.string":
     *     property_name = "value"
     */
    public void readDataFile() {
        this.hasCustomData = true;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(this.dataFile))) {
            String line;
            String currentKeybind = null;
            while ((line = fileReader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                // Lines ending with a colon are treated as headers (keybind IDs).
                if (line.trim().endsWith(":")) {
                    currentKeybind = line.replaceAll("[\":]+", "").trim();
                    this.customData.put(currentKeybind, new KeybindData());
                }
                // Other lines are treated as properties for the current keybind.
                else if (currentKeybind != null) {
                    String[] parts = line.trim().split("=", 2);
                    if (parts.length < 2) {
                        continue;
                    }

                    String key = parts[0].trim();
                    String value = parts[1].trim().replaceAll("\"", "");

                    KeybindData data = this.customData.get(currentKeybind);
                    if (data == null) continue;

                    switch (key) {
                        case "display_name" -> data.displayName = value;
                        case "sector_color" -> data.sectorColor = Integer.parseInt(value.replace("0x", ""), 16);
                        case "hide_category" -> data.hideCategory = Boolean.parseBoolean(value);
                        default -> KeybindsGalore.LOGGER.warn("Unknown custom data field: {}", key);
                    }
                }
            }
            KeybindsGalore.LOGGER.info("Custom keybind data file read successfully!");
        } catch (IOException e) {
            this.hasCustomData = false;
            KeybindsGalore.LOGGER.warn("IOException while reading custom data file: {}", e.getMessage());
        }
    }
}
