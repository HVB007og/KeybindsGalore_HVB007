package io.wispforest.owo.ui.core;

import io.wispforest.owo.Owo;
import io.wispforest.owo.renderdoc.RenderDoc;
import io.wispforest.owo.ui.util.CursorAdapter;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiFunction;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_4068;
import net.minecraft.class_437;
import net.minecraft.class_6379;
import net.minecraft.class_6382;

/**
 * A UI adapter constitutes the main entrypoint to using owo-ui.
 * It takes care of rendering the UI tree correctly, handles input events
 * and cursor styling as well as the component inspector.
 * <p>
 * Additionally, the adapter implements all interfaces required for it
 * to be treated as a normal widget by the vanilla screen system - this means
 * even if you choose to not use {@link io.wispforest.owo.ui.base.BaseOwoScreen}
 * you can always simply add it as a widget and get most of the functionality
 * working out of the box
 * <p>
 * To draw the UI tree managed by this adapter, call {@link OwoUIAdapter#method_25394(class_332, int, int, float)}.
 * Note that this does not draw the current tooltip of the UI - this must be done separately
 * by invoking {@link #drawTooltip(class_332, int, int, float)}. If in a scenario with multiple adapters
 * or other sources rendering UI elements to the screen, it is generally desirable to delay tooltip
 * drawing until after all UI is drawn to avoid layering issues.
 *
 * @see io.wispforest.owo.ui.base.BaseOwoScreen
 */
public class OwoUIAdapter<R extends ParentUIComponent> implements class_364, class_4068, class_6379 {

    private static boolean isRendering = false;

    public final R rootComponent;
    public final CursorAdapter cursorAdapter;

    protected boolean disposed = false;
    protected boolean captureFrame = false;

    protected int x, y;
    protected int width, height;

    public boolean enableInspector = false;
    public boolean globalInspector = false;
    public int inspectorZOffset = 1000;

