package io.wispforest.owo.ui.base;

import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.ParentUIComponent;
import io.wispforest.owo.ui.core.UIComponent;
import io.wispforest.owo.ui.inject.GreedyInputUIComponent;
import io.wispforest.owo.ui.util.DisposableScreen;
import io.wispforest.owo.ui.util.UIErrorToast;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_437;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * A minimal implementation of a Screen which fully
 * supports all aspects of the UI system. Implementing this class
 * is trivial, as you only need to provide implementations for
 * {@link #createAdapter()} to initialize the UI system and {@link #build(ParentUIComponent)}
 * which is where you declare your component hierarchy.
 * <p>
 * Should you be locked into a different superclass on your screen already,
 * you can easily copy all code from this class into your screen - as you
 * can see supporting the entire feature-set of owo-ui only requires
 * very few changes to how a vanilla screen works
 *
 * @param <R> The type of root component this screen uses
 */
public abstract class BaseOwoScreen<R extends ParentUIComponent> extends class_437 implements DisposableScreen {

    /**
     * The UI adapter of this screen. This handles
     * all user input as well as setting up GL state for rendering
     * and managing component focus
     */
    protected OwoUIAdapter<R> uiAdapter = null;

    /**
     * Whether this screen has encountered an unrecoverable
     * error during its lifecycle and should thus close
     * itself on the next frame
     */
    protected boolean invalid = false;

    protected BaseOwoScreen(class_2561 title) {
        super(title);
    }

    protected BaseOwoScreen() {
        this(class_2561.method_43473());
    }

    /**
     * Initialize the UI adapter for this screen. Usually
     * the body of this method will simply consist of a call
     * to {@link OwoUIAdapter#create(class_437, BiFunction)}
     *
     * @return The UI adapter for this screen to use
     */
    protected abstract @NotNull OwoUIAdapter<R> createAdapter();

    /**
     * Build the component hierarchy of this screen,
     * called after the adapter and root component have been
     * initialized by {@link #createAdapter()}
     *
     * @param rootComponent The root component created
     *                      in the previous initialization step
     */
    protected abstract void build(R rootComponent);

    @Override
    protected void method_25426() {
        if (this.invalid) return;

        // Check whether this screen was already initialized
        if (this.uiAdapter != null) {
            // If it was, only resize the adapter instead of recreating it - this preserves UI state
            this.uiAdapter.moveAndResize(0, 0, this.field_22789, this.field_22790);
            // Re-add it as a child to circumvent vanilla clearing them
            this.method_37063(this.uiAdapter);
        } else {
            try {
                this.uiAdapter = this.createAdapter();
                this.build(this.uiAdapter.rootComponent);

                this.uiAdapter.inflateAndMount();
            } catch (Exception error) {
                Owo.LOGGER.warn("Could not initialize owo screen", error);
                UIErrorToast.report(error);
                this.invalid = true;
            }
        }

        ScreenEvents.afterRender(this).register((screen, drawContext, mouseX, mouseY, tickDelta) -> {
            this.drawComponentTooltip(drawContext, mouseX, mouseY, tickDelta);
        });
    }

    /**
     * Draw the tooltip of this screen's component tree, invoked
     * by {@link ScreenEvents#afterRender(class_437)} so that tooltips are
     * properly rendered above content
     */
    protected void drawComponentTooltip(class_332 drawContext, int mouseX, int mouseY, float tickDelta) {
        if (this.uiAdapter != null) this.uiAdapter.drawTooltip(drawContext, mouseX, mouseY, tickDelta);
    }

    /**
     * A convenience shorthand for querying a component from the adapter's
     * root component via {@link ParentUIComponent#childById(Class, String)}
     */
    protected <C extends UIComponent> C component(Class<C> expectedClass, String id) {
        return this.uiAdapter.rootComponent.childById(expectedClass, id);
    }

    @Override
    public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {}

    @Override
    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        if (!this.invalid) {
            super.method_25394(context, mouseX, mouseY, delta);
        } else {
            this.method_25419();
        }
    }

    @Override
    public boolean method_25404(class_11908 input) {
        if (this.uiAdapter == null) return false;

        if (!input.method_74240()
                && this.uiAdapter.rootComponent.focusHandler().focused() instanceof GreedyInputUIComponent inputComponent
                && inputComponent.onKeyPress(input)) {
            return true;
        }

        if (super.method_25404(input)) {
            return true;
        }

        if (input.method_74231() && this.method_25422()) {
            this.method_25419();
            return true;
        }

        return false;
    }

    @Override
    public boolean method_25403(class_11909 click, double deltaX, double deltaY) {
        if (this.uiAdapter == null) return false;

        return this.uiAdapter.method_25403(click, deltaX, deltaY);
    }

    @Nullable
    @Override
    public class_364 method_25399() {
        return this.uiAdapter;
    }

    @Override
    public void method_25432() {
        if (this.uiAdapter != null) {
            this.uiAdapter.cursorAdapter.applyStyle(CursorStyle.NONE);
        }
    }

    @Override
    public void dispose() {
        if (this.uiAdapter != null) this.uiAdapter.dispose();
    }
}
