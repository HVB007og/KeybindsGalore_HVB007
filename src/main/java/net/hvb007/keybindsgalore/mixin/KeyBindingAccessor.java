package net.hvb007.keybindsgalore.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor
{
    @Accessor void setTimesPressed(int timesPressed);
    @Accessor("pressed") void setPressed(boolean pressed);

    @Accessor InputUtil.Key getBoundKey();

    // ❌ REMOVED BROKEN STRING ACCESSORS to prevent startup crash
    // We will handle these via Reflection in KeybindManager instead.

    @Invoker("reset") void invokeReset();
}