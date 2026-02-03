package net.hvb007.keybindsgalore.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Provides direct access to private fields and methods of the KeyBinding class.
 */
@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
    /**
     * Accessor for the 'timesPressed' field.
     */
    @Accessor
    void setTimesPressed(int timesPressed);

    /**
     * Accessor for the 'pressed' field.
     */
    @Accessor("pressed")
    void setPressed(boolean pressed);

    /**
     * Accessor for the 'boundKey' field.
     */
    @Accessor
    InputUtil.Key getBoundKey();

    /**
     * Invoker for the 'reset' method.
     */
    @Invoker("reset")
    void invokeReset();
}
