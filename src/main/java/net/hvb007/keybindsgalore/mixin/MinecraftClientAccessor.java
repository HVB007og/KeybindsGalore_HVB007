package net.hvb007.keybindsgalore.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor
{
    // Allows us to manually reset the left-click delay (cooldown)
    // Useful if the radial menu triggers an attack action and we want it to happen instantly
    @Accessor void setAttackCooldown(int attackCooldown);
}