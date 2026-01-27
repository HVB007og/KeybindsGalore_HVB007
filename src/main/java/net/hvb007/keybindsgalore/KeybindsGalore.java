package net.hvb007.keybindsgalore;

// FIXED IMPORTS: Pointing to the new net.hvb007 structure
import net.hvb007.keybindsgalore.configmanager.ConfigManager;
import net.hvb007.keybindsgalore.customdata.DataManager;
// Note: Accessor is unused in this file, but keeping import valid if you need it later
import net.hvb007.keybindsgalore.mixin.KeyBindingAccessor;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.io.IOException;

import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class KeybindsGalore implements ClientModInitializer
{
    public static ConfigManager configManager;
    public static DataManager customDataManager;

    // Fixed Logger ID
    public static final Logger LOGGER = LoggerFactory.getLogger("keybindsgalore");

    private static KeyBinding configreloadKeybind;

    @Override
    public void onInitializeClient()
    {
        LOGGER.info("KeybindsGalore initialising...");

        try
        {
            // Initialise ConfigManager and load config file
            // FIXED: Filename matches the one we renamed in the resources folder
            configManager = new ConfigManager(
                "KeybindsGalore",
                FabricLoader.getInstance().getConfigDir(),
                "keybindsgalore.properties",
                Configurations.class,
                null
            );

            LOGGER.info("Debug mode: {}", Configurations.DEBUG);

            if (Configurations.DEBUG)
            {
                this.configManager.printAllConfigs();
            }

            // Initialise custom data manager and read data file
            // FIXED: Filename updated to remove 'plus'
            customDataManager = new DataManager(
                    FabricLoader.getInstance().getConfigDir(),
                    "keybindsgalore_customdata.data"
            );


            // Set config reload key
            // FIXED: Translation keys updated to 'keybindsgalore'
            configreloadKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.keybindsgalore.reloadconfigs",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "category.keybindsgalore.keybinds"
            ));

            // Bind action to config reload key
            ClientTickEvents.END_CLIENT_TICK.register(client ->
            {
                while (configreloadKeybind.wasPressed())
                {
                    try
                    {
                        configManager.readConfigFile();
                        customDataManager.readDataFile();
                    }
                    catch (IOException firstIoe)
                    {
                        client.player.sendMessage(Text.translatable("text.keybindsgalore.configreloadfail", firstIoe.getMessage()), false);

                        return;
                    }

                    if (configManager.errorFlag) client.player.sendMessage(Text.translatable("text.keybindsgalore.configerrors").formatted(Formatting.RED), false);
                    if (customDataManager.hasCustomData) client.player.sendMessage(Text.translatable("text.keybindsgalore.customdatafound"), false);

                    client.player.sendMessage(Text.translatable("text.keybindsgalore.configreloaded"), false);

                    if (Configurations.DEBUG)
                    {
                        this.configManager.printAllConfigs();
                    }
                }
            });


        }
        catch (IOException ioe)
        {
            LOGGER.error("(KBG) IOException while reading config file on init!");
            ioe.printStackTrace();
        }

        // Find conflicts on first world join
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> KeybindManager.findAllConflicts());
    }

    public static void debugLog(String message)
    {
        if (Configurations.DEBUG) LOGGER.info("(KBG DEBUG) " + message);
    }

    public static void debugLog(String message, Object... objects)
    {
        if (Configurations.DEBUG) LOGGER.info("(KBG DEBUG) " + message, objects);
    }

    public static Text createHyperlinkText(String url)
    {
        return null;
    }
}