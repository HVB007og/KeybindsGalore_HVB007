package io.wispforest.owo.network;

import net.minecraft.class_3222;
import net.minecraft.class_3244;
import net.minecraft.server.MinecraftServer;

public record ServerAccess(class_3222 player) implements
        OwoNetChannel.EnvironmentAccess<class_3222, MinecraftServer, class_3244> {

    @Override
    public MinecraftServer runtime() {
        return player.method_51469().method_8503();
    }

    @Override
    public class_3244 packetListener() {
        return player.field_13987;
    }
}
