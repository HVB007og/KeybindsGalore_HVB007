package io.wispforest.owo.ui.core;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.wispforest.owo.mixin.ui.access.GuiGraphicsAccessor;
import io.wispforest.owo.ui.event.WindowResizeCallback;
import io.wispforest.owo.ui.renderstate.CircleElementRenderState;
import io.wispforest.owo.ui.renderstate.GradientQuadElementRenderState;
import io.wispforest.owo.ui.renderstate.LineElementRenderState;
import io.wispforest.owo.ui.renderstate.RingElementRenderState;
import io.wispforest.owo.ui.util.NinePatchTexture;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_10799;
import net.minecraft.class_11246;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_5684;
import net.minecraft.class_8000;
import net.minecraft.class_8001;
import net.minecraft.class_8029;
import net.minecraft.class_8030;

public class OwoUIGraphics extends class_332 {

    public static final class_2960 PANEL_NINE_PATCH_TEXTURE = class_2960.method_60655("owo", "panel/default");
    public static final class_2960 DARK_PANEL_NINE_PATCH_TEXTURE = class_2960.method_60655("owo", "panel/dark");
    public static final class_2960 PANEL_INSET_NINE_PATCH_TEXTURE = class_2960.method_60655("owo", "panel/inset");

    private final Consumer<Runnable> setTooltipDrawer;

    protected OwoUIGraphics(class_310 client, class_11246 renderState, int mouseX, int mouseY, Consumer<Runnable> setTooltipDrawer) {
        super(client, renderState, mouseX, mouseY);
        this.setTooltipDrawer = setTooltipDrawer;
    }

    public static OwoUIGraphics of(class_332 graphics) {
        var owoContext = new OwoUIGraphics(
            class_310.method_1551(),
            graphics.field_59826,
            ((GuiGraphicsAccessor) graphics).owo$getMouseY(),
            ((GuiGraphicsAccessor) graphics).owo$getMouseX(),
            ((GuiGraphicsAccessor) graphics)::owo$setDeferredTooltip
        );

        ((GuiGraphicsAccessor) owoContext).owo$setScissorStack(((GuiGraphicsAccessor) graphics).owo$getScissorStack());
        ((GuiGraphicsAccessor) owoContext).owo$setPose(((GuiGraphicsAccessor) graphics).owo$getPose());

        return owoContext;
    }

    public static UtilityScreen utilityScreen() {
        return UtilityScreen.get();
    }

    public boolean intersectsScissor(PositionedRectangle other) {
        other = other.transform(getMatrixStack());

        var rect = this.field_44659.method_70863();

        if (rect == null) return true;

        var pos = rect.comp_1195();

        return other.x() < pos.comp_1193() + rect.comp_1196()
            && other.x() + other.width() >= pos.comp_1193()
            && other.y() < pos.comp_1194() + rect.comp_1197()
            && other.y() + other.height() >= pos.comp_1194();
    }

    public void drawRectOutline(int x, int y, int width, int height, int color) {
        drawRectOutline(class_10799.field_56879, x, y, width, height, color);
    }

