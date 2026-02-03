package io.wispforest.owo.mixin.extension.json5;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import io.wispforest.owo.util.DataExtensionUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.class_2960;
import net.minecraft.class_3262;
import net.minecraft.class_3264;
import net.minecraft.class_3294;
import net.minecraft.class_3298;

import static io.wispforest.owo.util.DataExtensionUtil.coerceJson;

@Mixin(class_3294.class)
public abstract class FallbackResourceManagerMixin {

    @WrapMethod(method = "getResourceStack")
    private List<class_3298> json5$getAllResources(class_2960 id, Operation<List<class_3298>> original) {
        var base = original.call(id);
        if (id.method_12832().endsWith(".json")) original
            .call(id.method_45136(id.method_12832() + 5))
            .forEach(resource -> {
                if (DataExtensionUtil.JSON5_ENABLED_PACKS.contains(resource.method_45304().method_14409())) {
                    base.add(new class_3298(resource.method_45304(), () -> coerceJson(resource.method_14482())));
                }
            });
        return base;
    }

    @WrapWithCondition(
        method = "listResources",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/packs/PackResources;listResources(Lnet/minecraft/server/packs/PackType;Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/server/packs/PackResources$ResourceOutput;)V"
        )
    )
    private boolean json5$findResources(
        class_3262 instance,
        class_3264 resourceType,
        String namespace,
        String startingPath,
        class_3262.class_7664 resultConsumer,
        @Local(argsOnly = true) Predicate<class_2960> predicate
    ) {
        return !(predicate instanceof DataExtensionUtil.OptInIdentifierPredicate)
               || DataExtensionUtil.JSON5_ENABLED_PACKS.contains(instance.method_14409());
    }

    @WrapWithCondition(
        method = "listResourceStacks",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/packs/resources/FallbackResourceManager;listPackResources(Lnet/minecraft/server/packs/resources/FallbackResourceManager$PackEntry;Ljava/lang/String;Ljava/util/function/Predicate;Ljava/util/Map;)V"
        )
    )
    private boolean json5$findAllResources(
        class_3294 instance,
        class_3294.class_7082 pack,
        String startingPath,
        Predicate<class_2960> allowedPathPredicate,
        Map<?, ?> idToEntryList,
        @Local(argsOnly = true) Predicate<class_2960> predicate
    ) {
        return !(predicate instanceof DataExtensionUtil.OptInIdentifierPredicate)
               || pack.comp_530 != null && DataExtensionUtil.JSON5_ENABLED_PACKS.contains(pack.comp_530.method_14409());
    }
}
