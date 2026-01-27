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
    @Shadow private int timesPressed;

    @Shadow public abstract String getId();

    @Inject(at = @At("HEAD"), method = "setKeyPressed", cancellable = true)
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci)
    {
        // This delegates to KeybindManager, which now handles the "Nuclear Pulse" logic on release.
        KeybindManager.handleKeyPress(key, pressed, ci);
    }

    @Inject(at = @At("HEAD"), method = "onKeyPressed")
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) { }

    // --- VERIFICATION LOGGER ---
    // This logs whenever a keybinding's state actually changes.
    // Use this to prove that "Hotbar 9" never turns ON during the selection.
    @Inject(at = @At("HEAD"), method = "setPressed")
    public void setPressed(boolean pressed, CallbackInfo ci)
    {
        // Only log if the state is actually changing to avoid spam
        if (this.pressed != pressed) {
            String id = this.getId();
            // Filter for hotbar keys or relevant keys to keep logs readable
            if (id.contains("hotbar") || id.contains("keybindsgalore")) {
                KeybindsGalore.LOGGER.info("STATE CHANGE: {} -> {}", id, pressed ? "PRESSED" : "RELEASED");
            }
        }
    }
}