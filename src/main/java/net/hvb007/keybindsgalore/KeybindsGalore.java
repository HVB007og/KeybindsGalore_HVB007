package net.hvb007.keybindsgalore;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;

import net.hvb007.keybindsgalore.configmanager.ConfigManager;
import net.hvb007.keybindsgalore.customdata.DataManager;
import net.hvb007.keybindsgalore.mixin.KeyBindingAccessor;

public class KeybindsGalore implements ClientModInitializer {
    public static ConfigManager configManager;
    public static DataManager customDataManager;
    public static final Logger LOGGER = LoggerFactory.getLogger("keybindsgalore");
    private static KeyBinding configReloadKeybind;

    // The keybinding we want to force-press after a menu selection.
    public static KeyBinding activePulseTarget = null;
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

            // Register a keybind to reload the config file in-game.
            // Uses reflection to support multiple Minecraft versions.
            try {
                Constructor<?> constructor = KeyBinding.class.getConstructors()[0];
                Object categoryArg = "key.categories.misc";
                Class<?>[] paramTypes = constructor.getParameterTypes();
                if (paramTypes.length > 0 && !paramTypes[paramTypes.length - 1].equals(String.class)) {
                    try { categoryArg = KeyBinding.class.getField("MISC").get(null); }
                    catch (NoSuchFieldException e) { categoryArg = KeyBinding.class.getField("GAMEPLAY").get(null); }
                }

                if (paramTypes.length > 1 && paramTypes[1].equals(InputUtil.Type.class)) {
                    configReloadKeybind = (KeyBinding) constructor.newInstance("key.keybindsgalore.reloadconfigs", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, categoryArg);
                } else {
                    configReloadKeybind = (KeyBinding) constructor.newInstance("key.keybindsgalore.reloadconfigs", GLFW.GLFW_KEY_UNKNOWN, categoryArg);
                }
                KeyBindingHelper.registerKeyBinding(configReloadKeybind);
            } catch (Exception e) {
                LOGGER.error("Failed to register config reload keybind!", e);
            }

            // Register a client tick event to manage the pulse timer.
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                // Decrement the pulse timer each tick.
                if (pulseTimer > 0) {
                    pulseTimer--;
                    // When the timer expires, release the key and clear the target.
                    if (pulseTimer == 0 && activePulseTarget != null) {
                        ((KeyBindingAccessor) activePulseTarget).setPressed(false);
                        activePulseTarget = null;
                    }
                }

                // Handle the config reload keybind press.
                if (configReloadKeybind != null && configReloadKeybind.wasPressed()) {
                    try {
                        configManager.readConfigFile();
                        customDataManager.readDataFile();
                    } catch (IOException ex) {
                        if (client.player != null) client.player.sendMessage(Text.translatable("text.keybindsgalore.configreloadfail", ex.getMessage()), false);
                        return;
                    }

                    if (client.player != null) {
                        if (configManager.errorFlag) client.player.sendMessage(Text.translatable("text.keybindsgalore.configerrors").formatted(Formatting.RED), false);
                        if (customDataManager.hasCustomData) client.player.sendMessage(Text.translatable("text.keybindsgalore.customdatafound"), false);
                        client.player.sendMessage(Text.translatable("text.keybindsgalore.configreloaded"), false);
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
