package io.wispforest.owo.config;

import com.google.common.collect.HashMultimap;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.Owo;
import io.wispforest.owo.mixin.ServerCommonPacketListenerImplAccessor;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_124;
import net.minecraft.class_2535;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3222;
import net.minecraft.class_3545;
import net.minecraft.class_5250;
import net.minecraft.class_8710;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public class ConfigSynchronizer {

    public static final class_2960 CONFIG_SYNC_CHANNEL = Owo.id("config_sync");

    private static final Map<class_2535, Map<String, Map<Option.Key, Object>>> CLIENT_OPTION_STORAGE = new WeakHashMap<>();

    private static final Map<String, ConfigWrapper<?>> KNOWN_CONFIGS = new HashMap<>();
    private static final class_5250 PREFIX = TextOps.concat(Owo.PREFIX, class_2561.method_30163("§cunrecoverable config mismatch\n\n"));

    static void register(ConfigWrapper<?> config) {
        KNOWN_CONFIGS.put(config.name(), config);
    }

    /**
     * Retrieve the options which the given player's client
     * sent to the server during config synchronization
     *
     * @param player     The player for which to retrieve the client values
     * @param configName The name of the config for which to retrieve values
     * @return The player's client's values of the given config options,
     * or {@code null} if no config with the given name was synced
     */
    public static @Nullable Map<Option.Key, ?> getClientOptions(class_3222 player, String configName) {
        var storage = CLIENT_OPTION_STORAGE.get(((ServerCommonPacketListenerImplAccessor) player.field_13987).owo$getConnection());
        if (storage == null) return null;

        return storage.get(configName);
    }

    /**
     * Safer, more clear version of {@link #getClientOptions(class_3222, String)} to
     * be used when the actual config wrapper is available
     *
     * @see #getClientOptions(class_3222, String)
     */
    public static @Nullable Map<Option.Key, ?> getClientOptions(class_3222 player, ConfigWrapper<?> config) {
        return getClientOptions(player, config.name());
    }

    private static ConfigSyncPacket toPacket(Option.SyncMode targetMode) {
        Map<String, ConfigEntry> configs = new HashMap<>();

        KNOWN_CONFIGS.forEach((configName, config) -> {
            var entry = new ConfigEntry(new HashMap<>());

            config.allOptions().forEach((key, option) -> {
                if (option.syncMode().ordinal() < targetMode.ordinal()) return;

                class_2540 optionBuf = PacketByteBufs.create();
                option.write(optionBuf);

                entry.options().put(key.asString(), optionBuf);
            });

            configs.put(configName, entry);
        });

        return new ConfigSyncPacket(configs);
    }

    private static void read(ConfigSyncPacket packet, BiConsumer<Option<?>, class_2540> optionConsumer) {
        for (var configEntry : packet.configs().entrySet()) {
            var configName = configEntry.getKey();
            var config = KNOWN_CONFIGS.get(configName);
            if (config == null) {
                Owo.LOGGER.error("Received overrides for unknown config '{}', skipping", configName);
                continue;
            }

            for (var optionEntry : configEntry.getValue().options().entrySet()) {
                var optionKey = new Option.Key(optionEntry.getKey());
                var option = config.optionForKey(optionKey);
                if (option == null) {
                    Owo.LOGGER.error("Received override for unknown option '{}' in config '{}', skipping", optionKey, configName);
                    continue;
                }

                optionConsumer.accept(option, optionEntry.getValue());
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private static void applyClient(ConfigSyncPacket payload, ClientPlayNetworking.Context context) {
        Owo.LOGGER.info("Applying server overrides");
        var mismatchedOptions = new HashMap<Option<?>, Object>();

        if (!(context.client().method_1496() && context.client().method_1576().method_3724())) {
            read(payload, (option, packetByteBuf) -> {
                var mismatchedValue = option.read(packetByteBuf);
                if (mismatchedValue != null) mismatchedOptions.put(option, mismatchedValue);
            });

            if (!mismatchedOptions.isEmpty()) {
                Owo.LOGGER.error("Aborting connection, non-syncable config values were mismatched");
                mismatchedOptions.forEach((option, serverValue) -> {
                    Owo.LOGGER.error("- Option {} in config '{}' has value '{}' but server requires '{}'",
                            option.key().asString(), option.configName(), option.value(), serverValue);
                });

                var errorMessage = class_2561.method_43473();
                var optionsByConfig = HashMultimap.<String, class_3545<Option<?>, Object>>create();

                mismatchedOptions.forEach((option, serverValue) -> optionsByConfig.put(option.configName(), new class_3545<>(option, serverValue)));
                for (var configName : optionsByConfig.keys()) {
                    errorMessage.method_10852(TextOps.withFormatting("in config ", class_124.field_1080)).method_27693(configName).method_27693("\n");
                    for (var option : optionsByConfig.get(configName)) {
                        errorMessage.method_10852(class_2561.method_43471(option.method_15442().translationKey()).method_27692(class_124.field_1054)).method_27693(" -> ");
                        errorMessage.method_27693(option.method_15442().value().toString()).method_10852(TextOps.withFormatting(" (client)", class_124.field_1080));
                        errorMessage.method_10852(TextOps.withFormatting(" / ", class_124.field_1063));
                        errorMessage.method_27693(option.method_15441().toString()).method_10852(TextOps.withFormatting(" (server)", class_124.field_1080)).method_27693("\n");
                    }
                    errorMessage.method_27693("\n");
                }

                errorMessage.method_10852(TextOps.withFormatting("these options could not be synchronized because\n", class_124.field_1080));
                errorMessage.method_10852(TextOps.withFormatting("they require your client to be restarted\n", class_124.field_1080));
                errorMessage.method_10852(TextOps.withFormatting("change them manually and restart if you want to join this server", class_124.field_1080));

                context.player().field_3944.method_48296().method_10747(TextOps.concat(PREFIX, errorMessage));
                return;
            }
        }

        Owo.LOGGER.info("Responding with client values");
        context.responseSender().sendPacket(toPacket(Option.SyncMode.INFORM_SERVER));
    }

    private static void applyServer(ConfigSyncPacket payload, ServerPlayNetworking.Context context) {
        Owo.LOGGER.info("Receiving client config");
        var connection = ((ServerCommonPacketListenerImplAccessor) context.player().field_13987).owo$getConnection();

        read(payload, (option, optionBuf) -> {
            var config = CLIENT_OPTION_STORAGE.computeIfAbsent(connection, $ -> new HashMap<>()).computeIfAbsent(option.configName(), s -> new HashMap<>());
            config.put(option.key(), optionBuf.read(option.endec()));
        });
    }

    private record ConfigSyncPacket(Map<String, ConfigEntry> configs) implements class_8710 {
        public static final class_9154<ConfigSyncPacket> ID = new class_9154<>(CONFIG_SYNC_CHANNEL);
        public static final Endec<ConfigSyncPacket> ENDEC = StructEndecBuilder.of(
                ConfigEntry.ENDEC.mapOf().fieldOf("configs", ConfigSyncPacket::configs),
                ConfigSyncPacket::new
        );

        @Override
        public class_9154<? extends class_8710> method_56479() {
            return ID;
        }
    }

    private record ConfigEntry(Map<String, class_2540> options) {
        public static final Endec<ConfigEntry> ENDEC = StructEndecBuilder.of(
                MinecraftEndecs.FRIENDLY_BYTE_BUF.mapOf().fieldOf("options", ConfigEntry::options),
                ConfigEntry::new
        );
    }

    static {
        var packetCodec = CodecUtils.toPacketCodec(ConfigSyncPacket.ENDEC);

        PayloadTypeRegistry.playS2C().register(ConfigSyncPacket.ID, packetCodec);
        PayloadTypeRegistry.playC2S().register(ConfigSyncPacket.ID, packetCodec);

        var earlyPhase = Owo.id("early");
        ServerPlayConnectionEvents.JOIN.addPhaseOrdering(earlyPhase, Event.DEFAULT_PHASE);
        ServerPlayConnectionEvents.JOIN.register(earlyPhase, (handler, sender, server) -> {
            Owo.LOGGER.info("Sending server config values to client");

            sender.sendPacket(toPacket(Option.SyncMode.OVERRIDE_CLIENT));
        });

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.ID, ConfigSynchronizer::applyClient);

            ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
                KNOWN_CONFIGS.forEach((name, config) -> config.forEachOption(Option::reattach));
            });
        }

        ServerPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.ID, ConfigSynchronizer::applyServer);
    }
}
