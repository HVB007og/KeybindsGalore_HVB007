package io.wispforest.owo.ui.component;

import io.wispforest.owo.mixin.ui.access.AbstractWidgetAccessor;
import io.wispforest.owo.mixin.ui.access.EditBoxAccessor;
import io.wispforest.owo.ui.base.BaseUIComponent;
import io.wispforest.owo.ui.core.*;
import java.util.function.Consumer;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_310;
import net.minecraft.class_339;
import net.minecraft.class_342;
import net.minecraft.class_3532;
import net.minecraft.class_4185;
import net.minecraft.class_4286;

public class VanillaWidgetComponent extends BaseUIComponent {

    private final class_339 widget;

    protected VanillaWidgetComponent(class_339 widget) {
        this.widget = widget;

        this.horizontalSizing.set(Sizing.fixed(this.widget.method_25368()));
        this.verticalSizing.set(Sizing.fixed(this.widget.method_25364()));

        if (widget instanceof class_342) {
            this.margins(Insets.none());
        }
    }

    public boolean hovered() {
        return this.hovered;
    }

    @Override
    public void mount(ParentUIComponent parent, int x, int y) {
        super.mount(parent, x, y);
        this.applyToWidget();
    }

    @Override
    protected void updateHoveredState(int mouseX, int mouseY, boolean nowHovered) {
        this.hovered = nowHovered;

        if (nowHovered) {
            if (this.root() == null || this.root().childAt(mouseX, mouseY) != this.widget) {
                this.hovered = false;
                return;
            }

            this.mouseEnterEvents.sink().onMouseEnter();
        } else {
            this.mouseLeaveEvents.sink().onMouseLeave();
        }
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        if (this.widget instanceof class_4185 || this.widget instanceof class_4286 || this.widget instanceof SliderComponent) {
            return 20;
        } else if (this.widget instanceof class_342 textField) {
            if (((EditBoxAccessor) textField).owo$bordered()) {
                return 20;
            } else {
                return 9;
            }
        } else if (this.widget instanceof TextAreaComponent textArea && textArea.maxLines() > 0) {
            return class_3532.method_15340(textArea.method_44391() / 9 + 1, 2, textArea.maxLines()) * 9 + (textArea.displayCharCount() ? 9 + 12 : 9);
        } else {
            throw new UnsupportedOperationException(this.widget.getClass().getSimpleName() + " does not support Sizing.content() on the vertical axis");
        }
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        if (this.widget instanceof class_4185 button) {
            return class_310.method_1551().field_1772.method_27525(button.method_25369()) + 8;
        } else if (this.widget instanceof class_4286 checkbox) {
            return class_310.method_1551().field_1772.method_27525(checkbox.method_25369()) + 24;
        } else {
            throw new UnsupportedOperationException(this.widget.getClass().getSimpleName() + " does not support Sizing.content() on the horizontal axis");
        }
    }

    @Override
    public BaseUIComponent margins(Insets margins) {
        if (widget instanceof class_342) {
            return super.margins(margins.add(1, 1, 1, 1));
        } else {
            return super.margins(margins);
        }
    }

    @Override
    public void inflate(Size space) {
        super.inflate(space);
        this.applyToWidget();
    }

    @Override
    public void updateX(int x) {
        super.updateX(x);
        this.applyToWidget();
    }

    @Override
    public void updateY(int y) {
        super.updateY(y);
        this.applyToWidget();
    }

    private void applyToWidget() {
        var accessor = (AbstractWidgetAccessor) this.widget;

        accessor.owo$setX(this.x + this.widget.xOffset());
        accessor.owo$setY(this.y + this.widget.yOffset());

        accessor.owo$setWidth(this.width + this.widget.widthOffset());
        accessor.owo$setHeight(this.height + this.widget.heightOffset());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends UIComponent> C configure(Consumer<C> closure) {
        try {
            this.runAndDeferEvents(() -> closure.accept((C) this.widget));
        } catch (ClassCastException theUserDidBadItWasNotMyFault) {
            throw new IllegalArgumentException(
                    "Invalid target class passed when configuring component of type " + this.getClass().getSimpleName(),
                    theUserDidBadItWasNotMyFault
            );
        }

        return (C) this.widget;
    }

    @Override
    public void notifyParentIfMounted() {
        super.notifyParentIfMounted();
    }

    @Override
    public void draw(OwoUIGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        this.widget.method_25394(graphics, mouseX, mouseY, 0);
    }

    @Override
    public boolean shouldDrawTooltip(double mouseX, double mouseY) {
        return this.widget.field_22764 && this.widget.field_22763 && super.shouldDrawTooltip(mouseX, mouseY);
    }

    @Override
    public boolean onMouseDown(class_11909 click, boolean doubled) {
        return this.widget.method_25402(new class_11909(this.x + click.comp_4798(), this.y + click.comp_4799(), click.comp_4800()), doubled)
                | super.onMouseDown(click, doubled);
    }

    @Override
    public boolean onMouseUp(class_11909 click) {
        return this.widget.method_25406(new class_11909(this.x + click.comp_4798(), this.y + click.comp_4799(), click.comp_4800()))
                | super.onMouseUp(click);
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        return this.widget.method_25401(this.x + mouseX, this.y + mouseY, 0, amount)
                | super.onMouseScroll(mouseX, mouseY, amount);
    }

    @Override
    public boolean onMouseDrag(class_11909 click, double deltaX, double deltaY) {
        return this.widget.method_25403(new class_11909(this.x + click.comp_4798(), this.y + click.comp_4799(), click.comp_4800()), deltaX, deltaY)
                | super.onMouseDrag(click, deltaX, deltaY);
    }

    @Override
    public boolean onCharTyped(class_11905 input) {
        return this.widget.method_25400(input)
                | super.onCharTyped(input);
    }

    @Override
    public boolean onKeyPress(class_11908 input) {
        return this.widget.method_25404(input)
                | super.onKeyPress(input);
    }
}
