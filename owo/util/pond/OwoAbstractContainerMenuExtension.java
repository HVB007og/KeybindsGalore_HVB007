package io.wispforest.owo.util.pond;

import io.wispforest.owo.client.screens.MenuNetworkingInternals;
import net.minecraft.class_1657;

public interface OwoAbstractContainerMenuExtension {
    void owo$attachToPlayer(class_1657 player);

    void owo$readPropertySync(MenuNetworkingInternals.SyncPropertiesPacket packet);

    void owo$handlePacket(MenuNetworkingInternals.LocalPacket packet, boolean clientbound);
}
