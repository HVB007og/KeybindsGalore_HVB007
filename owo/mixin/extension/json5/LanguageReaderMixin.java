package io.wispforest.owo.mixin.extension.json5;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.owo.util.DataExtensionUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.server.translations.impl.language.LanguageReader;

import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.class_2960;
import net.minecraft.class_3298;
import net.minecraft.class_3300;

import static io.wispforest.owo.util.DataExtensionUtil.coerceJson;

@Mixin(LanguageReader.class)
public abstract class LanguageReaderMixin {

    @WrapOperation(
        method = "collectDataPackTranslations",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/packs/resources/ResourceManager;listResources(Ljava/lang/String;Ljava/util/function/Predicate;)Ljava/util/Map;"
        )
    )
    private static Map<class_2960, class_3298> json5$collectDataPackTranslations(
        class_3300 instance,
        String s,
        Predicate<class_2960> identifierPredicate,
        Operation<Map<class_2960, class_3298>> original
    ) {
        var base = original.call(instance, s, identifierPredicate);
        original.call(instance, s, DataExtensionUtil.OptInIdentifierPredicate.of(path -> path.method_12832().endsWith(".json5")))
            .forEach((identifier, resource) -> base.putIfAbsent(
                identifier, new class_3298(resource.method_45304(), () -> coerceJson(resource.method_14482()))
            ));
        return base;
    }
}
