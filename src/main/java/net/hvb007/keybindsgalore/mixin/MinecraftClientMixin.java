package net.hvb007.keybindsgalore.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable> implements WindowEventHandler
{
    @Shadow public @Final GameOptions options;
    @Shadow public int attackCooldown;

    @Shadow public abstract void openGameMenu(boolean pauseOnly);

    public MinecraftClientMixin(String string)
    {
        super(string);
    }

    // NOTE: Previous input handling logic was removed because it is now handled
    // by KeybindSelectorScreen.java and KeyBindingMixin.java.
    // This file is kept as a placeholder to satisfy the mixins.json config.
}