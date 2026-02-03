package io.wispforest.owo.command.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.class_2168;
import net.minecraft.class_2287;
import net.minecraft.class_7157;
import net.minecraft.class_9433;

import static net.minecraft.class_2170.method_9244;
import static net.minecraft.class_2170.method_9247;

public class MakeLootContainerCommand {

    public static void register(CommandDispatcher<class_2168> dispatcher, class_7157 registryAccess) {
        dispatcher.register(method_9247("make-loot-container")
                .then(method_9244("item", class_2287.method_9776(registryAccess))
                        .then(method_9244("loot_table", class_9433.method_58482(registryAccess))
                                .executes(MakeLootContainerCommand::execute))));
    }

    // TODO: reimplement
    private static int execute(CommandContext<class_2168> context) throws CommandSyntaxException {
//        var targetStack = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false);
//        var tableId = RegistryEntryArgumentType.getLootTable(context, "loot_table");
//
//        var blockEntityTag = targetStack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
//        if (blockEntityTag == null) {
//            blockEntityTag = TypedEntityData.create()
//        }
//
//        blockEntityTag = blockEntityTag.apply(x -> {
//            x.putString("LootTable", tableId.getIdAsString());
//        });
//        targetStack.set(DataComponentTypes.BLOCK_ENTITY_DATA, blockEntityTag);
//
//        context.getSource().getPlayer().getInventory().offerOrDrop(targetStack);

        return 0;
    }
}
