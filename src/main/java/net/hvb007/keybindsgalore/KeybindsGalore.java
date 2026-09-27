package net.hvb007.keybindsgalore;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import com.mojang.blaze3d.platform.InputConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import net.hvb007.keybindsgalore.api.BindingRegistry;
import net.hvb007.keybindsgalore.api.KeybindApi;
import net.hvb007.keybindsgalore.configmanager.ConfigManager;
import net.hvb007.keybindsgalore.core.InputOwnershipStateMachine;
import net.hvb007.keybindsgalore.customdata.DataManager;
import net.hvb007.keybindsgalore.integrations.minecraft.MinecraftBindingSource;
import net.hvb007.keybindsgalore.input.minecraft.MinecraftConflictIndex;
import net.hvb007.keybindsgalore.input.minecraft.PulseController;
import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;

public class KeybindsGalore implements ClientModInitializer {
    public static ConfigManager configManager;
    public static DataManager customDataManager;
    public static final Logger LOGGER = LoggerFactory.getLogger("keybindsgalore");

    /**
     * SDL scancode for the K key, used as the default binding for the capture hotkey.
     *
     * <p>26.3 replaced GLFW with SDL3, so {@code InputConstants.Type.KEYBOARD} stores
     * SDL scancodes rather than GLFW keycodes, and letter keys have no named constant on
     * {@code InputConstants}. SDL_SCANCODE_A is 4, so K is 14. This is the one value in
     * the 26.3 port that could not be confirmed from vanilla source, so it is the first
     * thing to check by hand: if the capture hotkey does not respond to K, rebind it in
     * the vanilla Controls screen.
     */
    private static final int SDL_SCANCODE_K = 14;
    private static final BindingRegistry BINDING_REGISTRY = new BindingRegistry();
    private static final InputOwnershipStateMachine INPUT_STATE = new InputOwnershipStateMachine();
    private static boolean minecraftSourceRegistered;
    private static boolean conflictsInitialized;

    public static KeybindApi getApi() {
        return BINDING_REGISTRY;
    }

    public static InputOwnershipStateMachine inputState() {
        return INPUT_STATE;
    }

    public static void saveConfigAndRefresh() {
        if (configManager == null) {
            return;
        }
        configManager.saveConfigFile();
        KeybindManager.refreshConflicts(MinecraftConflictIndex.RefreshReason.CONFIG_SAVED);
    }

    // The keybinding we want to force-press after a menu selection.
    public static KeyMapping activePulseTarget = null;
    // Ticks remaining to hold the activePulseTarget as pressed.
    public static int pulseTimer = 0;
    public static KeyMapping activePriorityTarget = null;
    private static final Set<KeyMapping> ACTIVE_PRIORITY_TARGETS =
            Collections.newSetFromMap(new IdentityHashMap<>());
    private static final PulseController PULSE_CONTROLLER = new PulseController();

    public static KeyMapping openCaptureKey;
    private static InputConstants.Key captureCooldownKey;
    private static int captureCooldownTicks;

    public static void startPulse(KeyMapping target) {
        INPUT_STATE.selectionMade();
        PULSE_CONTROLLER.start(target, Configurations.PULSE_TIMER_DURATION);
    }

    public static void tickPulse() {
        PULSE_CONTROLLER.tick();
    }

    public static void clearPulseIf(KeyMapping target) {
        PULSE_CONTROLLER.clearIf(target);
    }

    public static boolean isPriorityTarget(KeyMapping target) {
        return ACTIVE_PRIORITY_TARGETS.contains(target);
    }

    public static void addPriorityTarget(KeyMapping target) {
        if (target != null) {
            ACTIVE_PRIORITY_TARGETS.add(target);
            activePriorityTarget = target;
            INPUT_STATE.priorityActivated();
        }
    }

    public static void removePriorityTarget(KeyMapping target) {
        if (target != null) {
            ACTIVE_PRIORITY_TARGETS.remove(target);
        }
        if (ACTIVE_PRIORITY_TARGETS.isEmpty()) {
            INPUT_STATE.released();
        }
        if (activePriorityTarget == target) {
            activePriorityTarget = ACTIVE_PRIORITY_TARGETS.isEmpty()
                    ? null
                    : ACTIVE_PRIORITY_TARGETS.iterator().next();
        }
    }

