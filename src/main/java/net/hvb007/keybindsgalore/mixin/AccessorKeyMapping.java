//IDK what this is
package net.hvb007.keybindsgalore.mixin;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = KeyMapping.class)
public interface AccessorKeyMapping {
    @Accessor void setClickCount(int timesPressed);
    @Accessor void setIsDown(boolean pressed);
}
