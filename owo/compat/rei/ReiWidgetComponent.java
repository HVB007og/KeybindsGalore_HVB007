package io.wispforest.owo.compat.rei;

import io.wispforest.owo.ui.base.BaseUIComponent;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.ParentUIComponent;
import io.wispforest.owo.ui.core.Sizing;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;

public class ReiWidgetComponent extends BaseUIComponent {

    private final WidgetWithBounds widget;

    protected ReiWidgetComponent(WidgetWithBounds widget) {
        this.widget = widget;

        var bounds = widget.getBounds();
        this.horizontalSizing.set(Sizing.fixed(bounds.getWidth()));
        this.verticalSizing.set(Sizing.fixed(bounds.getHeight()));

        this.mouseEnter().subscribe(() -> {
            this.focusHandler().focus(this, FocusSource.KEYBOARD_CYCLE);
        });

        this.mouseLeave().subscribe(() -> {
            this.focusHandler().focus(null, null);
        });
    }

    @Override
    public void mount(ParentUIComponent parent, int x, int y) {
        super.mount(parent, x, y);
        this.applyToWidget();
    }

    @Override
    public void draw(OwoUIGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        this.widget.method_25394(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawFocusHighlight(OwoUIGraphics context, int mouseX, int mouseY, float partialTicks, float delta) {}

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return this.widget.getBounds().getWidth();
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return this.widget.getBounds().getHeight();
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
        var bounds = this.widget.getBounds();

        bounds.x = this.x;
        bounds.y = this.y;

        bounds.width = this.width;
        bounds.height = this.height;
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

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }
}
