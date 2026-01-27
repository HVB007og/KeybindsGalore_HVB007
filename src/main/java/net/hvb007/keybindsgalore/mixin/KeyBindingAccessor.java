package net.hvb007.keybindsgalore.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor
{
    // Accessing 'timesPressed' allows us to register a "click" without holding the key down
    @Accessor void setTimesPressed(int timesPressed);

    // Accessing the field 'pressed' directly is CRITICAL.
    // It allows us to set the key state without triggering the 'setKeyPressed' mixin method above.
    // This prevents the Stack Overflow crash.
    @Accessor("pressed") void setPressed(boolean pressed);

    @Accessor InputUtil.Key getBoundKey();

    @Invoker("reset") void invokeReset();
}