package io.wispforest.owo;

import io.wispforest.owo.client.screens.MenuNetworkingInternals;
import io.wispforest.owo.command.debug.OwoDebugCommands;
import io.wispforest.owo.ops.LootOps;
import io.wispforest.owo.text.CustomTextRegistry;
import io.wispforest.owo.text.InsertingTextContent;
import io.wispforest.owo.util.Wisdom;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.wispforest.owo.ops.TextOps.withColor;

public class Owo implements ModInitializer {

    public static final String MOD_ID = "owo";
    /**
     * Whether oωo debug is enabled, this defaults to {@code true} in a development environment.
     * To override that behavior, add the {@code -Dowo.debug=false} java argument
     */
    public static final boolean DEBUG;
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static MinecraftServer SERVER;

    public static final class_2561 PREFIX = class_2561.method_43473().method_27692(class_124.field_1080)
        .method_10852(withColor("o", 0x3955e5))
        .method_10852(withColor("ω", 0x13a6f0))
        .method_10852(withColor("o", 0x3955e5))
        .method_10852(class_2561.method_43470(" > ").method_27692(class_124.field_1080));

    static {
        boolean debug = FabricLoader.getInstance().isDevelopmentEnvironment();
        if (System.getProperty("owo.debug") != null) debug = Boolean.getBoolean("owo.debug");
        if (Boolean.getBoolean("owo.forceDisableDebug")) {
            LOGGER.warn("Deprecated system property 'owo.forceDisableDebug=true' was used - use 'owo.debug=false' instead");
            debug = false;
        }

        DEBUG = debug;
    }

    @Override
    @ApiStatus.Internal
    public void onInitialize() {
        LootOps.registerListener();
        CustomTextRegistry.register("index", InsertingTextContent.CODEC);
        MenuNetworkingInternals.init();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> SERVER = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> SERVER = null);

        Wisdom.spread();

        if (!DEBUG) return;

        OwoDebugCommands.register();
    }

    @ApiStatus.Internal
    public static void debugWarn(Logger logger, String message) {
        if (!DEBUG) return;
        logger.warn(message);
    }

    @ApiStatus.Internal
    public static void debugWarn(Logger logger, String message, Object... params) {
        if (!DEBUG) return;
        logger.warn(message, params);
    }

    /**
     * @return The currently active minecraft server instance. If running
     * on a physical client, this will return the integrated server while in
     * a local singleplayer world and {@code null} otherwise
     */
    public static MinecraftServer currentServer() {
        return SERVER;
    }

    // "eh it's only like 10-15 of them what's the big deal" - glisco, while writing the 52nd hardcoded Identifier.of("owo", ...)
    @ApiStatus.Internal
    public static class_2960 id(String path) {
        return class_2960.method_60655(MOD_ID, path);
    }
}
