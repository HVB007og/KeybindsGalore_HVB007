package io.wispforest.owo.ui.component;

import io.wispforest.owo.braid.widgets.label.RawLabel;
import io.wispforest.owo.ui.base.BaseUIComponent;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.util.Observable;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_5481;

public class LabelComponent extends BaseUIComponent {

    protected final class_327 textRenderer = class_310.method_1551().field_1772;

    protected class_2561 text;
    protected List<class_5481> wrappedText;

    protected VerticalAlignment verticalTextAlignment = VerticalAlignment.TOP;
    protected HorizontalAlignment horizontalTextAlignment = HorizontalAlignment.LEFT;

    protected final AnimatableProperty<Color> color = AnimatableProperty.of(Color.WHITE);
    protected final Observable<Integer> lineHeight = Observable.of(this.textRenderer.field_2000);
    protected final Observable<Integer> lineSpacing = Observable.of(2);
    protected boolean shadow;
    protected int maxWidth;

    protected Function<@Nullable class_2583, Boolean> textClickHandler = style -> {
        return style != null && OwoUIGraphics.utilityScreen().handleTextClick(style, class_310.method_1551().field_1755);
    };

    protected LabelComponent(class_2561 text) {
        this.text = text;
        this.wrappedText = new ArrayList<>();

        this.shadow = false;
        this.maxWidth = Integer.MAX_VALUE;

        Observable.observeAll(this::notifyParentIfMounted, this.lineHeight, this.lineSpacing);
    }

    public LabelComponent text(class_2561 text) {
        this.text = text;
        this.notifyParentIfMounted();
        return this;
    }

    public class_2561 text() {
        return this.text;
    }

