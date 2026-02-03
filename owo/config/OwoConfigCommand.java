package io.wispforest.owo.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.wispforest.owo.Owo;
import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.owo.config.ui.ConfigScreenProviders;
import io.wispforest.owo.ops.TextOps;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_7157;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public class OwoConfigCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, class_7157 access) {
        dispatcher.register(ClientCommandManager.literal("owo-config")
                .then(ClientCommandManager.argument("config_id", new ConfigScreenArgumentType())
                        .executes(context -> {
                            var screen = context.getArgument("config_id", ConfigScreen.class);
                            class_310.method_1551().method_63588(() -> class_310.method_1551().method_1507(screen));
                            return 0;
                        })));
    }

    private static class ConfigScreenArgumentType implements ArgumentType<class_437> {

        private static final SimpleCommandExceptionType NO_SUCH_CONFIG_SCREEN = new SimpleCommandExceptionType(
                TextOps.concat(Owo.PREFIX, class_2561.method_43470("no config screen with that id"))
        );

        @Override
        public class_437 parse(StringReader reader) throws CommandSyntaxException {
            var provider = ConfigScreenProviders.get(reader.readString());
            if (provider == null) throw NO_SUCH_CONFIG_SCREEN.create();

            return provider.apply(null);
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            var configNames = new ArrayList<String>();
            ConfigScreenProviders.forEach((s, screenFunction) -> configNames.add(s));
            return class_2172.method_9265(configNames, builder);
        }
    }
}