    public static void resetInputOwnership() {
        PULSE_CONTROLLER.reset();
        for (KeyMapping target : ACTIVE_PRIORITY_TARGETS) {
            ((KeyMappingAccessor) target).setIsDown(false);
        }
        ACTIVE_PRIORITY_TARGETS.clear();
        activePriorityTarget = null;
        INPUT_STATE.reset();
    }

    public static void beginCaptureCooldown(InputConstants.Key key) {
        captureCooldownKey = key;
        captureCooldownTicks = 5;
    }

    public static boolean shouldBlockCapturedKey(InputConstants.Key key) {
        return captureCooldownTicks > 0 && captureCooldownKey != null && captureCooldownKey.equals(key);
    }

    public static boolean isCaptureKey(InputConstants.Key key) {
        return openCaptureKey != null && ((KeyMappingAccessor) openCaptureKey).getKey().equals(key);
    }

    public static boolean tryOpenCaptureScreen(InputConstants.Key key) {
        if (!isCaptureKey(key) || captureCooldownTicks > 0) {
            return false;
        }
        if (Minecraft.getInstance().gui.screen() instanceof net.hvb007.keybindsgalore.configmanager.KeyCaptureScreen) {
            return true;
        }
        if (Minecraft.getInstance().gui.screen() != null) {
            return false;
        }
        openCaptureScreen();
        return true;
    }

    private static void openCaptureScreen() {
        Minecraft client = Minecraft.getInstance();
        beginCaptureCooldown(((KeyMappingAccessor) openCaptureKey).getKey());
        client.gui.setScreen(new net.hvb007.keybindsgalore.configmanager.KeyCaptureScreen(null, (capturedKey, conflicts) -> {
            beginCaptureCooldown(capturedKey);
            client.gui.setScreen(new net.hvb007.keybindsgalore.configmanager.ActionSelectionScreen(
                    null,
                    conflicts,
                    selected -> KeybindManager.prioritizeAction(selected, capturedKey),
                    () -> KeybindManager.removePriority(capturedKey)
            ));
        }));
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("KeybindsGalore initialising...");

        openCaptureKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.keybindsgalore.open_capture",
                InputConstants.Type.KEYBOARD,
                SDL_SCANCODE_K,
                KeyMapping.Category.MISC
        ));

        try {
            configManager = new ConfigManager("KeybindsGalore", FabricLoader.getInstance().getConfigDir(), "keybindsgalore.properties", Configurations.class, null);
            if (Configurations.DEBUG) {
                configManager.printAllConfigs();
            }

            customDataManager = new DataManager(FabricLoader.getInstance().getConfigDir(), "keybindsgalore_customdata.data");

            // Register a client tick event to manage the pulse timer.
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (!minecraftSourceRegistered && client.options != null) {
                    BINDING_REGISTRY.registerSource(new MinecraftBindingSource(client.options.keyMappings));
                    minecraftSourceRegistered = true;
                }
                if (!conflictsInitialized && client.options != null) {
                    KeybindManager.refreshConflicts(MinecraftConflictIndex.RefreshReason.STARTUP);
                    conflictsInitialized = true;
                }
                if (client.gui.screen() != null) {
                    resetInputOwnership();
                    while (openCaptureKey.consumeClick()) {
                    }
                } else {
                    while (openCaptureKey.consumeClick()) {
                        if (client.gui.screen() != null || captureCooldownTicks > 0 || openCaptureKey.isDown()) {
                            continue;
                        }
                        openCaptureScreen();
                    }
                }

                tickPulse();
                if (captureCooldownTicks > 0) {
                    captureCooldownTicks--;
                }
            });
        } catch (IOException ioe) {
            LOGGER.error("Failed to read config file on init!", ioe);
        }

        // Find all conflicting keybinds when the player joins a world.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            KeybindManager.refreshConflicts(MinecraftConflictIndex.RefreshReason.WORLD_JOIN);
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            resetInputOwnership();
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

    /**
     * Logs a high-volume message if both debug mode and verbose debug mode are enabled.
     * Used for per-frame and per-selection tracing such as selector hover changes.
     */
    public static void verboseLog(String message, Object... args) {
        if (Configurations.DEBUG && Configurations.VERBOSE_DEBUG) {
            LOGGER.info("(KBG VERBOSE) " + message, args);
        }
    }
}
