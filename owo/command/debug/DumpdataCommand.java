package io.wispforest.owo.command.debug;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.Owo;
import io.wispforest.owo.ops.TextOps;
import java.util.regex.Pattern;
import net.minecraft.class_11362;
import net.minecraft.class_124;
import net.minecraft.class_1675;
import net.minecraft.class_2168;
import net.minecraft.class_2203;
import net.minecraft.class_2378;
import net.minecraft.class_239;
import net.minecraft.class_2509;
import net.minecraft.class_2512;
import net.minecraft.class_2520;
import net.minecraft.class_2561;
import net.minecraft.class_3965;
import net.minecraft.class_7923;
import net.minecraft.class_8942;
import net.minecraft.class_9326;
import net.minecraft.class_9334;

import static net.minecraft.class_2170.method_9244;
import static net.minecraft.class_2170.method_9247;

public class DumpdataCommand {

    private static final int GENERAL_PURPLE = 0xB983FF;
    private static final int KEY_BLUE = 0x94B3FD;
    private static final int VALUE_BLUE = 0x94DAFF;

    public static void register(CommandDispatcher<class_2168> dispatcher) {
        dispatcher.register(method_9247("dumpdata")
                .then(method_9247("item").executes(withRootPath(DumpdataCommand::executeItem))
                        .then(method_9244("nbt_path", class_2203.method_9360()).executes(withPathArg(DumpdataCommand::executeItem))))
                .then(method_9247("block").executes(withRootPath(DumpdataCommand::executeBlock))
                        .then(method_9244("nbt_path", class_2203.method_9360()).executes(withPathArg(DumpdataCommand::executeBlock))))
                .then(method_9247("entity").executes(withRootPath(DumpdataCommand::executeEntity))
                        .then(method_9244("nbt_path", class_2203.method_9360()).executes(withPathArg(DumpdataCommand::executeEntity)))));
    }

    private static Command<class_2168> withRootPath(DataDumper dumper) {
        return context -> dumper.dump(context, class_2203.method_9360().method_9362(new StringReader("")));
    }

    private static Command<class_2168> withPathArg(DataDumper dumper) {
        return context -> {
            final var path = class_2203.method_9358(context, "nbt_path");
            return dumper.dump(context, path);
        };
    }

    private static int executeItem(CommandContext<class_2168> context, class_2203.class_2209 path) throws CommandSyntaxException {
        final var source = context.getSource();
        final var stack = source.method_44023().method_6047();

        informationHeader(source, "Item");
        sendIdentifier(source, stack.method_7909(), class_7923.field_41178);

        if (stack.method_58694(class_9334.field_50072) != null) {
            feedback(source, TextOps.withColor("Durability: §" + stack.method_58694(class_9334.field_50072),
                    TextOps.color(class_124.field_1080), KEY_BLUE));
        } else {
            feedback(source, TextOps.withFormatting("Not damageable", class_124.field_1080));
        }

        if (!stack.method_57380().method_57848()) {
            feedback(source, TextOps.withFormatting("Component changes" + formatPath(path) + ": ", class_124.field_1080)
                    .method_10852(class_2512.method_32270(getPath(class_9326.field_49589.encodeStart(class_2509.field_11560, stack.method_57380()).getOrThrow(), path))));
        } else {
            feedback(source, TextOps.withFormatting("No component changes", class_124.field_1080));
        }

        feedback(source, TextOps.withFormatting("-----------------------", class_124.field_1080));

        return 0;
    }

