package io.wispforest.owo.mixin.extension.recipe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.wispforest.owo.util.RecipeRemainderStorage;
import net.minecraft.class_1657;
import net.minecraft.class_1734;
import net.minecraft.class_1799;
import net.minecraft.class_1860;
import net.minecraft.class_1863;
import net.minecraft.class_1937;
import net.minecraft.class_2371;
import net.minecraft.class_3956;
import net.minecraft.class_8786;
import net.minecraft.class_9694;
import net.minecraft.class_9695;
import net.minecraft.world.item.crafting.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Function;

@Mixin(class_1734.class)
public abstract class ResultSlotMixin {

    @Shadow
    @Final
    private class_1657 player;

    @Inject(
        method = "onTake",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/CraftingContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V",
            ordinal = 1
        )
    )
    private void fixRemainderStacking(
        class_1657 player,
        class_1799 stack,
        CallbackInfo ci,
        @Local(ordinal = 2) class_1799 remainderStack
    ) {
        if (remainderStack.method_7947() > remainderStack.method_7914()) {
            int excess = remainderStack.method_7947() - remainderStack.method_7914();
            remainderStack.method_7934(excess);

            this.player.method_31548().method_7398(remainderStack.method_46651(excess));
        }
    }

    @WrapOperation(
        method = "getRemainingItems",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"
        )
    )
    private <I extends class_9695, T extends class_1860<I>> Optional<class_8786<T>> captureRecipeEntry(
        class_1863 instance,
        class_3956<T> type,
        I input,
        class_1937 world,
        Operation<Optional<class_8786<T>>> original,
        @Share(value = "recipe_entry") LocalRef<Optional<class_8786<T>>> recipeEntry
    ) {
        var entry = original.call(instance, type, input, world);

        recipeEntry.set(entry);

        return entry;
    }

    @WrapOperation(
        method = "getRemainingItems",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Optional;map(Ljava/util/function/Function;)Ljava/util/Optional;"
        )
    )
    private <I extends class_9695, T extends class_1860<I>> Optional<class_2371<class_1799>> addRecipeSpecificRemainders(
        Optional<T> instance,
        Function<? super T, ? extends class_2371<class_1799>> mapper,
        Operation<Optional<class_2371<class_1799>>> original,
        @Share(value = "recipe_entry") LocalRef<Optional<class_8786<?>>> recipeEntry,
        @Local(argsOnly = true) class_9694 input
    ) {
        var recipeEntryOptional = recipeEntry.get();

        return original.call(instance, mapper)
            .map(remainders -> {
                var recipeId = recipeEntryOptional.get().comp_1932().method_29177();

                if (RecipeRemainderStorage.has(recipeId)) {
                    var owoRemainders = RecipeRemainderStorage.get(recipeId);

                    for (int i = 0; i < remainders.size(); ++i) {
                        var item = input.method_59984(i).method_7909();
                        if (!owoRemainders.containsKey(item)) continue;

                        remainders.set(i, owoRemainders.get(item).method_7972());
                    }
                }

                return remainders;
            });
    }
}
