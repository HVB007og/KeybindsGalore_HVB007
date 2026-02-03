package io.wispforest.owo.client.screens;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.Owo;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import io.wispforest.owo.util.pond.OwoAbstractContainerMenuExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_2540;
import net.minecraft.class_2960;
import net.minecraft.class_3936;
import net.minecraft.class_8710;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class MenuNetworkingInternals {
    public static final class_2960 SYNC_PROPERTIES = Owo.id("sync_menu_properties");

    public static void init() {
        var localPacketCodec = CodecUtils.toPacketCodec(LocalPacket.ENDEC);

        PayloadTypeRegistry.playS2C().register(LocalPacket.ID, localPacketCodec);
        PayloadTypeRegistry.playC2S().register(LocalPacket.ID, localPacketCodec);
        PayloadTypeRegistry.playS2C().register(SyncPropertiesPacket.ID, CodecUtils.toPacketCodec(SyncPropertiesPacket.ENDEC));

        ServerPlayNetworking.registerGlobalReceiver(LocalPacket.ID, (payload, context) -> {
            var menu = context.player().field_7512;

            if (menu == null) {
                Owo.LOGGER.error("Received local packet for null ContainerMenu");
                return;
            }

            ((OwoAbstractContainerMenuExtension) menu).owo$handlePacket(payload, false);
        });
    }

    public record LocalPacket(int packetId, class_2540 payload) implements class_8710 {
        public static final class_9154<LocalPacket> ID = new class_9154<>(Owo.id("local_packet"));
        public static final Endec<LocalPacket> ENDEC = StructEndecBuilder.of(
            Endec.VAR_INT.fieldOf("packetId", LocalPacket::packetId),
            MinecraftEndecs.FRIENDLY_BYTE_BUF.fieldOf("payload", LocalPacket::payload),
            LocalPacket::new
        );

        @Override
        public class_9154<? extends class_8710> method_56479() {
            return ID;
        }
    }

    public record SyncPropertiesPacket(class_2540 payload) implements class_8710 {
        public static final class_9154<SyncPropertiesPacket> ID = new class_9154<>(SYNC_PROPERTIES);
        public static final Endec<SyncPropertiesPacket> ENDEC = StructEndecBuilder.of(
            MinecraftEndecs.FRIENDLY_BYTE_BUF.fieldOf("payload", SyncPropertiesPacket::payload),
            SyncPropertiesPacket::new
        );

        @Override
        public class_9154<? extends class_8710> method_56479() {
            return ID;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Client {
        public static void init() {
            ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                if (screen instanceof class_3936<?> handled)
                    ((OwoAbstractContainerMenuExtension) handled.method_17577()).owo$attachToPlayer(client.field_1724);
            });

            ClientPlayNetworking.registerGlobalReceiver(LocalPacket.ID, (payload, context) -> {
                var menu = context.player().field_7512;

                if (menu == null) {
                    Owo.LOGGER.error("Received local packet for null ContainerMenu");
                    return;
                }

                ((OwoAbstractContainerMenuExtension) menu).owo$handlePacket(payload, true);
            });

            ClientPlayNetworking.registerGlobalReceiver(SyncPropertiesPacket.ID, (payload, context) -> {
                var menu = context.player().field_7512;

                if (menu == null) {
                    Owo.LOGGER.error("Received sync properties packet for null ContainerMenu");
                    return;
                }

                ((OwoAbstractContainerMenuExtension) menu).owo$readPropertySync(payload);
            });
        }
    }
}