    private static int executeEntity(CommandContext<class_2168> context, class_2203.class_2209 path) throws CommandSyntaxException {
        final var source = context.getSource();
        final var player = source.method_44023();

        final var target = class_1675.method_18075(
                player,
                player.method_5836(0),
                player.method_5836(0).method_1019(player.method_5828(0).method_1021(5)),
                player.method_5829().method_18804(player.method_5828(0).method_1021(5)).method_1014(1),
                entity -> true,
                5 * 5);

        if (target == null || target.method_17783() != class_239.class_240.field_1331) {
            source.method_9213(TextOps.concat(Owo.PREFIX, class_2561.method_43470("You're not looking at an entity")));
            return 1;
        }

        final var entity = target.method_17782();

        informationHeader(source, "Entity");
        sendIdentifier(source, entity.method_5864(), class_7923.field_41177);

        var writeView = class_11362.method_71458(new class_8942.class_11340(Owo.LOGGER));
        entity.method_5662(writeView);

        feedback(source, TextOps.withFormatting("NBT" + formatPath(path) + ": ", class_124.field_1080)
                .method_10852(class_2512.method_32270(getPath(writeView.method_71475(), path))));

        feedback(source, TextOps.withFormatting("-----------------------", class_124.field_1080));

        return 0;
    }

    private static int executeBlock(CommandContext<class_2168> context, class_2203.class_2209 path) throws CommandSyntaxException {
        final var source = context.getSource();
        final var player = source.method_44023();

        final var target = player.method_5745(5, 0, false);

        if (target.method_17783() != class_239.class_240.field_1332) {
            source.method_9213(TextOps.concat(Owo.PREFIX, class_2561.method_43470("You're not looking at a block")));
            return 1;
        }

        final var pos = ((class_3965) target).method_17777();

        final var blockState = player.method_51469().method_8320(pos);
        final var blockStateString = blockState.toString();

        informationHeader(source, "Block");
        sendIdentifier(source, blockState.method_26204(), class_7923.field_41175);

        if (blockStateString.contains("[")) {
            feedback(source, TextOps.withFormatting("State properties: ", class_124.field_1080));

            var stateString = blockStateString.split(Pattern.quote("["))[1];
            stateString = stateString.substring(0, stateString.length() - 1);
            var stateInfo = stateString.replaceAll("=", ": §").split(",");

            for (var property : stateInfo) {
                feedback(source, TextOps.withColor("    " + property, KEY_BLUE, VALUE_BLUE));
            }
        } else {
            feedback(source, TextOps.withFormatting("No state properties", class_124.field_1080));
        }

        final var blockEntity = player.method_51469().method_8321(pos);
        if (blockEntity != null) {
            feedback(source, TextOps.withFormatting("Block Entity NBT" + formatPath(path) + ": ", class_124.field_1080)
                    .method_10852(class_2512.method_32270(getPath(blockEntity.method_38244(player.method_56673()), path))));
        } else {
            feedback(source, TextOps.withFormatting("No block entity", class_124.field_1080));
        }

        feedback(source, TextOps.withFormatting("-----------------------", class_124.field_1080));

        return 0;
    }

    private static <T> void sendIdentifier(class_2168 source, T object, class_2378<T> registry) {
        final var id = registry.method_10221(object).toString().split(":");
        feedback(source, TextOps.withColor("Identifier: §" + id[0] + ":§" + id[1], TextOps.color(class_124.field_1080), KEY_BLUE, VALUE_BLUE));
    }

    private static void informationHeader(class_2168 source, String name) {
        feedback(source, TextOps.withColor("---[§ " + name + " Information §]---",
                TextOps.color(class_124.field_1080), GENERAL_PURPLE, TextOps.color(class_124.field_1080)));
    }

    private static void feedback(class_2168 source, class_2561 message) {
        source.method_9226(() -> message, false);
    }

    private static String formatPath(class_2203.class_2209 path) {
        return path.toString().isBlank() ? "" : "(" + path + ")";
    }

    private static class_2520 getPath(class_2520 nbt, class_2203.class_2209 path) throws CommandSyntaxException {
        return path.method_9366(nbt).iterator().next();
    }

    @FunctionalInterface
    private interface DataDumper {
        int dump(CommandContext<class_2168> context, class_2203.class_2209 path) throws CommandSyntaxException;
    }
}
