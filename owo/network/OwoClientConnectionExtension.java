package io.wispforest.owo.network;

import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import net.minecraft.class_2960;

@ApiStatus.Internal
public interface OwoClientConnectionExtension {
    void owo$setChannelSet(Set<class_2960> channels);

    Set<class_2960> owo$getChannelSet();
}
