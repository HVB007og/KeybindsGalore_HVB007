package net.hvb007.keybindsgalore.mixin;

import net.hvb007.keybindsgalore.KeybindManager;
import net.hvb007.keybindsgalore.KeybindsGalore;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin
{
    @Shadow private InputUtil.Key boundKey;
    @Shadow private boolean pressed;
    @Shadow public abstract String getId();

    @Inject(at = @At("HEAD"), method = "setKeyPressed", cancellable = true)
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci)
    {
        KeybindsGalore.debugLog("Mixin setKeyPressed: Key={} Pressed={}", key, pressed);
        KeybindManager.handleKeyPress(key, pressed, ci);
    }

    @Inject(at = @At("HEAD"), method = "onKeyPressed", cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) 
    {
        KeybindsGalore.debugLog("Mixin onKeyPressed: Key={}", key);
        KeybindManager.handleOnKeyPressed(key, ci);
    }

    @Inject(at = @At("HEAD"), method = "setPressed", cancellable = true)
    public void setPressed(boolean pressed, CallbackInfo ci)
    {
        String id = this.getId();

        // --- THE GATEKEEPER (with an exception) ---
        // Block conflicting keys, but specifically ALLOW key.toggleGui to pass through.
        if (pressed && KeybindManager.hasConflicts(this.boundKey)) {
            if (!id.equals("key.toggleGui")) { // The exception for the special key
                KeyBinding self = (KeyBinding)(Object)this;
                if (self != KeybindsGalore.activePulseTarget) {
                    KeybindsGalore.debugLog("GATEKEEPER: BLOCKED press for {} due to conflict.", id);
                    ci.cancel();
                    return;
                }
            }
        }

        // --- LOGGING ---
        if (this.pressed != pressed) {
            KeybindsGalore.debugLog("STATE CHANGE: {} -> {}", id, pressed ? "PRESSED" : "RELEASED");
        }
    }
}