package io.wispforest.owo.braid.widgets.label;

import io.wispforest.owo.braid.core.BraidGraphics;
import io.wispforest.owo.braid.core.Constraints;
import io.wispforest.owo.braid.core.KeyModifiers;
import io.wispforest.owo.braid.core.Size;
import io.wispforest.owo.braid.core.cursor.CursorStyle;
import io.wispforest.owo.braid.framework.instance.LeafWidgetInstance;
import io.wispforest.owo.braid.framework.instance.MouseListener;
import io.wispforest.owo.braid.framework.instance.TooltipProvider;
import io.wispforest.owo.braid.framework.widget.LeafInstanceWidget;
import io.wispforest.owo.mixin.braid.ClickableStyleFinderAccessor;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.function.Function;
import net.minecraft.class_12225;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_5348;
import net.minecraft.class_5481;
import net.minecraft.class_5684;

public class RawLabel extends LeafInstanceWidget {

    public final LabelStyle style;
    public final boolean softWrap;
    public final boolean ellipsize;
    public final class_2561 text;

    public RawLabel(LabelStyle style, boolean softWrap, boolean ellipsize, class_2561 text) {
        this.style = style;
        this.softWrap = softWrap;
        this.ellipsize = ellipsize;
        this.text = text;
    }

    @Override
    public LeafWidgetInstance<?> instantiate() {
        return new Instance(this);
    }

    public static class Instance extends LeafWidgetInstance<RawLabel> implements TooltipProvider, MouseListener {

        private List<class_5481> renderText = List.of();
        private DoubleList renderTextWidths = new DoubleArrayList();
        private int renderTextHeight = 0;

        protected Function<class_2583, Boolean> textClickHandler = style -> {
            return style != null && OwoUIGraphics.utilityScreen().handleTextClick(style, class_310.method_1551().field_1755);
        };

        public Instance(RawLabel widget) {
            super(widget);
        }

        @Override
        public void setWidget(RawLabel widget) {
            if (Objects.equals(this.widget.style, widget.style)
                && this.widget.softWrap == widget.softWrap
                && this.widget.ellipsize == widget.ellipsize
                && Objects.equals(this.widget.text, widget.text)) {
                return;
            }

            super.setWidget(widget);
            this.markNeedsLayout();
        }

        protected List<class_5481> wrapText(class_327 font, int maxWidth, double maxHeight) {
            var styledText = this.widget.text.method_27661().method_27694(textStyle -> textStyle.method_27702(this.widget.style.textStyle()));
            var wrappedLines = font.method_27527().method_27495(styledText, this.widget.softWrap ? maxWidth : Integer.MAX_VALUE, class_2583.field_24360);

            var maxLines = (int) Math.floor(maxHeight / font.field_2000);
            if (this.widget.ellipsize && !wrappedLines.isEmpty() && maxLines > 0 && (wrappedLines.size() > maxLines || font.method_27525(wrappedLines.getLast()) > maxWidth)) {
                wrappedLines = wrappedLines.subList(0, maxLines);

                var ellipsis = class_5348.method_29430("…");
                var ellipsisLength = font.method_27525(ellipsis);

                var trimmedLastLine = font.method_1714(wrappedLines.getLast(), maxWidth - ellipsisLength);
                wrappedLines.set(
                    wrappedLines.size() - 1,
                    class_5348.method_29433(trimmedLastLine, ellipsis)
                );
            }

            return class_2477.method_10517().method_30933(wrappedLines);
        }

        protected TextMetrics measureText(class_327 font, List<class_5481> lines) {
            var textWidth = 0;
            var textHeight = 0;
            var lineWidths = new DoubleArrayList();

            for (var line : lines) {
                var lineWidth = font.method_30880(line);
                lineWidths.add(lineWidth);

                textWidth = Math.max(textWidth, lineWidth);
                textHeight += font.field_2000;
            }

            return new TextMetrics(textWidth, textHeight, lineWidths);
        }

