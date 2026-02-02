package net.hvb007.keybindsgalore.mixin;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.WindowEventHandler;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin into the main MinecraftClient class.
 * Currently a placeholder to satisfy the mixins.json config, as the
 * original input handling logic has been moved to other, more specific mixins.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler {
    public MinecraftMixin(String string) {
        super(string);
    }
}
