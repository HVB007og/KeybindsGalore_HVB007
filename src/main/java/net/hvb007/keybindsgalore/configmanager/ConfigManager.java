/**
 * This class is based on LiteConfig (MIT license).
 * It has been adapted for use in Keybinds Galore.
 */
package net.hvb007.keybindsgalore.configmanager;

import net.hvb007.keybindsgalore.KeybindsGalore;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;

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
            for (String line : reader.lines().toArray(String[]::new)) {
                if (line.trim().startsWith("#") || line.isBlank()) {
                    continue;
                }

                String[] entry = line.split("=", 2);
                if (entry.length < 2) {
                    continue;
                }

                try {
                    String key = entry[0].trim().toUpperCase(Locale.ROOT);
                    String value = entry[1].trim();
                    Field field = this.configurableClass.getDeclaredField(key);
                    setField(field, value);
                } catch (NoSuchFieldException e) {
                    KeybindsGalore.LOGGER.error("No matching config field found for entry: {}", entry[0].trim());
                    this.errorFlag = true;
                } catch (Exception e) {
                    KeybindsGalore.LOGGER.error("Malformed config entry: {}", line, e);
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
        Class<?> type = field.getType();

        if (type == short.class) {
            field.setShort(this.configurableClassInstance, Short.parseShort(value.replace("0x", ""), 16));
        } else if (type == int.class) {
            field.setInt(this.configurableClassInstance, Integer.parseInt(value.replace("0x", ""), value.startsWith("0x") ? 16 : 10));
        } else if (type == float.class) {
            field.setFloat(this.configurableClassInstance, Float.parseFloat(value));
        } else if (type == boolean.class) {
            field.setBoolean(this.configurableClassInstance, Boolean.parseBoolean(value));
        } else if (type == ArrayList.class) {
            ArrayList<Integer> list = new ArrayList<>();
            String[] values = value.replaceAll("[\\[\\]\\s]+", "").split(",");
            if (values.length == 1 && values[0].isEmpty()) {
                // Handle empty list case
            } else {
                for (String s : values) {
                    list.add(Integer.parseInt(s.trim()));
                }
            }
            field.set(this.configurableClassInstance, list);
        } else {
            KeybindsGalore.LOGGER.error("Unrecognized data type for field: {}", field.getName());
        }
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
}
