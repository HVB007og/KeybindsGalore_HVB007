package io.wispforest.owo.command.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.Owo;
import io.wispforest.owo.ops.TextOps;
import net.minecraft.class_11362;
import net.minecraft.class_124;
import net.minecraft.class_2168;
import net.minecraft.class_2203;
import net.minecraft.class_2487;
import net.minecraft.class_2512;
import net.minecraft.class_8942;

import static net.minecraft.class_2170.method_9244;
import static net.minecraft.class_2170.method_9247;

public class CcaDataCommand {

    public static void register(CommandDispatcher<class_2168> dispatcher) {
        dispatcher.register(method_9247("cca-data").executes(CcaDataCommand::executeDumpAll)
                .then(method_9244("path", class_2203.method_9360()).executes(CcaDataCommand::executeDumpPath)));
    }

    private static int executeDumpAll(CommandContext<class_2168> context) throws CommandSyntaxException {
        final var player = context.getSource().method_44023();
        final var writeView = class_11362.method_71458(new class_8942.class_11340(Owo.LOGGER));
        player.method_5662(writeView);

        final var nbt = writeView.method_71475().method_10562("cardinal_components").orElseGet(class_2487::new);

        context.getSource().method_9226(() -> TextOps.concat(Owo.PREFIX, TextOps.withFormatting("CCA Data:", class_124.field_1080)), false);
        context.getSource().method_9226(() -> class_2512.method_32270(nbt), false);

        return 0;
    }

    private static int executeDumpPath(CommandContext<class_2168> context) throws CommandSyntaxException {
        final var player = context.getSource().method_44023();
        final var path = class_2203.method_9358(context, "path");

        final var writeView = class_11362.method_71458(new class_8942.class_11340(Owo.LOGGER));
        player.method_5662(writeView);

        final var nbt = path.method_9366(writeView.method_71475().method_10562("cardinal_components").orElseGet(class_2487::new)).iterator().next();

        context.getSource().method_9226(() -> TextOps.concat(Owo.PREFIX, TextOps.withFormatting("CCA Data:", class_124.field_1080)), false);
        context.getSource().method_9226(() -> class_2512.method_32270(nbt), false);

        return 0;
    }

}
