package io.wispforest.owo.braid.widgets;

import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.MouseArea;
import io.wispforest.owo.braid.widgets.label.Label;
import net.minecraft.class_2561;
import net.minecraft.class_2583;

public class HoverStyledLabel extends StatefulWidget {

    public final class_2561 defaultText;
    public final class_2583 hoverStyle;

    public HoverStyledLabel(class_2561 defaultText, class_2583 hoverStyle) {
        this.defaultText = defaultText;
        this.hoverStyle = hoverStyle;
    }

    @Override
    public WidgetState<HoverStyledLabel> createState() {
        return new State();
    }

    public static class State extends WidgetState<HoverStyledLabel> {

        private boolean hovered = false;

        @Override
        public Widget build(BuildContext context) {
            return new MouseArea(
                widget -> widget
                    .enterCallback(() -> setState(() -> this.hovered = true))
                    .exitCallback(() -> setState(() -> this.hovered = false)),
                new Label(this.hovered ? this.widget().defaultText.method_27661().method_27696(this.widget().hoverStyle) : this.widget().defaultText)
            );
        }
    }
}
