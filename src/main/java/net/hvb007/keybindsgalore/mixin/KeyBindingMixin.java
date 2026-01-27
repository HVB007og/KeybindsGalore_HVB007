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
import java.util.List;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin
{
    @Shadow private InputUtil.Key boundKey;
    @Shadow private boolean pressed;
    @Shadow private int timesPressed;

    // Intercept the PRESS event to open the menu
    @Inject(at = @At("HEAD"), method = "setKeyPressed", cancellable = true)
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci)
    {
        KeybindManager.handleKeyPress(key, pressed, ci);
    }

    // Enforce Nuclear Pulse Logic AFTER vanilla has processed the key update
    @Inject(at = @At("RETURN"), method = "setKeyPressed")
    private static void setKeyPressedTail(InputUtil.Key key, boolean pressed, CallbackInfo ci)
    {
        // Only run if Pulse Mode is active
        if (KeybindsGalore.pulseTimer > 0 && KeybindsGalore.activePulseTarget != null) {
            
            // Check if the key being updated is the one we are protecting
            InputUtil.Key targetKey = ((KeyBindingAccessor)KeybindsGalore.activePulseTarget).getBoundKey();
            
            if (key.equals(targetKey)) {
                KeybindsGalore.LOGGER.info("Mixin setKeyPressed (TAIL): Enforcing Nuclear State for {}", key);

                KeyBinding target = KeybindsGalore.activePulseTarget;
                
                // 1. Force the Target KeyBinding to be PRESSED
                ((KeyBindingAccessor)target).setPressed(true);
                
                // 2. Force all other conflicting bindings to be RELEASED
                List<KeyBinding> conflicts = KeybindManager.getConflicts(key);
                if (conflicts != null) {
                    for (KeyBinding kb : conflicts) {
                        if (kb != target) {
                            ((KeyBindingAccessor)kb).setPressed(false);
                            ((KeyBindingAccessor)kb).setTimesPressed(0);
                        }
                    }
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "onKeyPressed")
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) { }

    // Kept as a fallback/utility, but the main logic is now in setKeyPressedTail
    @Inject(at = @At("HEAD"), method = "setPressed", cancellable = true)
    public void setPressed(boolean pressed, CallbackInfo ci)
    {
        if (KeybindsGalore.pulseTimer > 0 && KeybindsGalore.activePulseTarget != null) {
            KeyBinding self = (KeyBinding)(Object)this;
            if (self == KeybindsGalore.activePulseTarget) {
                this.pressed = true;
                ci.cancel();
            } else {
                InputUtil.Key targetKey = ((KeyBindingAccessor)KeybindsGalore.activePulseTarget).getBoundKey();
                if (this.boundKey.equals(targetKey)) {
                    this.pressed = false;
                    this.timesPressed = 0;
                    ci.cancel();
                }
            }
        }
    }
}