package io.wispforest.owo.braid.core;

import io.wispforest.owo.braid.core.events.*;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.widget.InheritedWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.BraidApp;
import io.wispforest.owo.ui.util.DisposableScreen;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.jetbrains.annotations.Nullable;

public class BraidScreen extends class_437 implements DisposableScreen {

    protected final EventBinding eventBinding = new EventBinding.Default();
    protected final Surface.Default surface = new Surface.Default();

    protected final Settings settings;
    protected final Widget rootWidget;
    public AppState state;

    public BraidScreen(Settings settings, Widget rootWidget) {
        super(class_2561.method_43473());
        this.settings = settings;
        this.rootWidget = rootWidget;
    }

    public BraidScreen(Widget rootWidget) {
        this(new Settings(), rootWidget);
    }

    @Override
    protected void method_25426() {
        super.method_25426();

        if (this.state == null) {
            var widget = this.settings.useBraidAppWidget
                ? new BraidApp(this.rootWidget)
                : this.rootWidget;

            this.state = new AppState(
                null,
                AppState.formatName("BraidScreen", this.rootWidget),
                this.field_22787,
                this.surface,
                this.eventBinding,
                new BraidScreenProvider(this, widget)
            );
        }
    }

    @Override
    public void method_25394(class_332 graphics, int mouseX, int mouseY, float delta) {
        super.method_25394(graphics, mouseX, mouseY, delta);

        this.eventBinding.add(new MouseMoveEvent(mouseX, mouseY));
        this.state.processEvents(
            this.field_22787.method_61966().method_60636()
        );

        this.state.draw(graphics);
    }

    @Override
    public void dispose() {
        this.state.dispose();
    }

    @Override
    public boolean method_25421() {
        return this.settings.shouldPause;
    }

    @Override
    public boolean method_25402(class_11909 click, boolean doubled) {
        this.eventBinding.add(new MouseButtonPressEvent(click.method_74245(), click.comp_4797()));
        return true;
    }

    @Override
    public boolean method_25406(class_11909 click) {
        this.eventBinding.add(new MouseButtonReleaseEvent(click.method_74245(), click.comp_4797()));
        return true;
    }

    @Override
    public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.eventBinding.add(new MouseScrollEvent(horizontalAmount, verticalAmount));
        return true;
    }

    @Override
    public boolean method_25404(class_11908 input) {
        this.eventBinding.add(new KeyPressEvent(input.comp_4795(), input.comp_4796(), input.comp_4797()));
        return super.method_25404(input);
    }

    @Override
    public boolean method_16803(class_11908 input) {
        this.eventBinding.add(new KeyReleaseEvent(input.comp_4795(), input.comp_4796(), input.comp_4797()));
        return true;
    }

    @Override
    public boolean method_25400(class_11905 input) {
        this.eventBinding.add(new CharInputEvent((char) input.comp_4793(), input.comp_4794()));
        return true;
    }

    // ---

    public static @Nullable BraidScreen maybeOf(BuildContext context) {
        var provider = context.getAncestor(BraidScreenProvider.class);
        return provider != null ? provider.screen : null;
    }

    public static class Settings {
        public boolean shouldPause = true;
        public boolean useBraidAppWidget = true;
    }
}

class BraidScreenProvider extends InheritedWidget {

    public final BraidScreen screen;

    public BraidScreenProvider(BraidScreen screen, Widget child) {
        super(child);
        this.screen = screen;
    }

    @Override
    public boolean mustRebuildDependents(InheritedWidget newWidget) {
        return false;
    }
}