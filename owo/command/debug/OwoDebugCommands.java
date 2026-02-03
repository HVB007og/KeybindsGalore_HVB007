package io.wispforest.owo.command.debug;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.logging.LogUtils;
import io.wispforest.owo.Owo;
import io.wispforest.owo.command.EnumArgumentType;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.renderdoc.RenderDoc;
import io.wispforest.owo.renderdoc.RenderdocScreen;
import io.wispforest.owo.ui.hud.HudInspectorScreen;
import io.wispforest.owo.ui.parsing.ConfigureHotReloadScreen;
import io.wispforest.owo.ui.parsing.UIModelLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_124;
import net.minecraft.class_2168;
import net.minecraft.class_2172;
import net.minecraft.class_2232;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2558;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3965;
import net.minecraft.class_4153;
import net.minecraft.class_7923;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.event.Level;

import static net.minecraft.class_2170.method_9244;
import static net.minecraft.class_2170.method_9247;

@ApiStatus.Internal
public class OwoDebugCommands {

    private static final EnumArgumentType<Level> LEVEL_ARGUMENT_TYPE =
        EnumArgumentType.create(Level.class, "'{}' is not a valid logging level");

    private static final SuggestionProvider<class_2168> POI_TYPES =
        (context, builder) -> class_2172.method_9270(class_7923.field_41128.method_10235(), builder);

    private static final SimpleCommandExceptionType NO_POI_TYPE = new SimpleCommandExceptionType(class_2561.method_30163("Invalid POI type"));
    public static final int GENERAL_PURPLE = 0xB983FF;
    public static final int KEY_BLUE = 0x94B3FD;
    public static final int VALUE_BLUE = 0x94DAFF;

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            dispatcher.register(method_9247("logger").then(method_9244("level", LEVEL_ARGUMENT_TYPE).executes(context -> {
                final var level = LEVEL_ARGUMENT_TYPE.get(context, "level");
                LogUtils.configureRootLoggingLevel(level);

                context.getSource().method_9226(() -> TextOps.concat(Owo.PREFIX, class_2561.method_30163("global logging level set to: §9" + level)), false);
                return 0;
            })));

            dispatcher.register(method_9247("query-poi").then(method_9244("poi_type", class_2232.method_9441()).suggests(POI_TYPES)
                .then(method_9244("radius", IntegerArgumentType.integer()).executes(context -> {
                    var player = context.getSource().method_44023();
                    var poiType = class_7923.field_41128.method_17966(class_2232.method_9443(context, "poi_type"))
                        .orElseThrow(NO_POI_TYPE::create);

                    var entries = ((class_3218) player.method_51469()).method_19494().method_19125(type -> type.comp_349() == poiType,
                        player.method_24515(), IntegerArgumentType.getInteger(context, "radius"), class_4153.class_4155.field_18489).toList();

                    player.method_7353(TextOps.concat(Owo.PREFIX, TextOps.withColor("Found §" + entries.size() + " §entr" + (entries.size() == 1 ? "y" : "ies"),
                        TextOps.color(class_124.field_1080), GENERAL_PURPLE, TextOps.color(class_124.field_1080))), false);

                    for (var entry : entries) {

                        final var entryPos = entry.method_19141();
                        final var blockId = class_7923.field_41175.method_10221(player.method_51469().method_8320(entryPos).method_26204()).toString();
                        final var posString = "(" + entryPos.method_10263() + " " + entryPos.method_10264() + " " + entryPos.method_10260() + ")";

                        final var message = TextOps.withColor("-> §" + blockId + " §" + posString,
                            TextOps.color(class_124.field_1080), KEY_BLUE, VALUE_BLUE);

                        message.method_27694(style -> style.method_10958(new class_2558.class_10610(
                                "/tp " + entryPos.method_10263() + " " + entryPos.method_10264() + " " + entryPos.method_10260()))
                            .method_10949(new class_2568.class_10613(class_2561.method_30163("Click to teleport"))));

                        player.method_7353(message, false);
                    }

                    return entries.size();
                }))));

