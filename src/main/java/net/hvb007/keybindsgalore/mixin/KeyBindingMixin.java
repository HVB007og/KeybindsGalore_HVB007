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

/**
 * Mixin to intercept vanilla keybinding logic.
 */
@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Shadow
    private InputUtil.Key boundKey;

    /**
     * Intercepts the moment a key's state is set from a physical input event.
     * This is the primary entry point for all our conflict detection logic.
     */
    @Inject(at = @At("HEAD"), method = "setKeyPressed", cancellable = true)
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        KeybindManager.handleKeyPress(key, pressed, ci);
    }

    /**
     * Intercepts the "click" event for a key.
     * We cancel this for conflicting keys to prevent vanilla actions from firing.
     */
    @Inject(at = @At("HEAD"), method = "onKeyPressed", cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        KeybindManager.handleOnKeyPressed(key, ci);
    }

    /**
     * Intercepts any attempt to change the `pressed` state of a keybinding.
     * This acts as a "gatekeeper" to block vanilla's polling-based updates
     * from overriding our logic for conflicting keys.
     */
    @Inject(at = @At("HEAD"), method = "setPressed", cancellable = true)
    public void setPressed(boolean pressed, CallbackInfo ci) {
        KeyBinding self = (KeyBinding) (Object) this;

        // Block any attempt to press a conflicting key unless it's our chosen target
        // or the special-cased 'toggleGui' key.
        if (pressed && KeybindManager.hasConflicts(this.boundKey)) {
            if (self != KeybindsGalore.activePulseTarget && !self.getId().equals("key.toggleGui")) {
                ci.cancel();
            }
        }
    }
}
