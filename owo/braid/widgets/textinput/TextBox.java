package io.wispforest.owo.braid.widgets.textinput;

import io.wispforest.owo.braid.core.Color;
import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.framework.widget.WidgetSetupCallback;
import io.wispforest.owo.braid.widgets.basic.Box;
import io.wispforest.owo.braid.widgets.basic.Padding;
import io.wispforest.owo.braid.widgets.focus.Focusable;
import net.minecraft.class_2583;
import net.minecraft.class_8012;

public class TextBox extends StatefulWidget {

    public final TextEditingController controller;
    private final EditableText editableText;

    public TextBox(
        TextEditingController controller,
        WidgetSetupCallback<EditableText> setupCallback
    ) {
        this.controller = controller;
        this.editableText = new EditableText(
            controller,
            widget -> {
                setupCallback.setup(widget);
                widget.suggestion(widget.suggestion().method_27661().method_27694(style -> style.method_27702(class_2583.field_24360.method_36139(class_8012.field_44941))));
            }
        );
    }

    @Override
    public WidgetState<TextBox> createState() {
        return new State();
    }

    public static class State extends WidgetState<TextBox> {

        private boolean focused = false;

        @Override
        public Widget build(BuildContext context) {
            return new Box(
                //TODO: use panel instead of box here
                this.focused ? Color.WHITE : new Color(class_8012.field_45073),
                new Focusable(
                    widget -> widget
                        .focusGainedCallback(() -> this.setState(() -> this.focused = true))
                        .focusLostCallback(() -> this.setState(() -> this.focused = false))
                        .skipTraversal(true),
                    new Padding(
                        Insets.all(1),
                        new Box(
                            Color.BLACK,
                            new Padding(
                                Insets.all(2),
                                this.widget().editableText
                            )
                        )
                    )
                )
            );
        }
    }
}