    public LabelComponent maxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        this.notifyParentIfMounted();
        return this;
    }

    public int maxWidth() {
        return this.maxWidth;
    }

    public LabelComponent shadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public boolean shadow() {
        return this.shadow;
    }

    public LabelComponent color(Color color) {
        this.color.set(color);
        return this;
    }

    public AnimatableProperty<Color> color() {
        return this.color;
    }

    public LabelComponent verticalTextAlignment(VerticalAlignment verticalAlignment) {
        this.verticalTextAlignment = verticalAlignment;
        return this;
    }

    public VerticalAlignment verticalTextAlignment() {
        return this.verticalTextAlignment;
    }

    public LabelComponent horizontalTextAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalTextAlignment = horizontalAlignment;
        return this;
    }

    public HorizontalAlignment horizontalTextAlignment() {
        return this.horizontalTextAlignment;
    }

    public LabelComponent lineHeight(int lineHeight) {
        this.lineHeight.set(lineHeight);
        return this;
    }

    public int lineHeight() {
        return this.lineHeight.get();
    }

    public LabelComponent lineSpacing(int lineSpacing) {
        this.lineSpacing.set(lineSpacing);
        return this;
    }

    public int lineSpacing() {
        return this.lineSpacing.get();
    }

    public LabelComponent textClickHandler(Function<@Nullable class_2583, Boolean> textClickHandler) {
        this.textClickHandler = textClickHandler;
        return this;
    }

    public Function<class_2583, Boolean> textClickHandler() {
        return textClickHandler;
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        int widestText = 0;
        for (var line : this.wrappedText) {
            int width = this.textRenderer.method_30880(line);
            if (width > widestText) widestText = width;
        }

        if (widestText > this.maxWidth) {
            this.wrapLines();
            return this.determineHorizontalContentSize(sizing);
        } else {
            return widestText;
        }
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        this.wrapLines();
        return this.textHeight();
    }

    @Override
    public void inflate(Size space) {
        this.wrapLines();
        super.inflate(space);
    }

    private void wrapLines() {
        this.wrappedText = this.textRenderer.method_1728(this.text, this.horizontalSizing.get().isContent() ? this.maxWidth : this.width);
    }

    protected int textHeight() {
        return (this.wrappedText.size() * (this.lineHeight() + this.lineSpacing())) - this.lineSpacing();
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        this.color.update(delta);
    }

    @Override
    public void draw(OwoUIGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        graphics
            .push()
            .translate(0, 1f / class_310.method_1551().method_22683().method_4495());

        this.drawText((renderX, renderY, text, shadow, color) -> {
            graphics.method_51430(
                class_310.method_1551().field_1772,
                text,
                renderX,
                renderY,
                color.argb(),
                shadow
            );
        });

        graphics.pop();
    }

    protected void drawText(LabelDrawFunction goodFunction) {
        int x = this.x;
        int y = this.y;

        if (this.horizontalSizing.get().isContent()) {
            x += this.horizontalSizing.get().value;
        }
        if (this.verticalSizing.get().isContent()) {
            y += this.verticalSizing.get().value;
        }

        switch (this.verticalTextAlignment) {
            case CENTER -> y += (this.height - (this.textHeight())) / 2;
            case BOTTOM -> y += this.height - (this.textHeight());
        }

        final int lambdaX = x;
        final int lambdaY = y;

        for (int i = 0; i < this.wrappedText.size(); i++) {
            var renderText = this.wrappedText.get(i);
            int renderX = lambdaX;

            switch (this.horizontalTextAlignment) {
                case CENTER -> renderX += (this.width - this.textRenderer.method_30880(renderText)) / 2;
                case RIGHT -> renderX += this.width - this.textRenderer.method_30880(renderText);
            }

            int renderY = lambdaY + i * (this.lineHeight() + this.lineSpacing());
            renderY += this.lineHeight() - this.textRenderer.field_2000;

            goodFunction.draw(renderX, renderY, renderText, this.shadow, this.color.get());
        }
    }

    @Override
    public void drawTooltip(OwoUIGraphics context, int mouseX, int mouseY, float partialTicks, float delta) {
        super.drawTooltip(context, mouseX, mouseY, partialTicks, delta);
        context.method_51441(this.textRenderer, this.styleAt(mouseX - this.x, mouseY - this.y), mouseX, mouseY);
    }

    @Override
    public boolean shouldDrawTooltip(double mouseX, double mouseY) {
        var hoveredStyle = this.styleAt((int) (mouseX - this.x), (int) (mouseY - this.y));
        return super.shouldDrawTooltip(mouseX, mouseY) || (hoveredStyle != null && hoveredStyle.method_10969() != null && this.isInBoundingBox(mouseX, mouseY));
    }

    @Override
    public boolean onMouseDown(class_11909 click, boolean doubled) {
        return this.textClickHandler.apply(this.styleAt((int) click.comp_4798(), (int) click.comp_4799())) | super.onMouseDown(click, doubled);
    }

    @Nullable
    protected class_2583 styleAt(int mouseX, int mouseY) {
        var clickHandler = new RawLabel.Instance.StyleCollector(this.textRenderer, this.x + mouseX, this.y + mouseY);
        this.drawText((renderX, renderY, text, $, $$) -> clickHandler.method_75762(renderX, renderY, text));

        return clickHandler.method_75777();
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "text", UIParsing::parseText, this::text);
        UIParsing.apply(children, "max-width", UIParsing::parseUnsignedInt, this::maxWidth);
        UIParsing.apply(children, "color", Color::parse, this::color);
        UIParsing.apply(children, "shadow", UIParsing::parseBool, this::shadow);
        UIParsing.apply(children, "line-height", UIParsing::parseUnsignedInt, this::lineHeight);
        UIParsing.apply(children, "line-spacing", UIParsing::parseUnsignedInt, this::lineSpacing);

        UIParsing.apply(children, "vertical-text-alignment", VerticalAlignment::parse, this::verticalTextAlignment);
        UIParsing.apply(children, "horizontal-text-alignment", HorizontalAlignment::parse, this::horizontalTextAlignment);
    }

    @FunctionalInterface
    protected interface LabelDrawFunction {
        void draw(int renderX, int renderY, class_5481 text, boolean shadow, Color color);
    }
}
