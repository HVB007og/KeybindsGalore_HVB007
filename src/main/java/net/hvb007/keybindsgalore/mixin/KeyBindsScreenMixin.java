package net.hvb007.keybindsgalore.mixin;

import net.hvb007.keybindsgalore.KeybindManager;
import net.hvb007.keybindsgalore.input.minecraft.MinecraftConflictIndex;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin for the vanilla Keybinds screen.
 */
@Mixin(KeyBindsScreen.class)
public abstract class KeyBindsScreenMixin extends OptionsSubScreen {
    public KeyBindsScreenMixin(Screen parent, Options gameOptions) {
        super(parent, gameOptions, Component.translatable("options.controls"));
    }

    /**
     * Re-scans for conflicting keybinds every time the user closes the controls menu.
     * This ensures our conflict list is always up-to-date.
     */
    @Override
    public void onClose() {
        super.onClose();
        KeybindManager.refreshConflicts(MinecraftConflictIndex.RefreshReason.KEYBINDS_SCREEN_CLOSED);
    }
}