            dispatcher.register(method_9247("dumpfield").then(method_9244("field_name", StringArgumentType.string()).executes(context -> {
                final var targetField = StringArgumentType.getString(context, "field_name");
                final class_2168 source = context.getSource();
                final class_3222 player = source.method_44023();
                class_239 target = player.method_5745(5, 0, false);

                if (target.method_17783() != class_239.class_240.field_1332) {
                    source.method_9213(TextOps.concat(Owo.PREFIX, class_2561.method_43470("You're not looking at a block")));
                    return 1;
                }

                class_2338 pos = ((class_3965) target).method_17777();
                final var blockEntity = player.method_51469().method_8321(pos);

                if (blockEntity == null) {
                    source.method_9213(TextOps.concat(Owo.PREFIX, class_2561.method_43470(("No block entity"))));
                    return 1;
                }

                var blockEntityClass = blockEntity.getClass();

                try {
                    final var field = blockEntityClass.getDeclaredField(targetField);

                    if (!field.canAccess(blockEntity)) field.setAccessible(true);
                    final var value = field.get(blockEntity);

                    source.method_9226(() -> TextOps.concat(Owo.PREFIX, TextOps.withColor("Field value: §" + value, TextOps.color(class_124.field_1080), KEY_BLUE)), false);

                } catch (Exception e) {
                    source.method_9213(TextOps.concat(Owo.PREFIX, class_2561.method_43470("Could not access field - " + e.getClass().getSimpleName() + ": " + e.getMessage())));
                }

                return 0;
            })));

            MakeLootContainerCommand.register(dispatcher, registryAccess);
            DumpdataCommand.register(dispatcher);
            HealCommand.register(dispatcher);

            if (FabricLoader.getInstance().isModLoaded("cardinal-components-base")) {
                CcaDataCommand.register(dispatcher);
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static class Client {

        private static final SuggestionProvider<FabricClientCommandSource> LOADED_UI_MODELS =
            (context, builder) -> class_2172.method_9270(UIModelLoader.allLoadedModels(), builder);

        private static final SimpleCommandExceptionType NO_SUCH_UI_MODEL = new SimpleCommandExceptionType(class_2561.method_43470("No such UI model is loaded"));

        public static void register() {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
                dispatcher.register(ClientCommandManager.literal("owo-hud-inspect")
                    .executes(context -> {
                        class_310.method_1551().method_1507(new HudInspectorScreen());
                        return 0;
                    }));

                dispatcher.register(ClientCommandManager.literal("owo-ui-set-reload-path")
                    .then(ClientCommandManager.argument("model-id", class_2232.method_9441()).suggests(LOADED_UI_MODELS).executes(context -> {
                        var modelId = context.getArgument("model-id", class_2960.class);
                        if (UIModelLoader.getPreloaded(modelId) == null) throw NO_SUCH_UI_MODEL.create();

                        class_310.method_1551().method_1507(new ConfigureHotReloadScreen(modelId, null));
                        return 0;
                    })));

                if (RenderDoc.isAvailable()) {
                    dispatcher.register(ClientCommandManager.literal("renderdoc").executes(context -> {
                        class_310.method_1551().method_1507(new RenderdocScreen());
                        return 1;
                    }).then(ClientCommandManager.literal("comment")
                        .then(ClientCommandManager.argument("capture_index", IntegerArgumentType.integer(0))
                            .then(ClientCommandManager.argument("comment", StringArgumentType.greedyString())
                                .executes(context -> {
                                    var capture = RenderDoc.getCapture(IntegerArgumentType.getInteger(context, "capture_index"));
                                    if (capture == null) {
                                        context.getSource().sendError(TextOps.concat(Owo.PREFIX, class_2561.method_30163("no such capture")));
                                        return 0;
                                    }

                                    RenderDoc.setCaptureComments(capture, StringArgumentType.getString(context, "comment"));
                                    context.getSource().sendFeedback(TextOps.concat(Owo.PREFIX, class_2561.method_30163("comment updated")));

                                    return 1;
                                })))));
                }
            });
        }
    }
}
