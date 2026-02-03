package io.wispforest.owo.braid.util;

import com.google.common.base.Preconditions;
import io.wispforest.owo.Owo;
import io.wispforest.owo.braid.core.Alignment;
import io.wispforest.owo.braid.core.AppState;
import io.wispforest.owo.braid.core.EventBinding;
import io.wispforest.owo.braid.core.Surface;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.widget.InheritedWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Align;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_368;
import net.minecraft.class_374;

public class BraidToast implements class_368 {

    private final @Nullable Duration timeout;
    private final Object token;
    private final AppState app;

    private EmbedderRoot.Instance rootInstance;

    private BraidToast(@Nullable Duration timeout, @Nullable Object token, Widget widget) {
        this.timeout = timeout;
        this.token = token != null ? token : new Object();
        this.app = new AppState(
            Owo.LOGGER,
            AppState.formatName("BraidToast", widget),
            class_310.method_1551(),
            new Surface.Default(),
            new EventBinding.Headless(),
            new Align(
                Alignment.TOP_LEFT,
                new EmbedderRoot(
                    instance -> this.rootInstance = instance,
                    new BraidToastProvider(
                        this,
                        widget
                    )
                )
            )
        );

        this.app.processEvents(0);
    }

    public static void show(@Nullable Duration timeout, @Nullable Object token, Widget widget) {
        class_310.method_1551().method_1566().method_1999(new BraidToast(timeout, token, widget));
    }

    public static void hideWithToken(Object token) {
        var toast = class_310.method_1551().method_1566().method_1997(BraidToast.class, token);
        if (toast != null) {
            toast.visibility = class_369.field_2209;
        }
    }

    public static void hide(BuildContext context) {
        var provider = context.getAncestor(BraidToastProvider.class);
        Preconditions.checkNotNull(provider, "BraidToast.hide can only be used from inside a BraidToast's widget tree");

        provider.toast.visibility = class_369.field_2209;
    }

    // ---

    @ApiStatus.Internal
    public void dispose() {
        this.app.dispose();
    }

    @Override
    public void method_1986(class_332 graphics, class_327 font, long startTime) {
        this.app.draw(graphics);
    }

    @Override
    public int method_29049() {
        return (int) this.rootInstance.transform.width();
    }

    @Override
    public int method_29050() {
        return (int) this.rootInstance.transform.height();
    }

    // ---

    private class_369 visibility = class_369.field_2210;

    @Override
    public void method_61989(class_374 manager, long time) {
        if (this.timeout != null && time > this.timeout.toMillis()) {
            this.visibility = class_369.field_2209;
        }

        var tickCounter = class_310.method_1551().method_61966();
        this.app.processEvents(
            tickCounter.method_60636()
        );
    }

    @Override
    public class_369 method_61988() {
        return this.visibility;
    }

    @Override
    public Object method_1987() {
        return this.token;
    }
}

class BraidToastProvider extends InheritedWidget {

    public final BraidToast toast;

    public BraidToastProvider(BraidToast toast, Widget child) {
        super(child);
        this.toast = toast;
    }

    @Override
    public boolean mustRebuildDependents(InheritedWidget newWidget) {
        return false;
    }
}
