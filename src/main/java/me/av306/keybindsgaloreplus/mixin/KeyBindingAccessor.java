package me.av306.keybindsgaloreplus.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin( KeyBinding.class )
public interface KeyBindingAccessor
{
    @Accessor void setTimesPressed( int timesPressed );
    @Accessor void setPressed( boolean pressed ); // Sets the internal pressed state -- not to be confused with Keybinding#setPressed

    @Accessor InputUtil.Key getBoundKey();

    @Invoker( "reset" ) void invokeReset();
}
