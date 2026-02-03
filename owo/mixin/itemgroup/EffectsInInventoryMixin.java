package io.wispforest.owo.mixin.itemgroup;

import io.wispforest.owo.itemgroup.OwoItemGroup;
import net.minecraft.class_481;
import net.minecraft.class_485;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(class_485.class)
public class EffectsInInventoryMixin {

    @ModifyVariable(method = "renderEffects",
        at = @At("HEAD"),
        ordinal = 0, argsOnly = true)
    private int shiftStatusEffects(int x) {
        if (!((Object) this instanceof class_481)) return x;
        if (!(CreativeModeInventoryScreenAccessor.owo$getSelectedTab() instanceof OwoItemGroup group)) return x;
        if (group.getButtons().isEmpty()) return x;

        return x + 28;
    }

}
