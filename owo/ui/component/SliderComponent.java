package io.wispforest.owo.ui.component;

import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.function.Function;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_11910;
import net.minecraft.class_2561;
import net.minecraft.class_3532;
import net.minecraft.class_357;

public class SliderComponent extends class_357 {

    protected final EventStream<OnChanged> changedEvents = OnChanged.newStream();
    protected final EventStream<OnSlideEnd> slideEndEvents = OnSlideEnd.newStream();

    protected Function<String, class_2561> messageProvider = value -> class_2561.method_43473();
    protected double scrollStep = .05;

    protected SliderComponent(Sizing horizontalSizing) {
        super(0, 0, 0, 0, class_2561.method_43473(), 0);

        this.sizing(horizontalSizing, Sizing.fixed(20));
    }

    public SliderComponent value(double value) {
        value = class_3532.method_15350(value, 0, 1);

        if (this.field_22753 != value) {
            this.field_22753 = value;

            this.method_25346();
            this.method_25344();
        }

        return this;
    }

    public double value() {
        return this.field_22753;
    }

    public SliderComponent message(Function<String, class_2561> messageProvider) {
        this.messageProvider = messageProvider;
        this.method_25346();
        return this;
    }

    public SliderComponent scrollStep(double scrollStep) {
        this.scrollStep = scrollStep;
        return this;
    }

    public double scrollStep() {
        return this.scrollStep;
    }

    public SliderComponent active(boolean active) {
        this.field_22763 = active;
        return this;
    }

    public boolean active() {
        return this.field_22763;
    }

    public EventSource<OnChanged> onChanged() {
        return this.changedEvents.source();
    }

    public EventSource<OnSlideEnd> slideEnd() {
        return this.slideEndEvents.source();
    }

    @Override
    protected void method_25346() {
        this.method_25355(this.messageProvider.apply(String.valueOf(this.field_22753)));
    }

    @Override
    protected void method_25344() {
        this.changedEvents.sink().onChanged(this.field_22753);
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        if (!this.field_22763) return super.onMouseScroll(mouseX, mouseY, amount);

        this.value(this.field_22753 + this.scrollStep * amount);

        super.onMouseScroll(mouseX, mouseY, amount);
        return true;
    }

    @Override
    public boolean onMouseUp(class_11909 click) {
        this.slideEndEvents.sink().onSlideEnd();
        return super.onMouseUp(click);
    }

    @Override
    public boolean method_25404(class_11908 input) {
        if (!this.field_22763) return false;
        return super.method_25404(input);
    }

    @Override
    protected boolean method_25351(class_11910 input) {
        return this.field_22763 && super.method_25351(input);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        if (children.containsKey("text")) {
            var node = children.get("text");
            var content = node.getTextContent().strip();

            if (node.getAttribute("translate").equalsIgnoreCase("true")) {
                this.message(value -> class_2561.method_43469(content, value));
            } else {
                var text = class_2561.method_43470(content);
                this.message(value -> text);
            }
        }

        UIParsing.apply(children, "value", UIParsing::parseDouble, this::value);
    }

    /**
     * @deprecated Use {@link #message(Function)} instead,
     * as the message set by this method will be overwritten
     * the next time this slider is moved
     */
    @Override
    @Deprecated
    public final void method_25355(class_2561 message) {
        super.method_25355(message);
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