        @Override
        protected void doLayout(Constraints constraints) {
            var font = this.host().client().field_1772;
            this.renderText = this.wrapText(font, (int) constraints.maxWidth(), (int) constraints.maxHeight());

            var metrics = this.measureText(font, this.renderText);

            this.renderTextWidths = metrics.lineWidths();
            this.renderTextHeight = metrics.height();

            var size = Size.of(metrics.width, metrics.height).constrained(constraints);
            this.transform.setSize(size);
        }

        @Override
        protected double measureIntrinsicWidth(double height) {
            var renderer = this.host().client().field_1772;
            return this.measureText(renderer, this.wrapText(renderer, Integer.MAX_VALUE, (int) height)).width;
        }

        @Override
        protected double measureIntrinsicHeight(double width) {
            var renderer = this.host().client().field_1772;
            return this.measureText(renderer, this.wrapText(renderer, this.widget.softWrap ? (int) width : Integer.MAX_VALUE, Integer.MAX_VALUE)).height;
        }

        @Override
        protected OptionalDouble measureBaselineOffset() {
            return OptionalDouble.of(this.host().client().field_1772.field_2000 - 2);
        }

        @Override
        public void draw(BraidGraphics graphics) {
            var font = this.host().client().field_1772;
            var yOffset = this.widget.style.textAlignment().alignVertical(this.transform.height(), this.renderTextHeight);

            for (int lineIdx = 0; lineIdx < this.renderText.size(); lineIdx++) {
                graphics.method_51430(
                    font,
                    this.renderText.get(lineIdx),
                    (int) this.widget.style.textAlignment().alignHorizontal(this.transform.width(), this.renderTextWidths.getDouble(lineIdx)),
                    (int) yOffset + lineIdx * font.field_2000,
                    this.widget.style.baseColor().argb(),
                    this.widget.style.shadow()
                );
            }
        }

        // this reimplementation of RawLabel.draw is pretty cringe, however
        // mojang has left our hands tied since the text collector interface
        // does not give us control over text color and shadow
        public void collectText(class_12225 collector) {
            var font = this.host().client().field_1772;
            var yOffset = this.widget.style.textAlignment().alignVertical(this.transform.height(), this.renderTextHeight);

            for (int lineIdx = 0; lineIdx < this.renderText.size(); lineIdx++) {
                collector.method_75762(
                    (int) this.widget.style.textAlignment().alignHorizontal(this.transform.width(), this.renderTextWidths.getDouble(lineIdx)),
                    (int) yOffset + lineIdx * font.field_2000,
                    this.renderText.get(lineIdx)
                );
            }
        }

        @Override
        @Nullable
        public List<class_5684> getTooltipComponentsAt(double x, double y) {
            return null;
        }

        @Override
        @Nullable
        public class_2583 getStyleAt(double x, double y) {
            if (this.renderText.isEmpty()) return null;

            var collector = new StyleCollector(this.host().client().field_1772, (int) x, (int) y);
            this.collectText(collector);

            return collector.method_75777();
        }

        @Override
        public boolean onMouseDown(double x, double y, int button, KeyModifiers modifiers) {
            if (button != 0) return MouseListener.super.onMouseDown(x, y, button, modifiers);
            return this.textClickHandler.apply(this.getStyleAt(x, y));
        }

        @Override
        public @Nullable CursorStyle cursorStyleAt(double x, double y) {
            var style = this.getStyleAt(x, y);
            if (style == null) return null;
            if (style.method_10970() != null) return CursorStyle.HAND;
            return null;
        }

        public static class StyleCollector extends class_12225.class_12226 {

            public StyleCollector(class_327 font, int clickX, int clickY) {
                super(font, clickX, clickY);
                ((ClickableStyleFinderAccessor) this).owo$setStyleScanner(((ClickableStyleFinderAccessor) this)::owo$setResult);
            }
        }
    }

    public record TextMetrics(int width, int height, DoubleList lineWidths) {}
}
