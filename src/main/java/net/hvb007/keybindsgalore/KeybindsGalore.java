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

public class KeybindsGalore implements ClientModInitializer
{
    public static ConfigManager configManager;
    public static DataManager customDataManager;
    public static final Logger LOGGER = LoggerFactory.getLogger("keybindsgalore");
    private static KeyBinding configreloadKeybind;

    // --- NUCLEAR PULSE STATE ---
    // The keybind we want to FORCE ON
    public static KeyBinding activePulseTarget = null;
    // How long (in ticks) to hold the state
    public static int pulseTimer = 0;

    @Override
    public void onInitializeClient()
    {
        LOGGER.info("KeybindsGalore initialising...");

        try {
            configManager = new ConfigManager("KeybindsGalore", FabricLoader.getInstance().getConfigDir(), "keybindsgalore.properties", Configurations.class, null);
            if (Configurations.DEBUG) this.configManager.printAllConfigs();

            customDataManager = new DataManager(FabricLoader.getInstance().getConfigDir(), "keybindsgalore_customdata.data");

            // Reflection for Reload Keybind
            try {
                Constructor<?> constructor = KeyBinding.class.getConstructors()[0];
                Object categoryArg = "key.categories.misc";
                Class<?>[] paramTypes = constructor.getParameterTypes();
                if (paramTypes.length > 0 && !paramTypes[paramTypes.length - 1].equals(String.class)) {
                    try { categoryArg = KeyBinding.class.getField("MISC").get(null); }
                    catch (NoSuchFieldException e) { categoryArg = KeyBinding.class.getField("GAMEPLAY").get(null); }
                }

                if (paramTypes.length > 1 && paramTypes[1].equals(InputUtil.Type.class)) {
                    configreloadKeybind = (KeyBinding) constructor.newInstance("key.keybindsgalore.reloadconfigs", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, categoryArg);
                } else {
                    configreloadKeybind = (KeyBinding) constructor.newInstance("key.keybindsgalore.reloadconfigs", GLFW.GLFW_KEY_UNKNOWN, categoryArg);
                }
                KeyBindingHelper.registerKeyBinding(configreloadKeybind);
            } catch (Exception e) { LOGGER.error("Failed to register keybind!", e); }

            // --- TICK LOGIC ---
            ClientTickEvents.END_CLIENT_TICK.register(client ->
            {
                // Manage the Nuclear Pulse Timer
                if (pulseTimer > 0) {
                    pulseTimer--;
                    // When timer expires, release the lock on the target
                    if (pulseTimer == 0 && activePulseTarget != null) {
                        ((KeyBindingAccessor) activePulseTarget).setPressed(false);
                        activePulseTarget = null;
                    }
                }

                if (configreloadKeybind != null && configreloadKeybind.wasPressed()) {
                    try {
                        configManager.readConfigFile();
                        customDataManager.readDataFile();
                    } catch (IOException firstIoe) {
                        if (client.player != null) client.player.sendMessage(Text.translatable("text.keybindsgalore.configreloadfail", firstIoe.getMessage()), false);
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
            LOGGER.error("(KBG) IOException while reading config file on init!");
            ioe.printStackTrace();
        }

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> KeybindManager.findAllConflicts());
    }

    public static void debugLog(String message) {
        if (Configurations.DEBUG) LOGGER.info("(KBG DEBUG) " + message);
    }

    public static void debugLog(String message, Object... objects) {
        if (Configurations.DEBUG) LOGGER.info("(KBG DEBUG) " + message, objects);
    }
}