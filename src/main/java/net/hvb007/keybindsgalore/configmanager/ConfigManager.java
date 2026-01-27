/**
 * Copied from LiteConfig (MIT license)
 */

package net.hvb007.keybindsgalore.configmanager;

import net.hvb007.keybindsgalore.KeybindsGalore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Configuration manager. Handles reading/saving config file, and setting fields in configurable class.
 */
public class ConfigManager
{
    private final String name; /** Application name, used to locate config file */
    private final Path configFileDirectory; /** Directory containing the config file */
    private final String configFileName; /** Name of the config file */
    private final Class<?> configurableClass; /** The Class object holding the config fields */
    private Object configurableClassInstance; /** The instance on which to set the config fields (if instance fields are used) */

    /** A File object representing the config file, guaranteed to exist after checkConfigFile() is run */
    private File configFile;

    /**
     * True if there were errors when reading the config file.
     */
    public boolean errorFlag = false;

    /**
     * Constructor for a config manager that tries to find a default config file in the JAR resources section.
     * This constructor calls checkConfigFileExists() and readConfigFile().
     *
     * @param name: Name of the application, used in logging statements
     * @param configFilePath: Path to the config file
     * @param configFileName: Name of the config file (with extension, e.g. "app_config.properties") (this will be used both to name the newly created one, and to find the embedded default one)
     * @param configurableClass: {java.lang.Class} object that holds the configurable fields (use NameOfClass.class or classInstance.getClass())
     * @param configurableClassInstance: Instance of the previous configurable object, if instance fields are used. Pass NULL here if static fields are used
     */
    public ConfigManager(
        String name, Path configFilePath, String configFileName,
        Class<?> configurableClass,
        Object configurableClassInstance
    ) throws IOException
    {
        this.name = name;
        this.configFileDirectory = configFilePath;
        this.configFileName = configFileName;
        this.configurableClass = configurableClass;
        this.configurableClassInstance = configurableClassInstance;

        this.checkConfigFileExists();
        this.readConfigFile();
    }

    /**
     * Check for the existence of a config file, and copy the one on the classpath over if needed
     */
    public void checkConfigFileExists() throws IOException
    {
        this.configFile = this.configFileDirectory.resolve(this.configFileName).toFile();

        if (!this.configFile.exists())
        {
            try (
                InputStream defaultConfigFileInputStream = this.getClass().getResourceAsStream("/" + this.configFileName);
                FileOutputStream fos = new FileOutputStream(this.configFile)
            )
            {
                this.configFile.createNewFile();

                KeybindsGalore.LOGGER.warn("(KBG Config Manager) {} config file not found, copying default config file", this.name);
                defaultConfigFileInputStream.transferTo(fos);
            }
            catch (IOException ioe)
            {
                KeybindsGalore.LOGGER.error("(KBG Config Manager) IOException while copying default config file!");
                ioe.printStackTrace();
                throw ioe; // Re-throw for user app to handle exception
            }
        }

        KeybindsGalore.LOGGER.info("(KBG Config Manager) Config file exists!");
    }

    /**
     * Read configs from the config file. Sets hasCustomData if invalid config statements were read.
     * <br>
     * NOTE: entries in the config file MUST match field names EXACTLY (case-insensitive)
     */
    public void readConfigFile() throws IOException
    {
        // Reset error flag
        this.errorFlag = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(this.configFile)))
        {
            // Iterate over each line in the file
            for (String line : reader.lines().toArray(String[]::new))
            {
                // Skip comments and blank lines
                if (line.trim().startsWith("#") || line.isBlank()) continue;

                // Split it by the equals sign (.properties format)
                String[] entry = line.split("=");

                try
                {
                    // Trim lines so you can have spaces around the equals ("prop = val" as opposed to "prop=val")
                    entry[0] = entry[0].trim();
                    entry[1] = entry[1].trim();

                    // Set fields in configurable class
                    Field f = this.configurableClass.getDeclaredField(entry[0].toUpperCase(Locale.getDefault()));
                    Class<?> fieldTypeClass = f.getType();

                    if (fieldTypeClass.isAssignableFrom(short.class))
                    {
                        // Short value (0x??)
                        f.setShort(this.configurableClassInstance, Short.parseShort(
                                entry[1].replace("0x", ""),
                                16)
                        );
                    }
                    else if (fieldTypeClass.isAssignableFrom(int.class))
                    {
                        // Integer value
                        if (entry[1].startsWith("0x"))
                        {
                            // Hex literal
                            Integer.parseInt(
                                    entry[1].replace("0x", ""),
                                    16
                            );
                        }
                        else f.setInt(
                            this.configurableClassInstance,
                            Integer.parseInt(entry[1])
                        );
                    }
                    else if (fieldTypeClass.isAssignableFrom(float.class))
                    {
                        f.setFloat(
                            this.configurableClassInstance,
                            Float.parseFloat(entry[1])
                        );
                    }
                    else if (fieldTypeClass.isAssignableFrom(boolean.class))
                    {
                        f.setBoolean(
                            this.configurableClassInstance,
                            Boolean.parseBoolean(entry[1])
                        );
                    }
                    else if (fieldTypeClass.isAssignableFrom(ArrayList.class))
                    {
                        // Remove opening square brackets and commas
                        ArrayList<Integer> list = new ArrayList<>();

                        for (String e : entry[1].replaceAll("[\\[\\]\\s]+", "").split(","))
                            list.add(Integer.parseInt(e));

                        f.set(this.configurableClassInstance, list);
                    }
                    else
                    {
                        KeybindsGalore.LOGGER.error("(KBG Config Manager) Unrecognised data type for config entry {}", line);
                    }
                }
                catch (NoSuchFieldException nsfe)
                {
                    KeybindsGalore.LOGGER.error("(KBG Config Manager) No matching field found for config entry: {}", entry[0]);
                    this.errorFlag = true;
                }
                catch (IllegalAccessException illegal)
                {
                    KeybindsGalore.LOGGER.error("(KBG Config Manager) Could not set field involved in: {}", line);
                    this.errorFlag = true;
                    illegal.printStackTrace();
                }
                catch (Exception e)
                {
                    KeybindsGalore.LOGGER.error("(KBG Config Manager) Malformed config entry: {}", line);
                    this.errorFlag = true;
                }
            }
        }
        catch (IOException ioe)
        {
            KeybindsGalore.LOGGER.error("(KBG Config Manager) IOException while reading config file: {}", ioe.getMessage());
            throw ioe;
        }

        KeybindsGalore.LOGGER.info("(KBG Config Manager) Finished reading config file!");
    }

    public void printAllConfigs()
    {
        KeybindsGalore.LOGGER.info("(KBG Config Manager) Dumping configs:");
        for (var f : this.configurableClass.getDeclaredFields())
        {
            try { KeybindsGalore.LOGGER.info("\t{}: {}", f.getName(), f.get(this.configurableClassInstance)); }
            catch (IllegalAccessException | NullPointerException ignored) {}
        }
    }
}