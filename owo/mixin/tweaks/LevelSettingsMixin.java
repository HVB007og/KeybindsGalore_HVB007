package io.wispforest.owo.mixin.tweaks;

import io.wispforest.owo.Owo;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1267;
import net.minecraft.class_1928;
import net.minecraft.class_1934;
import net.minecraft.class_1940;
import net.minecraft.class_7712;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_1940.class)
public class LevelSettingsMixin {

    @Shadow
    @Final
    private class_1928 gameRules;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void simulationIsForNerds(String name, class_1934 gameMode, boolean hardcore, class_1267 difficulty, boolean allowCommands, class_1928 gameRules, class_7712 dataConfiguration, CallbackInfo ci) {
        if (!(Owo.DEBUG && FabricLoader.getInstance().isDevelopmentEnvironment())) return;

        this.gameRules.method_76186(class_1928.field_19396, false, null);
        this.gameRules.method_76186(class_1928.field_19406, false, null);
    }

}
