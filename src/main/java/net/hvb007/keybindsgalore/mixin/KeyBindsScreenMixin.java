package net.hvb007.keybindsgalore.mixin;

import net.hvb007.keybindsgalore.KeybindManager;
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
    public KeyBindsScreenMixin(Screen parent, Options gameOptions, Component title) {
        super(parent, gameOptions, title);
    }

    /**
     * Re-scans for conflicting keybinds every time the user closes the controls menu.
     * This ensures our conflict list is always up-to-date.
     */
    @Override
    public void onClose() {
        super.onClose();
        KeybindManager.findAllConflicts();
    }
}
