package io.wispforest.owo.mixin;

import io.wispforest.owo.Owo;
import io.wispforest.owo.util.TagInjector;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.class_2960;
import net.minecraft.class_3300;
import net.minecraft.class_3503;

@Mixin(class_3503.class)
public class TagLoaderMixin {

    @Shadow
    @Final
    private String directory;

    @Inject(method = "load", at = @At("TAIL"))
    public void injectValues(class_3300 manager, CallbackInfoReturnable<Map<class_2960, List<class_3503.class_5145>>> cir) {
        var map = cir.getReturnValue();

        TagInjector.ADDITIONS.forEach((location, entries) -> {
            if (!this.directory.equals(location.type())) return;

            var list = map.computeIfAbsent(location.tagId(), id -> new ArrayList<>());
            entries.forEach(addition -> list.add(new class_3503.class_5145(addition, Owo.MOD_ID)));
        });
    }

}
