package net.hvb007.keybindsgalore.mixin;

import static net.hvb007.keybindsgalore.Configurations.DEBUG;

import net.hvb007.keybindsgalore.KeybindsGalore;
import net.hvb007.keybindsgalore.KeybindManager;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyBinding.class)
public abstract class KeyBindingMixin
{
    @Shadow
    private InputUtil.Key boundKey;

    @Shadow @Final
    private String translationKey;

    @Shadow private boolean pressed;


    // Intercepts the raw input signal from the keyboard before it reaches any specific binding
    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci)
    {
        KeybindsGalore.debugLog("setKeyPressed({}, {}) called", key.getTranslationKey(), pressed);

        // This is the gatekeeper. If the key is conflicted, we cancel the event here
        // so vanilla Minecraft never knows the key was pressed.
        KeybindManager.handleKeyPress(key, pressed, ci);
    }

    // Normally this handles incrementing times pressed; only called when key first goes down
    // "times pressed" is used for sub-tick input accumulation
    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci)
    {
        KeybindsGalore.debugLog("onKeyPressed({}) called", key.getTranslationKey());

        if (KeybindManager.hasConflicts(key))
        {
            KeybindsGalore.debugLog("\tCancelling sub-tick accumulation");
            ci.cancel(); // Cancel, because we've manually handled the press in KeybindManager
        }
    }

    // Checks internal state updates.
    // NOTE: This injection is less critical for functionality but good for debugging state desyncs.
    @Inject(method = "setPressed", at = @At("HEAD"), cancellable = true)
    private void setPressed(boolean pressed, CallbackInfo ci)
    {
        KeybindsGalore.debugLog("setPressed({}) called for keybind {} on physical key {}", pressed, this.translationKey, this.boundKey.getTranslationKey());
    }
}