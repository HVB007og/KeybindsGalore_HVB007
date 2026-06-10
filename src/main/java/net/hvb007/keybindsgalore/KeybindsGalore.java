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
import java.lang.reflect.Field;

import net.hvb007.keybindsgalore.configmanager.ConfigManager;
import net.hvb007.keybindsgalore.customdata.DataManager;
import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;

public class KeybindsGalore implements ClientModInitializer {
    public static ConfigManager configManager;
    public static DataManager customDataManager;
    public static final Logger LOGGER = LoggerFactory.getLogger("keybindsgalore");

    // The keybinding we want to force-press after a menu selection.
    public static KeyMapping activePulseTarget = null;
    // Ticks remaining to hold the activePulseTarget as pressed.
    public static int pulseTimer = 0;

    public static KeyMapping openCaptureKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("KeybindsGalore initialising...");

        openCaptureKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.keybindsgalore.open_capture",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.keybindsgaloreplus.keybinds"
        ));

        try {
            configManager = new ConfigManager("KeybindsGalore", FabricLoader.getInstance().getConfigDir(), "keybindsgalore.properties", Configurations.class, null);
            if (Configurations.DEBUG) {
                configManager.printAllConfigs();
            }

            customDataManager = new DataManager(FabricLoader.getInstance().getConfigDir(), "keybindsgalore_customdata.data");

            // Register a client tick event to manage the pulse timer.
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                while (openCaptureKey.consumeClick()) {
                    client.setScreen(new net.hvb007.keybindsgalore.configmanager.KeyCaptureScreen(null, (capturedKey, conflicts) -> {
                        client.setScreen(new net.hvb007.keybindsgalore.configmanager.ActionSelectionScreen(null, conflicts, selected -> {
                            KeybindManager.prioritizeAction(selected, capturedKey);
                        }));
                    }));
                }

                // Decrement the pulse timer each tick.
                if (pulseTimer > 0) {
                    pulseTimer--;
                    // When the timer expires, release the key and clear the target.
                    if (pulseTimer == 0 && activePulseTarget != null) {
                        ((KeyMappingAccessor) activePulseTarget).setIsDown(false);
                        activePulseTarget = null;
                    }
                }
            });
        } catch (IOException ioe) {
            LOGGER.error("Failed to read config file on init!", ioe);
        }

        // Find all conflicting keybinds when the player joins a world.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            KeybindManager.findAllConflicts();
        });
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