    /**
     * Draw the outline of a rectangle
     *
     * @param x      The x-coordinate of top-left corner of the rectangle
     * @param y      The y-coordinate of top-left corner of the rectangle
     * @param width  The width of the rectangle
     * @param height The height of the rectangle
     * @param color  The color of the rectangle
     */
    public void drawRectOutline(RenderPipeline pipeline, int x, int y, int width, int height, int color) {
        this.method_48196(pipeline, x, y, x + width, y + 1, color);
        this.method_48196(pipeline, x, y + height - 1, x + width, y + height, color);

        this.method_48196(pipeline, x, y + 1, x + 1, y + height - 1, color);
        this.method_48196(pipeline, x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    public void drawGradientRect(int x, int y, int width, int height, int topLeftColor, int topRightColor, int bottomRightColor, int bottomLeftColor) {
        this.drawGradientRect(class_10799.field_56879, x, y, width, height, topLeftColor, topRightColor, bottomRightColor, bottomLeftColor);
    }

    /**
     * Draw a filled rectangle with a gradient
     *
     * @param x                The x-coordinate of top-left corner of the rectangle
     * @param y                The y-coordinate of top-left corner of the rectangle
     * @param width            The width of the rectangle
     * @param height           The height of the rectangle
     * @param topLeftColor     The color at the rectangle's top left corner
     * @param topRightColor    The color at the rectangle's top right corner
     * @param bottomRightColor The color at the rectangle's bottom right corner
     * @param bottomLeftColor  The color at the rectangle's bottom left corner
     */
    public void drawGradientRect(RenderPipeline pipeline, int x, int y, int width, int height, int topLeftColor, int topRightColor, int bottomRightColor, int bottomLeftColor) {
        this.field_59826.method_70919(new GradientQuadElementRenderState(
            pipeline,
            new Matrix3x2f(this.method_51448()),
            new class_8030(new class_8029(x, y), width, height),
            this.field_44659.method_70863(),
            Color.ofArgb(topLeftColor),
            Color.ofArgb(topRightColor),
            Color.ofArgb(bottomLeftColor),
            Color.ofArgb(bottomRightColor)
        ));
    }

    /**
     * Draw a panel that looks like the background of a vanilla
     * inventory screen
     *
     * @param x      The x-coordinate of top-left corner of the panel
     * @param y      The y-coordinate of top-left corner of the panel
     * @param width  The width of the panel
     * @param height The height of the panel
     * @param dark   Whether to use the dark version of the panel texture
     */
    public void drawPanel(int x, int y, int width, int height, boolean dark) {
        NinePatchTexture.draw(dark ? DARK_PANEL_NINE_PATCH_TEXTURE : PANEL_NINE_PATCH_TEXTURE, this, x, y, width, height);
    }

    public void drawSpectrum(int x, int y, int width, int height, boolean vertical) {
        this.field_59826.method_70919(new GradientQuadElementRenderState(
            OwoUIPipelines.GUI_HSV,
            new Matrix3x2f(this.method_51448()),
            new class_8030(new class_8029(x, y), width, height),
            this.field_44659.method_70863(),
            Color.WHITE,
            new Color(vertical ? 1f : 0f, 1f, 1f),
            new Color(vertical ? 0f : 1f, 1f, 1f),
            new Color(0f, 1f, 1f)
        ));
    }

    public void drawText(class_2561 text, float x, float y, float scale, int color) {
        drawText(text, x, y, scale, color, TextAnchor.TOP_LEFT);
    }

    public void drawText(class_2561 text, float x, float y, float scale, int color, TextAnchor anchorPoint) {
        final var textRenderer = class_310.method_1551().field_1772;

        this.method_51448().pushMatrix();
        this.method_51448().scale(scale, scale);

        switch (anchorPoint) {
            case TOP_RIGHT -> x -= textRenderer.method_27525(text) * scale;
            case BOTTOM_LEFT -> y -= textRenderer.field_2000 * scale;
            case BOTTOM_RIGHT -> {
                x -= textRenderer.method_27525(text) * scale;
                y -= textRenderer.field_2000 * scale;
            }
        }


        this.method_51439(textRenderer, text, (int) (x * (1 / scale)), (int) (y * (1 / scale)), color, false);
        this.method_51448().popMatrix();
    }

    public enum TextAnchor {
        TOP_RIGHT, BOTTOM_RIGHT, TOP_LEFT, BOTTOM_LEFT
    }

    public void drawLine(int x1, int y1, int x2, int y2, double thiccness, Color color) {
        drawLine(class_10799.field_56879, x1, y1, x2, y2, thiccness, color);
    }

    public void drawLine(RenderPipeline pipeline, int x1, int y1, int x2, int y2, double thiccness, Color color) {
        this.field_59826.method_70919(new LineElementRenderState(
            pipeline,
            new Matrix3x2f(this.method_51448()),
            this.field_44659.method_70863(),
            x1, y1, x2, y2,
            thiccness,
            color
        ));
    }

    public void drawCircle(int centerX, int centerY, int segments, double radius, Color color) {
        drawCircle(OwoUIPipelines.GUI_TRIANGLE_FAN, centerX, centerY, segments, radius, color);
    }

    public void drawCircle(int centerX, int centerY, double angleFrom, double angleTo, int segments, double radius, Color color) {
        drawCircle(OwoUIPipelines.GUI_TRIANGLE_FAN, centerX, centerY, angleFrom, angleTo, segments, radius, color);
    }

    public void drawCircle(RenderPipeline pipeline, int centerX, int centerY, int segments, double radius, Color color) {
        drawCircle(pipeline, centerX, centerY, 0, 360, segments, radius, color);
    }

    public void drawCircle(RenderPipeline pipeline, int centerX, int centerY, double angleFrom, double angleTo, int segments, double radius, Color color) {
        Preconditions.checkArgument(angleFrom < angleTo, "angleFrom must be less than angleTo");

        this.field_59826.method_70919(new CircleElementRenderState(
            pipeline,
            new Matrix3x2f(this.method_51448()),
            this.field_44659.method_70863(),
            centerX, centerY, angleFrom, angleTo, segments, radius, color
        ));
    }

    public void drawRing(int centerX, int centerY, int segments, double innerRadius, double outerRadius, Color innerColor, Color outerColor) {
        drawRing(OwoUIPipelines.GUI_TRIANGLE_STRIP, centerX, centerY, segments, innerRadius, outerRadius, innerColor, outerColor);
    }

    public void drawRing(int centerX, int centerY, double angleFrom, double angleTo, int segments, double innerRadius, double outerRadius, Color innerColor, Color outerColor) {
        drawRing(OwoUIPipelines.GUI_TRIANGLE_STRIP, centerX, centerY, angleFrom, angleTo, segments, innerRadius, outerRadius, innerColor, outerColor);
    }

    public void drawRing(RenderPipeline pipeline, int centerX, int centerY, int segments, double innerRadius, double outerRadius, Color innerColor, Color outerColor) {
        drawRing(pipeline, centerX, centerY, 0d, 360d, segments, innerRadius, outerRadius, innerColor, outerColor);
    }

    public void drawRing(RenderPipeline pipeline, int centerX, int centerY, double angleFrom, double angleTo, int segments, double innerRadius, double outerRadius, Color innerColor, Color outerColor) {
        Preconditions.checkArgument(angleFrom < angleTo, "angleFrom must be less than angleTo");
        Preconditions.checkArgument(innerRadius < outerRadius, "innerRadius must be less than outerRadius");

        this.field_59826.method_70919(new RingElementRenderState(
            pipeline,
            new Matrix3x2f(this.method_51448()),
            this.field_44659.method_70863(),
            centerX, centerY, angleFrom, angleTo, segments, innerRadius, outerRadius, innerColor, outerColor
        ));
    }

    public void drawTooltip(class_327 textRenderer, int x, int y, List<class_5684> components) {
        drawTooltip(textRenderer, x, y, components, null);
    }

    public void drawTooltip(class_327 textRenderer, int x, int y, List<class_5684> components, @Nullable class_2960 texture) {
        ((GuiGraphicsAccessor) this).owo$drawTooltipImmediately(textRenderer, components, x, y, class_8001.field_41687, texture);
    }

    @Override
    protected void method_71273(class_327 textRenderer, List<class_5684> components, int x, int y, class_8000 positioner, @Nullable class_2960 texture, boolean focused) {
        super.method_71273(textRenderer, components, x, y, positioner, texture, focused);
        this.setTooltipDrawer.accept(((GuiGraphicsAccessor) this).owo$getDeferredTooltip());
    }

    // --- debug rendering ---

    public static void drawInsets(OwoUIGraphics self, int x, int y, int width, int height, Insets insets, int color) {
        drawInsets(self, class_10799.field_56879, x, y, width, height, insets, color);
    }

    /**
     * Draw the area around the given rectangle which
     * the given insets describe
     *
     * @param x      The x-coordinate of top-left corner of the rectangle
     * @param y      The y-coordinate of top-left corner of the rectangle
     * @param width  The width of the rectangle
     * @param height The height of the rectangle
     * @param insets The insets to draw around the rectangle
     * @param color  The color to draw the inset area with
     */
    public static void drawInsets(OwoUIGraphics self, RenderPipeline pipeline, int x, int y, int width, int height, Insets insets, int color) {
        self.method_48196(pipeline, x - insets.left(), y - insets.top(), x + width + insets.right(), y, color);
        self.method_48196(pipeline, x - insets.left(), y + height, x + width + insets.right(), y + height + insets.bottom(), color);

        self.method_48196(pipeline, x - insets.left(), y, x, y + height, color);
        self.method_48196(pipeline, x + width, y, x + width + insets.right(), y + height, color);
    }

    /**
     * Draw the element inspector for the given tree, detailing the position,
     * bounding box, margins and padding of each component
     *
     * @param root        The root component of the hierarchy to draw
     * @param mouseX      The x-coordinate of the mouse pointer
     * @param mouseY      The y-coordinate of the mouse pointer
     * @param onlyHovered Whether to only draw the inspector for the hovered widget
     */
    public static void drawInspector(OwoUIGraphics self, ParentUIComponent root, double mouseX, double mouseY, boolean onlyHovered) {
        var client = class_310.method_1551();
        var textRenderer = client.field_1772;

        var children = new ArrayList<UIComponent>();
        if (!onlyHovered) {
            root.collectDescendants(children);
        } else if (root.childAt((int) mouseX, (int) mouseY) != null) {
            children.add(root.childAt((int) mouseX, (int) mouseY));
        }

        var pipeline = class_10799.field_56879;

        for (var child : children) {
            if (child instanceof ParentUIComponent parentComponent) {
                drawInsets(self, pipeline, parentComponent.x(), parentComponent.y(), parentComponent.width(),
                    parentComponent.height(), parentComponent.padding().get().inverted(), 0xA70CECDD);
            }

            final var margins = child.margins().get();
            drawInsets(self, pipeline, child.x(), child.y(), child.width(), child.height(), margins, 0xA7FFF338);
            self.drawRectOutline(pipeline, child.x(), child.y(), child.width(), child.height(), 0xFF3AB0FF);

            if (onlyHovered) {

                int inspectorX = child.x() + 1;
                int inspectorY = child.y() + child.height() + child.margins().get().bottom() + 1;

                final var message = class_2561.method_43470(child.getClass().getSimpleName())
                    .method_27693(child.id() == null ? "\n" : " '" + child.id() + "'\n")
                    .method_10852(child.inspectorDescriptor());
                final var wrappedMessage = textRenderer.method_1728(message, client.method_22683().method_4486() + 4);
                int inspectorWidth = wrappedMessage.stream().mapToInt(textRenderer::method_30880).max().orElse(30);
                int inspectorHeight = textRenderer.field_2000 * wrappedMessage.size() + 4;

                if (inspectorY > client.method_22683().method_4502() - inspectorHeight) {
                    inspectorY -= child.fullSize().height() + inspectorHeight + 1;
                    if (child instanceof ParentUIComponent parentComponent) {
                        inspectorX += parentComponent.padding().get().left();
                        inspectorY += parentComponent.padding().get().top();
                    }
                }
                if (inspectorY < 0) inspectorY = 1;

                if (inspectorX > client.method_22683().method_4486() - inspectorWidth) {
                    inspectorX = client.method_22683().method_4486() - inspectorWidth - 2;
                }
                if (inspectorX < 0) inspectorX = 1;

                self.method_48196(pipeline, inspectorX, inspectorY, inspectorX + inspectorWidth + 3, inspectorY + inspectorHeight, 0xA7000000);
                self.drawRectOutline(pipeline, inspectorX, inspectorY, inspectorWidth + 3, inspectorHeight, 0xA7000000);

                self.method_51440(textRenderer, message, inspectorX + 2, inspectorY + 2, inspectorWidth, 0xFFFFFFFF, false);
            }
        }
    }

    public static class UtilityScreen extends class_437 {

        private static UtilityScreen INSTANCE;

        private UtilityScreen() {
            super(class_2561.method_43473());
        }

        public static UtilityScreen get() {
            if (INSTANCE == null) {
                INSTANCE = new UtilityScreen();

                final var client = class_310.method_1551();
                INSTANCE.method_25423(
                    client.method_22683().method_4486(),
                    client.method_22683().method_4502()
                );
            }

            return INSTANCE;
        }

        public boolean handleTextClick(class_2583 style, class_437 screenAfterRun) {
            if (style.method_10970() == null) return false;
            method_71999(style.method_10970(), this.field_22787, screenAfterRun);

            return true;
        }

        static {
            WindowResizeCallback.EVENT.register((client, window) -> {
                if (INSTANCE == null) return;
                INSTANCE.method_25423(window.method_4486(), window.method_4502());
            });
        }
    }
}
