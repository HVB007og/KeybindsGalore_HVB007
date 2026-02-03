package net.hvb007.keybindsgalore.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin into the main MinecraftClient class.
 * Currently a placeholder to satisfy the mixins.json config, as the
 * original input handling logic has been moved to other, more specific mixins.
 */
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable> implements WindowEventHandler {
    public MinecraftClientMixin(String string) {
        super(string);
    }
}
