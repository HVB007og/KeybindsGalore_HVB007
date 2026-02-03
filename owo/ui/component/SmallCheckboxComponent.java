package io.wispforest.owo.ui.component;

import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.base.BaseUIComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.UISounds;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import io.wispforest.owo.util.Observable;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.w3c.dom.Element;

import java.util.Map;
import net.minecraft.class_10799;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;

public class SmallCheckboxComponent extends BaseUIComponent {

    public static final class_2960 TEXTURE = Owo.id("textures/gui/smol_checkbox.png");

    protected final EventStream<OnChanged> checkedEvents = OnChanged.newStream();

    protected final Observable<@Nullable class_2561> label;
    protected boolean labelShadow = false;
    protected boolean checked = false;

    public SmallCheckboxComponent(class_2561 label) {
        this.cursorStyle(CursorStyle.HAND);

        this.label = Observable.of(label);
        this.label.observe(text -> this.notifyParentIfMounted());
    }

    public SmallCheckboxComponent() {
        this(null);
    }

    @Override
    public void draw(OwoUIGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        if (this.label.get() != null) {
            graphics.method_51439(class_310.method_1551().field_1772, this.label.get(), this.x + 13 + 2, this.y + 3, Color.WHITE.argb(), this.labelShadow);
        }

        graphics.method_25302(class_10799.field_56883, TEXTURE, this.x, this.y, 0, 0, 13, 13, 13, 13, 32, 16);
        if (this.checked) {
            graphics.method_25302(class_10799.field_56883, TEXTURE, this.x, this.y, 16, 0, 13, 13, 13, 13, 32, 16);
        }
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return this.label.get() != null
                ? 13 + 2 + class_310.method_1551().field_1772.method_27525(this.label.get())
                : 13;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return 13;
    }

    @Override
    public boolean onMouseDown(class_11909 click, boolean doubled) {
        boolean result = super.onMouseDown(click, doubled);

        if (click.method_74245() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.toggle();
            return true;
        }

        return result;
    }

    @Override
    public boolean onKeyPress(class_11908 input) {
        boolean result = super.onKeyPress(input);

        if (input.method_74229()) {
            this.toggle();
            return true;
        }

        return result;
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }

    public void toggle() {
        this.checked(!this.checked);
        UISounds.playInteractionSound();
    }

    public EventSource<OnChanged> onChanged() {
        return this.checkedEvents.source();
    }

    public SmallCheckboxComponent checked(boolean checked) {
        this.checked = checked;
        this.checkedEvents.sink().onChanged(this.checked);

        return this;
    }

    public boolean checked() {
        return checked;
    }

    public SmallCheckboxComponent label(class_2561 label) {
        this.label.set(label);
        return this;
    }

    public class_2561 label() {
        return this.label.get();
    }

    public SmallCheckboxComponent labelShadow(boolean labelShadow) {
        this.labelShadow = labelShadow;
        return this;
    }

    public boolean labelShadow() {
        return labelShadow;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "label", UIParsing::parseText, this::label);
        UIParsing.apply(children, "label-shadow", UIParsing::parseBool, this::labelShadow);
        UIParsing.apply(children, "checked", UIParsing::parseBool, this::checked);
    }

    public interface OnChanged {
        void onChanged(boolean nowChecked);

        static EventStream<OnChanged> newStream() {
            return new EventStream<>(subscribers -> value -> {
                for (var subscriber : subscribers) {
                    subscriber.onChanged(value);
                }
            });
        }
    }
}
