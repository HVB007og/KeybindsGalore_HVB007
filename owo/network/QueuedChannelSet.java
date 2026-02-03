package io.wispforest.owo.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public class QueuedChannelSet {
    public static Set<class_2960> channels;
}