    protected OwoUIAdapter(int x, int y, int width, int height, R rootComponent) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.cursorAdapter = CursorAdapter.ofClientWindow();
        this.rootComponent = rootComponent;
    }

    /**
     * Create a UI adapter for the given screen. This also sets it up
     * to be rendered and receive input events, without needing you to
     * do any more setup
     *
     * @param screen             The screen for which to create an adapter
     * @param rootComponentMaker A function which will create the root component of this screen
     * @param <R>                The type of root component the created adapter will use
     * @return The new UI adapter, already set up for the given screen
     */
    public static <R extends ParentUIComponent> OwoUIAdapter<R> create(class_437 screen, BiFunction<Sizing, Sizing, R> rootComponentMaker) {
        var rootComponent = rootComponentMaker.apply(Sizing.fill(100), Sizing.fill(100));

        var adapter = new OwoUIAdapter<>(0, 0, screen.field_22789, screen.field_22790, rootComponent);
        screen.method_37063(adapter);
        screen.method_25395(adapter);

        return adapter;
    }

    /**
     * Create a new UI adapter without the specific context of a screen - use this
     * method when you want to embed owo-ui into a different context
     *
     * @param x                  The x-coordinate of the top-left corner of the root component
     * @param y                  The y-coordinate of the top-left corner of the root component
     * @param width              The width of the available area, in pixels
     * @param height             The height of the available area, in pixels
     * @param rootComponentMaker A function which will create the root component of the adapter
     * @param <R>                The type of root component the created adapter will use
     * @return The new UI adapter, ready for layout inflation
     */
    public static <R extends ParentUIComponent> OwoUIAdapter<R> createWithoutScreen(int x, int y, int width, int height, BiFunction<Sizing, Sizing, R> rootComponentMaker) {
        var rootComponent = rootComponentMaker.apply(Sizing.fill(100), Sizing.fill(100));
        return new OwoUIAdapter<>(x, y, width, height, rootComponent);
    }

    /**
     * Begin the layout process of the UI tree and
     * mount the tree once the layout is inflated
     * <p>
     * After this method has executed, this adapter is ready for rendering
     */
    public void inflateAndMount() {
        this.rootComponent.inflate(Size.of(this.width, this.height));
        this.rootComponent.mount(null, this.x, this.y);
    }

    public void moveAndResize(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.inflateAndMount();
    }

    /**
     * Dispose this UI adapter - this will destroy the cursor
     * objects held onto by this adapter and stop updating the cursor style
     * <p>
     * After this method has executed, this adapter can safely be garbage-collected
     */
    // TODO properly dispose root component
    public void dispose() {
        this.cursorAdapter.dispose();
        this.disposed = true;
    }

    /**
     * @return Toggle rendering of the inspector
     */
    public boolean toggleInspector() {
        return this.enableInspector = !this.enableInspector;
    }

    /**
     * @return Toggle the inspector between
     * hovered and global mode
     */
    public boolean toggleGlobalInspector() {
        return this.globalInspector = !this.globalInspector;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    @Override
    public void method_25394(class_332 graphics, int mouseX, int mouseY, float partialTicks) {
        if (!(graphics instanceof OwoUIGraphics)) graphics = OwoUIGraphics.of(graphics);
        var owoGraphics = (OwoUIGraphics) graphics;

        try {
            isRendering = true;

            if (this.captureFrame) RenderDoc.startFrameCapture();

            final var delta = class_310.method_1551().method_61966().method_60636();
            final var window = class_310.method_1551().method_22683();

            this.rootComponent.update(delta, mouseX, mouseY);

            graphics.method_44379(0, 0, window.method_4489(), window.method_4506());
            this.rootComponent.draw(owoGraphics, mouseX, mouseY, partialTicks, delta);
            graphics.method_44380();

            final var hovered = this.rootComponent.childAt(mouseX, mouseY);
            if (!disposed && hovered != null) {
                this.cursorAdapter.applyStyle(hovered.cursorStyle());
            }

            if (this.enableInspector) {
                OwoUIGraphics.drawInspector(owoGraphics, this.rootComponent, mouseX, mouseY, !this.globalInspector);
            }

            if (this.captureFrame) RenderDoc.endFrameCapture();
        } finally {
            isRendering = false;
            this.captureFrame = false;
        }
    }

    /**
     * Draw the current tooltip of the UI managed by this adapter. This method
     * must not be called without a previous, corresponding call to {@link #method_25394(class_332, int, int, float)}
     *
     * @since 0.12.19
     */
    public void drawTooltip(class_332 graphics, int mouseX, int mouseY, float partialTicks) {
        if (!(graphics instanceof OwoUIGraphics)) graphics = OwoUIGraphics.of(graphics);
        var owoContext = (OwoUIGraphics) graphics;

        final var delta = class_310.method_1551().method_61966().method_60636();

        this.rootComponent.drawTooltip(owoContext, mouseX, mouseY, partialTicks, delta);
        graphics.method_73199();
    }

    @Override
    public boolean method_25405(double mouseX, double mouseY) {
        return this.rootComponent.isInBoundingBox(mouseX, mouseY);
    }

    @Override
    public void method_25365(boolean focused) {}

    @Override
    public boolean method_25370() {
        return true;
    }

    @Override
    public boolean method_25402(class_11909 click, boolean doubled) {
        return this.rootComponent.onMouseDown(click, doubled);
    }

    @Override
    public boolean method_25406(class_11909 click) {
        return this.rootComponent.onMouseUp(click);
    }

    @Override
    public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return this.rootComponent.onMouseScroll(mouseX, mouseY, verticalAmount);
    }

    @Override
    public boolean method_25403(class_11909 click, double deltaX, double deltaY) {
        return this.rootComponent.onMouseDrag(click, deltaX, deltaY);
    }

    @Override
    public boolean method_25404(class_11908 input) {
        if (Owo.DEBUG && input.comp_4795() == GLFW.GLFW_KEY_LEFT_SHIFT) {
            if (input.method_74240()) {
                this.toggleInspector();
            } else if (input.method_74238()) {
                this.toggleGlobalInspector();
            }
        }

        if (Owo.DEBUG && input.comp_4795() == GLFW.GLFW_KEY_R && RenderDoc.isAvailable()) {
            if (input.method_74238() && input.method_74240()) {
                this.captureFrame = true;
            }
        }

        return this.rootComponent.onKeyPress(input);
    }

    @Override
    public boolean method_25400(class_11905 input) {
        return this.rootComponent.onCharTyped(input);
    }

    @Override
    public class_6380 method_37018() {
        return class_6380.field_33784;
    }

    @Override
    public void method_37020(class_6382 builder) {}

    public static boolean isRendering() {
        return isRendering;
    }
}
