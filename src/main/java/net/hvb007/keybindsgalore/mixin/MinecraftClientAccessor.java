package net.hvb007.keybindsgalore.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor
{
    @Accessor("attackCooldown")
    void setAttackCooldown(int attackCooldown);
}
