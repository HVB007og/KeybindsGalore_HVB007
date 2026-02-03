package io.wispforest.owo.mixin.extension.json5;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.owo.util.DataExtensionUtil.OptInIdentifierPredicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.class_2960;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import net.minecraft.class_7654;

import static io.wispforest.owo.util.DataExtensionUtil.OptInIdentifierPredicate;
import static io.wispforest.owo.util.DataExtensionUtil.coerceJson;

@Mixin(class_7654.class)
public abstract class FileToIdConverterMixin {

    @Shadow @Final private String extension;

    @WrapOperation(
        method = "listMatchingResources",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/packs/resources/ResourceManager;listResources(Ljava/lang/String;Ljava/util/function/Predicate;)Ljava/util/Map;"
        )
    )
    private Map<class_2960, class_3298> json5$findResources(
        class_3300 instance,
        String directoryName,
        Predicate<class_2960> identifierPredicate,
        Operation<Map<class_2960, class_3298>> original
    ) {
        var base = original.call(instance, directoryName, identifierPredicate);
        if (this.extension.equals(".json")) {
            original
                .call(instance, directoryName, OptInIdentifierPredicate.of(path -> path.method_12832().endsWith(".json5")))
                .forEach((identifier, resource) -> base.put(
                    identifier,
                    new class_3298(resource.method_45304(), () -> coerceJson(resource.method_14482()))
                ));
        }
        return base;
    }

    @WrapOperation(
        method = "listMatchingResourceStacks",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/packs/resources/ResourceManager;listResourceStacks(Ljava/lang/String;Ljava/util/function/Predicate;)Ljava/util/Map;"
        )
    )
    private Map<class_2960, List<class_3298>> json5$findAllResources(
        class_3300 instance,
        String directoryName,
        Predicate<class_2960> identifierPredicate,
        Operation<Map<class_2960, List<class_3298>>> original
    ) {
        var base = original.call(instance, directoryName, identifierPredicate);
        if (this.extension.equals(".json")) {
            original
                .call(instance, directoryName, OptInIdentifierPredicate.of(path -> path.method_12832().endsWith(".json5")))
                .forEach((identifier, resources) -> base
                    .computeIfAbsent(identifier, id -> new ArrayList<>())
                    .addAll(resources
                        .stream()
                        .map(resource -> new class_3298(resource.method_45304(), () -> coerceJson(resource.method_14482())))
                        .toList()
                    )
                );
        }
        return base;
    }

    @WrapMethod(method = "fileToId")
    private class_2960 json5$fixToResourceId(
        class_2960 path, Operation<class_2960> original
    ) {
        if (this.extension.equals(".json") && path.method_12832().endsWith(".json5"))
            path = path.method_45136(path.method_12832().substring(0, path.method_12832().length() - 1));
        return original.call(path);
    }
}
