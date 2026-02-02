package net.hvb007.keybindsgalore.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor
{
    // Allows us to manually reset the left-click delay (cooldown)
    // Useful if the radial menu triggers an attack action and we want it to happen instantly
    @Accessor void setMissTime(int attackCooldown);
}