package io.wispforest.owo.mixin.extension.json5;

import io.wispforest.owo.Owo;
import io.wispforest.owo.util.DataExtensionUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.class_3262;
import net.minecraft.class_3264;
import net.minecraft.class_6861;

@Mixin(class_6861.class)
public abstract class MultiPackResourceManagerMixin {

    @Inject(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/packs/resources/MultiPackResourceManager;getPackFilterSection(Lnet/minecraft/server/packs/PackResources;)Lnet/minecraft/server/packs/resources/ResourceFilterSection;"
        )
    )
    private static void json5$optInPacks(class_3264 type, List<class_3262> packs, CallbackInfo ci) {
        for (var pack : packs) {
            var inputSupplier = pack.method_14410(Owo.MOD_ID + "-json5");
            if (inputSupplier != null) {
                DataExtensionUtil.JSON5_ENABLED_PACKS.add(pack.method_14409());
            } else {
                DataExtensionUtil.JSON5_ENABLED_PACKS.remove(pack.method_14409());
            }
        }
    }
}
