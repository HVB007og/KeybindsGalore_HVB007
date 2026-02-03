package io.wispforest.owo.ops;

import io.wispforest.owo.mixin.SetComponentsFunctionAccessor;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.class_141;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_219;
import net.minecraft.class_2960;
import net.minecraft.class_44;
import net.minecraft.class_55;
import net.minecraft.class_5662;
import net.minecraft.class_77;
import net.minecraft.class_79;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A simple utility class to make injecting simple items or
 * ItemStacks into one or multiple LootTables a one-line operation
 */
public final class LootOps {

    private LootOps() {}

    private static final Map<class_2960[], Supplier<class_79>> ADDITIONS = new HashMap<>();

    /**
     * Injects a single item entry into the specified LootTable(s)
     *
     * @param item         The item to inject
     * @param chance       The chance for the item to actually generate
     * @param targetTables The LootTable(s) to inject into
     */
    public static void injectItem(class_1935 item, float chance, class_2960... targetTables) {
        ADDITIONS.put(targetTables, () -> class_77.method_411(item).method_421(class_219.method_932(chance)).method_419());
    }

    /**
     * Injects an item entry into the specified LootTable(s),
     * with a random count between {@code min} and {@code max}
     *
     * @param item         The item to inject
     * @param chance       The chance for the item to actually generate
     * @param min          The minimum amount of items to generate
     * @param max          The maximum amount of items to generate
     * @param targetTables The LootTable(s) to inject into
     */
    public static void injectItemWithCount(class_1935 item, float chance, int min, int max, class_2960... targetTables) {
        ADDITIONS.put(targetTables, () -> class_77.method_411(item)
                .method_421(class_219.method_932(chance))
                .method_438(class_141.method_621(class_5662.method_32462(min, max)))
                .method_419());
    }

    /**
     * Injects a single ItemStack entry into the specified LootTable(s)
     *
     * @param stack        The ItemStack to inject
     * @param chance       The chance for the ItemStack to actually generate
     * @param targetTables The LootTable(s) to inject into
     */
    public static void injectItemStack(class_1799 stack, float chance, class_2960... targetTables) {
        ADDITIONS.put(targetTables, () -> class_77.method_411(stack.method_7909())
                .method_421(class_219.method_932(chance))
                .method_438(() -> SetComponentsFunctionAccessor.createSetComponentsLootFunction(List.of(), stack.method_57380()))
                .method_438(class_141.method_621(class_44.method_32448(stack.method_7947())))
                .method_419());
    }

    /**
     * Test is {@code target} matches against any of the {@code predicates}.
     * Used to easily target multiple LootTables
     *
     * @param target     The target identifier (this would be the current table)
     * @param predicates The identifiers to test against (this would be the targeted tables)
     * @return {@code true} if target matches any of the predicates
     */
    public static boolean anyMatch(class_2960 target, class_2960... predicates) {
        for (var predicate : predicates) if (target.equals(predicate)) return true;
        return false;
    }

    @ApiStatus.Internal
    public static void registerListener() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, provider) -> {
            ADDITIONS.forEach((identifiers, lootPoolEntrySupplier) -> {
                if (anyMatch(key.method_29177(), identifiers)) tableBuilder.method_336(class_55.method_347().with(lootPoolEntrySupplier.get()));
            });
        });
    }

}
