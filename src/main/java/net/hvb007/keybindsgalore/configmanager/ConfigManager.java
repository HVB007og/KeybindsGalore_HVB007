/**
 * This class is based on LiteConfig (MIT license).
 * It has been adapted for use in Keybinds Galore.
 */
package net.hvb007.keybindsgalore.configmanager;

import net.hvb007.keybindsgalore.Configurations;
import net.hvb007.keybindsgalore.KeybindsGalore;
import net.hvb007.keybindsgalore.config.ConfigurationCodec;
import net.hvb007.keybindsgalore.config.ConfigurationDocument;
import net.hvb007.keybindsgalore.config.ConfigurationMigrator;
import net.hvb007.keybindsgalore.config.ConfigurationPersistence;
import net.hvb007.keybindsgalore.config.ConfigurationSnapshotCodec;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages reading the .properties config file and applying its values
 * to the fields in the Configurations class using reflection.
 */
public class ConfigManager {
    private final String name;
    private final Path configFileDirectory;
    private final String configFileName;
    private final Class<?> configurableClass;
    private final Object configurableClassInstance;

    private File configFile;
    public boolean errorFlag = false;

    /**
     * @param name                      Name of the application, used in logging.
     * @param configFilePath            Path to the config directory.
     * @param configFileName            Name of the config file (e.g., "mod.properties").
     * @param configurableClass         The Class object that holds the config fields.
     * @param configurableClassInstance Instance of the configurable class; null if fields are static.
     */
    public ConfigManager(
            String name, Path configFilePath, String configFileName,
            Class<?> configurableClass,
            Object configurableClassInstance
    ) throws IOException {
        this.name = name;
        this.configFileDirectory = configFilePath;
        this.configFileName = configFileName;
        this.configurableClass = configurableClass;
        this.configurableClassInstance = configurableClassInstance;

        this.checkConfigFileExists();
        this.readConfigFile();
    }

    /**
     * Checks if the config file exists. If not, it copies the default
     * config file from the mod's resources into the config directory.
     */
    public void checkConfigFileExists() throws IOException {
        this.configFile = this.configFileDirectory.resolve(this.configFileName).toFile();

        if (!this.configFile.exists()) {
            try (
                    InputStream defaultConfigStream = this.getClass().getResourceAsStream("/" + this.configFileName);
                    FileOutputStream fos = new FileOutputStream(this.configFile)
            ) {
                if (defaultConfigStream == null) {
                    // If no default file is found, create an empty one.
                    this.configFile.createNewFile();
                    KeybindsGalore.LOGGER.warn("Default config file not found in resources. Creating an empty one.");
                    return;
                }

                this.configFile.createNewFile();
                KeybindsGalore.LOGGER.info("Config file not found. Copying default config.");
                defaultConfigStream.transferTo(fos);
            } catch (IOException e) {
                KeybindsGalore.LOGGER.error("IOException while copying default config file!", e);
                throw e;
            }
        }
    }

    /**
     * Reads the config file line by line, parsing key-value pairs
     * and setting the corresponding fields in the Configurations class.
     */
    public void readConfigFile() throws IOException {
        this.errorFlag = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(this.configFile))) {
            ConfigurationDocument.ParseResult parsed = ConfigurationDocument.parse(reader.lines().toList());
            for (String error : parsed.errors()) {
                KeybindsGalore.LOGGER.error(error);
                this.errorFlag = true;
            }

            Map<String, String> migrated = ConfigurationMigrator.migrate(parsed.values());
            if (isActiveConfigurations()) {
                for (String key : migrated.keySet()) {
                    if (!ConfigurationSnapshotCodec.isKnownKey(key)) {
                        KeybindsGalore.LOGGER.error("No matching config field found for entry: {}", key);
                        this.errorFlag = true;
                    }
                }
                try {
                    Configurations.apply(ConfigurationSnapshotCodec.fromMap(migrated, Configurations.snapshot()));
                    return;
                } catch (RuntimeException e) {
                    KeybindsGalore.LOGGER.error("Malformed configuration snapshot", e);
                    this.errorFlag = true;
                }
            }

            for (var entry : migrated.entrySet()) {
                if (isActiveConfigurations() && !ConfigurationSnapshotCodec.isKnownKey(entry.getKey())) {
                    continue;
                }
                try {
                    Field field = this.configurableClass.getDeclaredField(entry.getKey());
                    setField(field, entry.getValue());
                } catch (NoSuchFieldException e) {
                    KeybindsGalore.LOGGER.error("No matching config field found for entry: {}", entry.getKey());
                    this.errorFlag = true;
                } catch (Exception e) {
                    KeybindsGalore.LOGGER.error("Malformed config entry: {}", entry.getKey() + "=" + entry.getValue(), e);
                    this.errorFlag = true;
                }
            }
        } catch (IOException e) {
            KeybindsGalore.LOGGER.error("IOException while reading config file!", e);
            throw e;
        }
    }

    /**
     * Sets a field's value based on its type.
     */
    private void setField(Field field, String value) throws IllegalAccessException {
        Object parsedValue = ConfigurationCodec.parseValue(field.getType(), field.getGenericType(), value);
        field.set(this.configurableClassInstance, parsedValue);
    }

    /**
     * Prints all current configuration values to the log.
     * Useful for debugging.
     */
    public void printAllConfigs() {
        KeybindsGalore.LOGGER.info("Dumping current configurations:");
        for (Field f : this.configurableClass.getDeclaredFields()) {
            try {
                KeybindsGalore.LOGGER.info("\t{}: {}", f.getName(), f.get(this.configurableClassInstance));
            } catch (IllegalAccessException | NullPointerException ignored) {
            }
        }
    }

    /**
     * Saves the current configuration fields back to the .properties file.
     */
    public void saveConfigFile() {
        List<String> lines = new ArrayList<>();
        lines.add("# KeybindsGalore Configuration File");
        lines.add("# This file is automatically updated by the in-game GUI.");
        lines.add("");

        if (isActiveConfigurations()) {
            lines.addAll(ConfigurationSnapshotCodec.toLines(Configurations.snapshot()));
        } else {
            for (Field field : this.configurableClass.getDeclaredFields()) {
                try {
                    Object value = field.get(this.configurableClassInstance);
                    lines.add(ConfigurationCodec.formatValue(field.getName(), value));
                } catch (IllegalAccessException e) {
                    KeybindsGalore.LOGGER.error("Failed to access field: {}", field.getName(), e);
                }
            }
        }

        try {
            ConfigurationPersistence.write(this.configFile.toPath(), lines);
        } catch (IOException e) {
            KeybindsGalore.LOGGER.error("IOException while saving config file!", e);
        }
    }

    private boolean isActiveConfigurations() {
        return this.configurableClass == Configurations.class && this.configurableClassInstance == null;
    }
}
