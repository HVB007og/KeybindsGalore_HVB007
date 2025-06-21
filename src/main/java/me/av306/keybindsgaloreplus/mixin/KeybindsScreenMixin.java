package me.av306.keybindsgaloreplus.mixin;

import me.av306.keybindsgaloreplus.KeybindManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin( KeybindsScreen.class )
public abstract class KeybindsScreenMixin extends GameOptionsScreen
{
    public KeybindsScreenMixin( Screen parent, GameOptions gameOptions, Text title )
    {
        super( parent, gameOptions, title );
    }

    @Override
    public void close()
    {
        super.close();

        // Check for conflicting keybinds on screen close
        KeybindManager.findAllConflicts();
    }
}
