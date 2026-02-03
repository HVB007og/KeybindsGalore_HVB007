package io.wispforest.owo.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_746;

public class ClientAccess implements OwoNetChannel.EnvironmentAccess<class_746, class_310, class_634> {

    @Environment(EnvType.CLIENT) private final class_634 packetListener;
    @Environment(EnvType.CLIENT) private final class_310 instance = class_310.method_1551();

    public ClientAccess(class_634 packetListener) {
        this.packetListener = packetListener;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public class_746 player() {
        return instance.field_1724;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public class_310 runtime() {
        return instance;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public class_634 packetListener() {
        return packetListener;
    }
}
