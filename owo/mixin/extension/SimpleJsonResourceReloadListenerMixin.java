package io.wispforest.owo.mixin.extension;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.JsonOps;
import io.wispforest.owo.Owo;
import io.wispforest.owo.mixin.extension.recipe.RecipeManagerAccessor;
import io.wispforest.owo.util.RecipeRemainderStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.Reader;
import java.util.HashMap;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_4309;
import net.minecraft.class_7654;

@Mixin(class_4309.class)
public abstract class SimpleJsonResourceReloadListenerMixin {

    @WrapOperation(
        method = "scanDirectory(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/FileToIdConverter;Lcom/mojang/serialization/DynamicOps;Lcom/mojang/serialization/Codec;Ljava/util/Map;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/StrictJsonParser;parse(Ljava/io/Reader;)Lcom/google/gson/JsonElement;"
        )
    )
    private static JsonElement loadRecipeExtensions(
        Reader jsonReader,
        Operation<JsonElement> original,
        @Local(argsOnly = true) class_7654 finder,
        @Local(ordinal = 1) class_2960 recipeId
    ) {
        var element = original.call(jsonReader);

        if (RecipeManagerAccessor.owo$getFinder() == finder && element instanceof JsonObject json) {
            if (json.has(Owo.id("remainders").toString())) {
                var remainders = new HashMap<class_1792, class_1799>();

                for (var remainderEntry : json.getAsJsonObject(Owo.id("remainders").toString()).entrySet()) {
                    var item = class_3518.method_15256(new JsonPrimitive(remainderEntry.getKey()), remainderEntry.getKey());

                    if (remainderEntry.getValue().isJsonObject()) {
                        var remainderStack = class_1799.field_24671.parse(
                            JsonOps.INSTANCE,
                            remainderEntry.getValue().getAsJsonObject()
                        ).getOrThrow(JsonParseException::new);
                        remainders.put(item.comp_349(), remainderStack);
                    } else {
                        var remainderItem = class_3518.method_15256(remainderEntry.getValue(), "item");
                        remainders.put(item.comp_349(), new class_1799(remainderItem));
                    }
                }

                if (!remainders.isEmpty()) RecipeRemainderStorage.store(recipeId, remainders);
            }
        }

        return element;
    }
}
