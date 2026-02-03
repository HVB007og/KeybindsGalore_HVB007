package io.wispforest.owo.command.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.Owo;
import io.wispforest.owo.ops.TextOps;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_2168;
import net.minecraft.class_2186;
import net.minecraft.class_2561;

import static net.minecraft.class_2170.method_9244;
import static net.minecraft.class_2170.method_9247;

public class HealCommand {

    public static void register(CommandDispatcher<class_2168> dispatcher) {
        dispatcher.register(method_9247("heal")
                .executes(HealCommand::executeFullHeal)
                .then(method_9244("amount", FloatArgumentType.floatArg(0))
                        .executes(HealCommand::executeSelfHeal))
                .then(method_9244("entity", class_2186.method_9309())
                        .executes(HealCommand::executeTargetedFullHeal)
                        .then(method_9244("amount", FloatArgumentType.floatArg(0))
                                .executes(HealCommand::executeTargetedHeal))));
    }

    private static int executeFullHeal(CommandContext<class_2168> context) throws CommandSyntaxException {
        var target = context.getSource().method_9229();
        return executeHeal(
                context,
                target,
                target instanceof class_1309 living ? living.method_6063() : Float.MAX_VALUE
        );
    }

    private static int executeSelfHeal(CommandContext<class_2168> context) throws CommandSyntaxException {
        return executeHeal(
                context,
                context.getSource().method_9229(),
                FloatArgumentType.getFloat(context, "amount")
        );
    }

    private static int executeTargetedFullHeal(CommandContext<class_2168> context) throws CommandSyntaxException {
        var target = class_2186.method_9313(context, "entity");
        return executeHeal(
                context,
                target,
                target instanceof class_1309 living ? living.method_6063() : Float.MAX_VALUE
        );
    }

    private static int executeTargetedHeal(CommandContext<class_2168> context) throws CommandSyntaxException {
        return executeHeal(
                context,
                class_2186.method_9313(context, "entity"),
                FloatArgumentType.getFloat(context, "amount")
        );
    }

    private static int executeHeal(CommandContext<class_2168> context, class_1297 entity, float amount) throws CommandSyntaxException {
        if (entity instanceof class_1309 living) {
            float healed = living.method_6032();
            living.method_6025(amount);
            healed = living.method_6032() - healed;

            float thankYouMojang = healed;
            context.getSource().method_9226(() -> TextOps.concat(Owo.PREFIX, TextOps.withColor("healed §" + thankYouMojang + " §hp",
                    TextOps.color(class_124.field_1080), OwoDebugCommands.GENERAL_PURPLE, TextOps.color(class_124.field_1080))), false);
        } else {
            context.getSource().method_9213(TextOps.concat(Owo.PREFIX, class_2561.method_30163("Cannot heal non living entity")));
        }

        return (int) Math.floor(amount);
    }

}

//chyz was here