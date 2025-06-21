package me.av306.keybindsgaloreplus.mixin;

import static me.av306.keybindsgaloreplus.Configurations.DEBUG;

import me.av306.keybindsgaloreplus.KeybindsGalorePlus;
import static me.av306.keybindsgaloreplus.KeybindsGalorePlus.LOGGER;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.av306.keybindsgaloreplus.KeybindManager;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( value = KeyBinding.class )
public abstract class KeyBindingMixin
{
    @Shadow
    private InputUtil.Key boundKey;

    @Shadow @Final
    private String translationKey;

    @Shadow private boolean pressed;


    @Inject( method = "setKeyPressed", at = @At( "HEAD" ), cancellable = true )
    private static void setKeyPressed( InputUtil.Key key, boolean pressed, CallbackInfo ci ) throws Exception
    {
        KeybindsGalorePlus.debugLog( "setKeyPressed( {}, {} ) called", key.getTranslationKey(), pressed );

        // Handle key
        KeybindManager.handleKeyPress( key, pressed, ci );
    }

    // Normally this handles incrementing times pressed; only called when key first goes down
    // "times pressed" is used for sub-tick input accumulation
    @Inject( method = "onKeyPressed", at = @At( "HEAD" ), cancellable = true )
    private static void onKeyPressed( InputUtil.Key key, CallbackInfo ci )
    {
        KeybindsGalorePlus.debugLog( "onKeyPressed( {} ) called", key.getTranslationKey() );

        if ( KeybindManager.hasConflicts( key ) /*&& !KeybindManager.isSkippedKey( key )*/ )
        {
            KeybindsGalorePlus.debugLog( "\tCancelling sub-tick accumulation" );

            ci.cancel(); // Cancel, because we've sorted out sub-tick presses (by setting it to 1)
        }
    }

    // Theoretically, this should be called ALL THE TIME
    // which it *is*, but ONLY IN A NON-DEV ENVIRONMENT, somehow
    @Inject( method = "setPressed", at = @At("HEAD"), cancellable = true )
    private void setPressed( boolean pressed, CallbackInfo ci )
    {
        KeybindsGalorePlus.debugLog( "setPressed( {} ) called for keybind {} on physical key {}", pressed, this.translationKey, this.boundKey.getTranslationKey() );

        // I can't demonstrate that this actually causes issues (setPressed( true ) only happened for the mouse when I tried)
        // but it has potential for duplicating the handleKeyPress call, since setKeyPressed is *supposed* to call setPressed...
        // Not calling handleKeyPress may cause https://github.com/AV306/KeybindsGalore-Plus/issues/10 though
        //KeybindManager.handleKeyPress( this.boundKey, pressed, ci );
    }
}
