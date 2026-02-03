package io.wispforest.owo.mixin;

import io.wispforest.owo.network.OwoClientConnectionExtension;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collections;
import java.util.Set;
import net.minecraft.class_2535;
import net.minecraft.class_2960;

@Mixin(class_2535.class)
public class ConnectionMixin implements OwoClientConnectionExtension {
    private Set<class_2960> channels = Collections.emptySet();

    @Override
    public void owo$setChannelSet(Set<class_2960> channels) {
        this.channels = channels;
    }

    @Override
    public Set<class_2960> owo$getChannelSet() {
        return this.channels;
    }
}
