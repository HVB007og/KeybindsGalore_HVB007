package io.wispforest.owo.braid.util;

import io.wispforest.owo.Owo;
import io.wispforest.owo.braid.core.Alignment;
import io.wispforest.owo.braid.core.AppState;
import io.wispforest.owo.braid.core.EventBinding;
import io.wispforest.owo.braid.core.Surface;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Align;
import org.apache.commons.lang3.mutable.MutableObject;

import java.lang.ref.Cleaner;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5684;

public class BraidTooltipComponent implements class_5684 {

    private final AppState app;
    private final EmbedderRoot.Instance instance;

    public BraidTooltipComponent(Widget widget) {
        var embedderInstance = new MutableObject<EmbedderRoot.Instance>();
        this.app = new AppState(
            Owo.LOGGER,
            AppState.formatName("BraidTooltipComponent", widget),
            class_310.method_1551(),
            new Surface.Default(),
            new EventBinding.Headless(),
            new Align(
                Alignment.TOP_LEFT,
                new EmbedderRoot(
                    embedderInstance::setValue,
                    widget
                )
            )
        );

        this.app.processEvents(0);
        this.instance = embedderInstance.getValue();

        APP_CLEANER.register(this, new CleanCallback(this.app));
    }

    @Override
    public void method_32666(class_327 font, int x, int y, int width, int height, class_332 context) {
        context.push().translate(x, y);
        this.app.draw(context);
        context.pop();
    }

    @Override
    public int method_32664(class_327 font) {
        return (int) this.instance.transform.width();
    }

    @Override
    public int method_32661(class_327 font) {
        return (int) this.instance.transform.height();
    }

    // ---

    private static final Cleaner APP_CLEANER = Cleaner.create();

    private record CleanCallback(AppState app) implements Runnable {
        @Override
        public void run() {
            class_310.method_1551().method_63588(this.app::dispose);
        }
    }
}
