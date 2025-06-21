package me.av306.keybindsgaloreplus.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.av306.keybindsgaloreplus.KeybindsGalorePlus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( MinecraftClient.class )
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable> implements WindowEventHandler
{
    @Shadow public @Final GameOptions options;
    @Shadow public int attackCooldown;

    @Shadow public abstract void openGameMenu( boolean pauseOnly );

    public MinecraftClientMixin( String string )
    {
        super( string );
    }

//    @Inject(
//            method = "handleInputEvents",
//            at = @At( "HEAD" )
//    )
//    private void onHandleInputEvents( CallbackInfo ci )
//    {
//        //KeybindsGalorePlus.debugLog( "attackKey pressed: {}, using item: {}", this.options.attackKey.isPressed(), this.player.isUsingItem() );
//
//        // Only keep attack pressed if needed AND attack
//        // This does work. But not for attacking.
//        ((KeyBindingAccessor) this.options.attackKey).setPressed( true );
//        this.attackCooldown = 0;
//        ((KeyBindingAccessor) this.options.attackKey).setTimesPressed( 1 );
//    }

//    @Inject(
//            method = "handleInputEvents",
//            at = @At( value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V", shift = At.Shift.BEFORE )
//
//    )
//    private void onHandleInputEventsTail( CallbackInfo ci, @Local( ordinal = 2, print = true ) boolean bl3 )
//    {
//        KeybindsGalorePlus.debugLog( "{}", bl3 );
//    }

//    @Inject(
//            method = "handleBlockBreaking",
//            at = @At( "HEAD" )
//    )
//    private void onHandleBlockBreaking( CallbackInfo ci, @Local( argsOnly = true ) boolean breaking )
//    {
//        KeybindsGalorePlus.debugLog( "attack cooldown: {} breaking: {}", this.attackCooldown, breaking );
//    }

    // Some notes (TODO: add to Yarn mappings?)
    // doAttack() returns true if and only if attacking the block broke it (the block turned into air)
    // bl3 is approximately "block breaking finished"
    // handleBlockBreaking() handles block breaking in progress, and is only called if block breaking has NOT finished (bl3 is false)
    // The problem here was that attack cooldown is set to 10000 every tick that a screen is shown, and is only reset when handleBlockBreaking( false ) is called
    // I'm guessing that there isn't enough time for a handleBlockBreaking( false ) call when the pie menu screen is closed and the attack key is set to pressed
    // So the workaround is to set attackCooldown = 0 in the pie menu screen!
}
