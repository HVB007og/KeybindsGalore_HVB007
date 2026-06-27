package net.hvb007.keybindsgalore.mixin;

import net.hvb007.keybindsgalore.KeybindManager;
import net.hvb007.keybindsgalore.KeybindsGalore;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to intercept vanilla keybinding logic.
 */
@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    @Shadow
    private InputConstants.Key key;

    /**
     * Intercepts the moment a key's state is set from a physical input event.
     * This is the primary entry point for all our conflict detection logic.
     */
    @Inject(at = @At("HEAD"), method = "set", cancellable = true)
    private static void setKeyPressed(InputConstants.Key key, boolean pressed, CallbackInfo ci) {
        KeybindManager.handleKeyPress(key, pressed, ci);
    }

    /**
     * Intercepts the "click" event for a key.
     * We cancel this for conflicting keys to prevent vanilla actions from firing.
     */
    @Inject(at = @At("HEAD"), method = "click", cancellable = true)
    private static void onKeyPressed(InputConstants.Key key, CallbackInfo ci) {
        KeybindManager.handleOnKeyPressed(key, ci);
    }

    /**
     * Intercepts any attempt to change the `pressed` state of a keybinding.
     * This acts as a "gatekeeper" to block vanilla's polling-based updates
     * from overriding our logic for conflicting keys.
     */
    @Inject(at = @At("HEAD"), method = "setDown", cancellable = true)
    public void setPressed(boolean pressed, CallbackInfo ci) {
        if (KeybindManager.isAmecsLoaded()) return;

        KeyMapping self = (KeyMapping) (Object) this;

        // Block any attempt to press a conflicting key unless it's our chosen target
        // or the special-cased 'toggleGui' key.
        if (pressed && KeybindManager.hasConflicts(this.key)) {
            KeyMapping priority = KeybindManager.getPriorityKey(this.key);
            
            // Allow if this IS the priority key (by name), or if it's the pulse target, or toggleGui
            if (priority != null && self.getName().equals(priority.getName())) {
                return; // Let the priority key pass
            }
            if (KeybindsGalore.activePulseTarget != null && self.getName().equals(KeybindsGalore.activePulseTarget.getName())) {
                return; // Let the pulse target pass
            }
            if (self.getName().equals("key.toggleGui")) {
                return;
            }
            
            ci.cancel(); // Block non-priority conflicting keys
        }
    }
}
