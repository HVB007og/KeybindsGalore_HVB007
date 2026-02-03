package net.hvb007.keybindsgalore.mixin;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Provides direct access to private fields and methods of the KeyBinding class.
 */
@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {
    /**
     * Accessor for the 'timesPressed' field.
     */
    @Accessor
    void setClickCount(int timesPressed);

    /**
     * Accessor for the 'pressed' field.
     */
    @Accessor("isDown")
    void setIsDown(boolean pressed);

    /**
     * Accessor for the 'boundKey' field.
     */
    @Accessor
    InputConstants.Key getKey();

    /**
     * Invoker for the 'reset' method.
     */
    @Invoker("release")
    void invokeRelease();
}
