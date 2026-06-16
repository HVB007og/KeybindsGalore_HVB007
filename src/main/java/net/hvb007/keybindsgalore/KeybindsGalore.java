package net.hvb007.keybindsgalore;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

    public static RenderPipeline GUI_TRIANGLE_STRIP;
    public static RenderType GUI_SECTOR_LAYER;

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
                        client.setScreen(new net.hvb007.keybindsgalore.configmanager.ActionSelectionScreen(
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

        // 1.21.5+ rendering: Tesselator/BufferUploader removed — must use RenderPipeline + RenderType.
        // Register a custom TRIANGLE_STRIP pipeline derived from the GUI_SNIPPET base,
        // using POSITION_COLOR vertices (no UV/lightmap/overlay needed for flat UI sectors).
        // This is the same approach used by owo-lib 0.12.21+1.21.5.
        GUI_TRIANGLE_STRIP = RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
            .withLocation(ResourceLocation.fromNamespaceAndPath("keybindsgalore", "gui_triangle_strip"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
            .build();
        RenderPipelines.register(GUI_TRIANGLE_STRIP);
        // Wrap the pipeline in a RenderType so it works with BufferSource.getBuffer().
        // The empty CompositeState (no texture, default shard settings) is correct for
        // untextured colored quads. This is the Mojang-mapped equivalent of Yarn's
        // RenderLayer.of(name, bufSize, pipeline, params).
        GUI_SECTOR_LAYER = RenderType.create(
            "keybindsgalore:sector_triangle_strip",
            0xc0000,
            GUI_TRIANGLE_STRIP,
            RenderType.CompositeState.builder().createCompositeState(false)
        );

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
