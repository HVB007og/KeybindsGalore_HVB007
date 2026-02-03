package io.wispforest.owo.ui.component;

import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.base.BaseUIComponent;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.UIComponent;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.NinePatchTexture;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import io.wispforest.owo.util.Observable;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_5684;

public class SlimSliderComponent extends BaseUIComponent {

    public static final Function<Double, class_2561> VALUE_TOOLTIP_SUPPLIER = value -> class_2561.method_43470(String.valueOf(value));

    protected static final class_2960 TEXTURE = Owo.id("textures/gui/slim_slider.png");
    protected static final class_2960 TRACK_TEXTURE = Owo.id("slim_slider_track");

    protected final EventStream<OnChanged> changedEvents = OnChanged.newStream();
    protected final EventStream<OnSlideEnd> slideEndEvents = OnSlideEnd.newStream();

    protected final Axis axis;
    protected final Observable<Double> value = Observable.of(0d);

    protected double min = 0d, max = 1d;
    protected double stepSize = 0;
    protected @Nullable Function<Double, class_2561> tooltipSupplier = null;

    public SlimSliderComponent(Axis axis) {
        this.cursorStyle(CursorStyle.MOVE);

        this.axis = axis;
        this.value.observe($ -> {
            this.changedEvents.sink().onChanged(this.value());
            this.updateTooltip();
        });
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        if (this.axis == Axis.VERTICAL) {
            return 9;
        } else {
            throw new UnsupportedOperationException("Horizontal SlimSliderComponent cannot be horizontally content-sized");
        }
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        if (this.axis == Axis.HORIZONTAL) {
            return 9;
        } else {
            throw new UnsupportedOperationException("Vertical SlimSliderComponent cannot be vertically content-sized");
        }
    }

    @Override
    public void draw(OwoUIGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        if (this.axis == Axis.HORIZONTAL) {
            NinePatchTexture.draw(TRACK_TEXTURE, graphics, this.x + 1, this.y + 3, this.width - 2, 3);
            graphics.method_25302(class_10799.field_56883, TEXTURE, (int) (this.x + (this.width - 4) * this.value.get()), this.y + 1, 0, 3, 4, 7, 4, 7, 16, 16);
        } else {
            NinePatchTexture.draw(TRACK_TEXTURE, graphics, this.x + 3, this.y + 1, 3, this.height - 2);
            graphics.method_25302(class_10799.field_56883, TEXTURE, this.x + 1, (int) (this.y + (this.height - 4) * this.value.get()), 4, 3, 7, 4, 7, 4, 16, 16);
        }
    }

    @Override
    public boolean onMouseDown(class_11909 click, boolean doubled) {
        super.onMouseDown(click, doubled);
        this.setValueFromMouse(click.comp_4798(), click.comp_4799());
        return true;
    }

    @Override
    public boolean onMouseDrag(class_11909 click, double deltaX, double deltaY) {
        super.onMouseDrag(click, deltaX, deltaY);
        this.setValueFromMouse(click.comp_4798(), click.comp_4799());
        return true;
    }

    @Override
    public boolean onMouseUp(class_11909 click) {
        super.onMouseUp(click);
        this.slideEndEvents.sink().onSlideEnd();
        return true;
    }

    protected void setValueFromMouse(double mouseX, double mouseY) {
        this.value(this.axis == Axis.VERTICAL
            ? this.min + (mouseY / this.height) * (this.max - this.min)
            : this.min + (mouseX / this.width) * (this.max - this.min));
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }

    public EventSource<OnChanged> onChanged() {
        return this.changedEvents.source();
    }

    public EventSource<OnSlideEnd> onSlideEnd() {
        return this.slideEndEvents.source();
    }

    public SlimSliderComponent value(double value) {
        value -= this.min;
        if (this.stepSize != 0) {
            value = Math.round(value / this.stepSize) * this.stepSize;
        }

        this.value.set(class_3532.method_15350(value / (this.max - this.min), 0, 1));
        return this;
    }

    public double value() {
        return this.min + this.value.get() * (this.max - this.min);
    }

    public SlimSliderComponent min(double min) {
        this.min = min;
        return this;
    }

    public double min() {
        return min;
    }

    public SlimSliderComponent max(double max) {
        this.max = max;
        return this;
    }

    public double max() {
        return max;
    }

    public SlimSliderComponent stepSize(double stepSize) {
        this.stepSize = stepSize;
        return this;
    }

    public double stepSize() {
        return stepSize;
    }

    public SlimSliderComponent tooltipSupplier(Function<Double, class_2561> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        this.updateTooltip();

        return this;
    }

    public Function<Double, class_2561> tooltipSupplier() {
        return tooltipSupplier;
    }

    protected void updateTooltip() {
        if (this.tooltipSupplier != null) {
            this.tooltip(this.tooltipSupplier.apply(this.value()));
        } else {
            this.tooltip((List<class_5684>) null);
        }
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "step-size", UIParsing::parseDouble, this::stepSize);
        UIParsing.apply(children, "min", UIParsing::parseDouble, this::min);
        UIParsing.apply(children, "max", UIParsing::parseDouble, this::max);
        UIParsing.apply(children, "value", UIParsing::parseDouble, this::value);
    }

    public static UIComponent parse(Element element) {
        return element.getAttribute("direction").equals("vertical")
            ? new SlimSliderComponent(Axis.VERTICAL)
            : new SlimSliderComponent(Axis.HORIZONTAL);
    }

    public static Function<Double, class_2561> valueTooltipSupplier(int decimalPlaces) {
        return value -> class_2561.method_43470(new BigDecimal(value).setScale(decimalPlaces, RoundingMode.HALF_UP).toPlainString());
    }

    public enum Axis {
        VERTICAL, HORIZONTAL
    }

    public interface OnChanged {
        void onChanged(double value);

        static EventStream<OnChanged> newStream() {
            return new EventStream<>(subscribers -> value -> {
                for (var subscriber : subscribers) {
                    subscriber.onChanged(value);
                }
            });
        }
    }

    public interface OnSlideEnd {
        void onSlideEnd();

        static EventStream<OnSlideEnd> newStream() {
            return new EventStream<>(subscribers -> () -> {
                for (var subscriber : subscribers) {
                    subscriber.onSlideEnd();
                }
            });
        }
    }
}
