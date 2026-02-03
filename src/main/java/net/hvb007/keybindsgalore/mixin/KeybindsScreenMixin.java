package net.hvb007.keybindsgalore.mixin;

import net.hvb007.keybindsgalore.KeybindManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin for the vanilla Keybinds screen.
 */
@Mixin(KeybindsScreen.class)
public abstract class KeybindsScreenMixin extends GameOptionsScreen {
    public KeybindsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    /**
     * Re-scans for conflicting keybinds every time the user closes the controls menu.
     * This ensures our conflict list is always up-to-date.
     */
    @Override
    public void close() {
        super.close();
        KeybindManager.findAllConflicts();
    }
}
