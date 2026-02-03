package io.wispforest.owo.braid.widgets.textinput;

import io.wispforest.owo.braid.core.ListenableValue;
import net.minecraft.class_2561;
import net.minecraft.class_2583;

public class TextEditingController extends ListenableValue<TextEditingValue> {

    public TextEditingController(String text, TextSelection selection) {
        super(new TextEditingValue(text, selection));
    }

    public TextEditingController(String text) {
        this(text, TextSelection.collapsed(text.length()));
    }

    public TextEditingController() {
        this("");
    }

    public class_2561 createTextForRendering(class_2583 baseStyle) {
        return class_2561.method_43470(this.value().text()).method_27694(style -> baseStyle.method_27702(baseStyle));
    }
}
