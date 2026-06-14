package net.hvb007.keybindsgalore;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import net.hvb007.keybindsgalore.configmanager.ConfigManager;
import net.hvb007.keybindsgalore.configmanager.ConfigScreenFactory;
import net.hvb007.keybindsgalore.customdata.DataManager;
import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;

@Mod("keybindsgalore")
public class KeybindsGalore {
    public static ConfigManager configManager;
    public static DataManager customDataManager;
    public static final Logger LOGGER = LoggerFactory.getLogger("keybindsgalore");

    public static KeyMapping activePulseTarget = null;
    public static int pulseTimer = 0;

    public static KeyMapping openCaptureKey;

    public KeybindsGalore(IEventBus modEventBus, ModContainer container) {
        LOGGER.info("KeybindsGalore initialising...");
        container.registerExtensionPoint(IConfigScreenFactory.class, new ConfigScreenFactory());

        openCaptureKey = new KeyMapping(
                "key.keybindsgalore.open_capture",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.keybindsgaloreplus.keybinds"
        );

        modEventBus.addListener((RegisterKeyMappingsEvent event) -> {
            event.register(openCaptureKey);
        });

        try {
            configManager = new ConfigManager("KeybindsGalore", FMLPaths.CONFIGDIR.get(), "keybindsgalore.properties", Configurations.class, null);
            if (Configurations.DEBUG) {
                configManager.printAllConfigs();
            }

            customDataManager = new DataManager(FMLPaths.CONFIGDIR.get(), "keybindsgalore_customdata.data");

            NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> {
                while (openCaptureKey.consumeClick()) {
                    Minecraft.getInstance().setScreen(new net.hvb007.keybindsgalore.configmanager.KeyCaptureScreen(null, (capturedKey, conflicts) -> {
                        Minecraft.getInstance().setScreen(new net.hvb007.keybindsgalore.configmanager.ActionSelectionScreen(
                            null,
                            conflicts,
                            selected -> {
                                KeybindManager.prioritizeAction(selected, capturedKey);
                            },
                            () -> {
                                KeybindManager.removePriority(capturedKey);
                            }
                        ));
                    }));
                }

                if (pulseTimer > 0) {
                    pulseTimer--;
                    if (pulseTimer == 0 && activePulseTarget != null) {
                        ((KeyMappingAccessor) activePulseTarget).setIsDown(false);
                        activePulseTarget = null;
                    }
                }
            });
        } catch (IOException ioe) {
            LOGGER.error("Failed to read config file on init!", ioe);
        }

        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingIn event) -> {
            KeybindManager.findAllConflicts();
        });
    }

    public static void debugLog(String message, Object... args) {
        if (Configurations.DEBUG) {
            LOGGER.info("(KBG DEBUG) " + message, args);
        }
    }
}
