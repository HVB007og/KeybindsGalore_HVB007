package net.hvb007.keybindsgalore;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;

import net.hvb007.keybindsgalore.configmanager.ConfigManager;
import net.hvb007.keybindsgalore.customdata.DataManager;
import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;

public class KeybindsGalore implements ClientModInitializer {
    public static ConfigManager configManager;
    public static DataManager customDataManager;
    public static final Logger LOGGER = LoggerFactory.getLogger("keybindsgalore");
    private static KeyMapping configReloadKeybind;

    // The keybinding we want to force-press after a menu selection.
    public static KeyMapping activePulseTarget = null;
    // Ticks remaining to hold the activePulseTarget as pressed.
    public static int pulseTimer = 0;

    @Override
    public void onInitializeClient() {
        LOGGER.info("KeybindsGalore initialising...");

        try {
            configManager = new ConfigManager("KeybindsGalore", FabricLoader.getInstance().getConfigDir(), "keybindsgalore.properties", Configurations.class, null);
            if (Configurations.DEBUG) {
                configManager.printAllConfigs();
            }

            customDataManager = new DataManager(FabricLoader.getInstance().getConfigDir(), "keybindsgalore_customdata.data");

            // This block uses reflection to register a keybind in a way that is compatible with multiple
            // Minecraft versions. The constructor for KeyMapping has changed over time, and this code
            // dynamically figures out which constructor to use at runtime.
            try {
                // Get the first public constructor of the KeyMapping class. This is a bit of a gamble,
                // but it's the most likely one to be the primary constructor.
                Constructor<?> constructor = KeyMapping.class.getConstructors()[0];

                // The category argument for the constructor has also changed.
                // In older versions, it was a String (e.g., "key.categories.misc").
                // In newer versions, it's an enum (e.g., KeyMapping.MISC).
                Object categoryArg = "key.categories.misc";
                Class<?>[] paramTypes = constructor.getParameterTypes();
                if (paramTypes.length > 0 && !paramTypes[paramTypes.length - 1].equals(String.class)) {
                    // If the last parameter is not a String, we assume it's the enum type.
                    // We try to get the MISC enum field first, and if that fails, we try GAMEPLAY.
                    try { categoryArg = KeyMapping.class.getField("MISC").get(null); }
                    catch (NoSuchFieldException e) { categoryArg = KeyMapping.class.getField("GAMEPLAY").get(null); }
                }

                // The number and types of parameters in the constructor have also changed.
                // Newer versions have an InputConstants.Type parameter as the second argument.
                if (paramTypes.length > 1 && paramTypes[1].equals(InputConstants.Type.class)) {
                    // This is the constructor for newer Minecraft versions.
                    configReloadKeybind = (KeyMapping) constructor.newInstance("key.keybindsgalore.reloadconfigs", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, categoryArg);
                } else {
                    // This is the constructor for older Minecraft versions.
                    configReloadKeybind = (KeyMapping) constructor.newInstance("key.keybindsgalore.reloadconfigs", GLFW.GLFW_KEY_UNKNOWN, categoryArg);
                }
                KeyBindingHelper.registerKeyBinding(configReloadKeybind);
            } catch (Exception e) {
                LOGGER.error("Failed to register config reload keybind using reflection!", e);
            }

            // Register a client tick event to manage the pulse timer.
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                // Decrement the pulse timer each tick.
                if (pulseTimer > 0) {
                    pulseTimer--;
                    // When the timer expires, release the key and clear the target.
                    if (pulseTimer == 0 && activePulseTarget != null) {
                        ((KeyMappingAccessor) activePulseTarget).setIsDown(false);
                        activePulseTarget = null;
                    }
                }

                // Handle the config reload keybind press.
                if (configReloadKeybind != null && configReloadKeybind.consumeClick()) {
                    try {
                        configManager.readConfigFile();
                        customDataManager.readDataFile();
                    } catch (IOException ex) {
                        if (client.player != null) client.player.displayClientMessage(Component.translatable("text.keybindsgalore.configreloadfail", ex.getMessage()), false);
                        return;
                    }

                    if (client.player != null) {
                        if (configManager.errorFlag) client.player.displayClientMessage(Component.translatable("text.keybindsgalore.configerrors").withStyle(ChatFormatting.RED), false);
                        if (customDataManager.hasCustomData) client.player.displayClientMessage(Component.translatable("text.keybindsgalore.customdatafound"), false);
                        client.player.displayClientMessage(Component.translatable("text.keybindsgalore.configreloaded"), false);
                    }
                }
            });
        } catch (IOException ioe) {
            LOGGER.error("Failed to read config file on init!", ioe);
        }

        // Find all conflicting keybinds when the player joins a world.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> KeybindManager.findAllConflicts());
    }

    /**
     * Logs a message if debug mode is enabled in the config.
     */
    public static void debugLog(String message, Object... args) {
        if (Configurations.DEBUG) {
            LOGGER.info("(KBG DEBUG) " + message, args);
        }
    }
}
