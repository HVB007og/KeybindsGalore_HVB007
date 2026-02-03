package io.wispforest.owo.ui.util;

import io.wispforest.owo.Owo;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.parsing.UIModelLoader;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_368;
import net.minecraft.class_374;
import net.minecraft.class_5481;

@ApiStatus.Internal
public class UIErrorToast implements class_368 {

    private final List<class_5481> errorMessage;
    private final class_327 textRenderer;
    private final int width;

    public UIErrorToast(Throwable error) {
        this.textRenderer = class_310.method_1551().field_1772;
        var texts = this.initText(String.valueOf(error.getMessage()), (consumer) -> {
            var stackTop = error.getStackTrace()[0];
            var errorLocation = stackTop.getClassName().split("\\.");

            consumer.accept(class_2561.method_43470("Type: ").method_27692(class_124.field_1061)
                    .method_10852(class_2561.method_43470(error.getClass().getSimpleName()).method_27692(class_124.field_1080)));
            consumer.accept(class_2561.method_43470("Thrown by: ").method_27692(class_124.field_1061)
                    .method_10852(class_2561.method_43470(errorLocation[errorLocation.length - 1] + ":" + stackTop.getLineNumber()).method_27692(class_124.field_1080)));
        });

        this.width = Math.min(240, TextOps.width(textRenderer, texts) + 8);
        this.errorMessage = this.wrap(texts);
    }

    public UIErrorToast(String message) {
        this.textRenderer = class_310.method_1551().field_1772;
        var texts = this.initText(message, (consumer) -> {
            consumer.accept(class_2561.method_43470("No context provided").method_27692(class_124.field_1080));
        });
        this.width = Math.min(240, TextOps.width(textRenderer, texts) + 8);
        this.errorMessage = this.wrap(texts);
    }

    public static void report(String message) {
        logErrorsDuringInitialLoad();
        class_310.method_1551().method_1566().method_1999(new UIErrorToast(message));
    }

    public static void report(Throwable error) {
        logErrorsDuringInitialLoad();
        class_310.method_1551().method_1566().method_1999(new UIErrorToast(error));
    }

    private static void logErrorsDuringInitialLoad() {
        if (UIModelLoader.hasCompletedInitialLoad()) return;

        var throwable = new Throwable();
        Owo.LOGGER.error(
                "An owo-ui error has occurred during the initial resource reload (on thread {}). This is likely a bug caused by *some* other mod initializing an owo-config screen significantly too early - please report it at https://github.com/wisp-forest/owo-lib/issues",
                Thread.currentThread().getName(),
                throwable
        );
    }

    private class_369 visibility = class_369.field_2209;

    @Override
    public void method_61989(class_374 manager, long time) {
        this.visibility = time > 10000 ? class_369.field_2209 : class_369.field_2210;
    }

    @Override
    public class_369 method_61988() {
        return this.visibility;
    }

    @Override
    public void method_1986(class_332 context, class_327 textRenderer, long startTime) {
        var owoContext = OwoUIGraphics.of(context);

        owoContext.method_25294(0, 0, this.method_29049(), this.method_29050(), 0x77000000);
        owoContext.drawRectOutline(0, 0, this.method_29049(), this.method_29050(), 0xA7FF0000);

        int xOffset = this.method_29049() / 2 - this.textRenderer.method_30880(this.errorMessage.get(0)) / 2;
        owoContext.method_35720(this.textRenderer, this.errorMessage.get(0), 4 + xOffset, 4, 0xFFFFFFFF);

        for (int i = 1; i < this.errorMessage.size(); i++) {
            owoContext.method_51430(this.textRenderer, this.errorMessage.get(i), 4, 4 + i * 11, 0xFFFFFFFF, false);
        }
    }

    @Override
    public int method_29050() {
        return 6 + this.errorMessage.size() * 11;
    }

    @Override
    public int method_29049() {
        return this.width;
    }

    private List<class_2561> initText(String errorMessage, Consumer<Consumer<class_2561>> contextAppender) {
        final var texts = new ArrayList<class_2561>();
        texts.add(class_2561.method_43470("owo-ui error").method_27692(class_124.field_1061));

        texts.add(class_2561.method_43470(" "));
        contextAppender.accept(texts::add);
        texts.add(class_2561.method_43470(" "));

        texts.add(class_2561.method_43470(errorMessage));

        texts.add(class_2561.method_43470(" "));
        texts.add(class_2561.method_43470("Check your log for details").method_27692(class_124.field_1080));

        return texts;
    }

    private List<class_5481> wrap(List<class_2561> message) {
        var list = new ArrayList<class_5481>();
        for (var text : message) list.addAll(this.textRenderer.method_1728(text, this.method_29049() - 8));
        return list;
    }

    @Override
    public Object method_1987() {
        return Type.VERY_TYPE;
    }

    enum Type {
        VERY_TYPE
    }
}
