package io.wispforest.owo.ui.component;

import io.wispforest.owo.mixin.ui.access.CheckboxAccessor;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.util.Observable;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.class_11907;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_4286;

public class CheckboxComponent extends class_4286 {

    protected final Observable<Boolean> listeners;

    protected CheckboxComponent(class_2561 message) {
        super(0, 0, 0, message, class_310.method_1551().field_1772, false, (checkbox, checked) -> {});
        this.listeners = Observable.of(this.method_20372());
        this.sizing(Sizing.content(), Sizing.fixed(20));
    }

    @Override
    public void method_25306(class_11907 input) {
        super.method_25306(input);
        this.listeners.set(this.method_20372());
    }

    public CheckboxComponent checked(boolean checked) {
        ((CheckboxAccessor) this).owo$setSelected(checked);
        this.listeners.set(this.method_20372());
        return this;
    }

    public CheckboxComponent onChanged(Consumer<Boolean> listener) {
        this.listeners.observe(listener);
        return this;
    }

    @Override
    public void inflate(Size space) {
        super.inflate(space);
        ((CheckboxAccessor) this).owo$getTextWidget().method_48984(this.field_22758);
    }

    @Override
    public void method_25355(class_2561 message) {
        super.method_25355(message);
        ((CheckboxAccessor)this).owo$getTextWidget().method_25355(message);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "checked", UIParsing::parseBool, this::checked);
        UIParsing.apply(children, "text", UIParsing::parseText, this::method_25355);
    }

    public CursorStyle owo$preferredCursorStyle() {
        return CursorStyle.HAND;
    }
}
