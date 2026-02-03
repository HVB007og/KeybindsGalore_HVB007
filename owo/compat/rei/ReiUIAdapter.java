package io.wispforest.owo.compat.rei;

import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.ParentUIComponent;
import io.wispforest.owo.ui.core.Sizing;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_364;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ReiUIAdapter<T extends ParentUIComponent> extends Widget {

    public static final Point LAYOUT = new Point(-69, -69);

    public final OwoUIAdapter<T> adapter;

    public ReiUIAdapter(Rectangle bounds, BiFunction<Sizing, Sizing, T> rootComponentMaker) {
        this.adapter = OwoUIAdapter.createWithoutScreen(bounds.x, bounds.y, bounds.width, bounds.height, rootComponentMaker);
        this.adapter.inspectorZOffset = 900;

        var screenWithREI = class_310.method_1551().field_1755;

        if (screenWithREI != null) {
            ScreenEvents.remove(screenWithREI).register(screen -> this.adapter.dispose());
            ScreenEvents.afterRender(screenWithREI).register((screen, drawContext, mouseX, mouseY, tickDelta) -> {
                this.adapter.drawTooltip(drawContext, mouseX, mouseY, tickDelta);
            });
        }
    }

    public void prepare() {
        this.adapter.inflateAndMount();
    }

    public T rootComponent() {
        return this.adapter.rootComponent;
    }

    public <W extends WidgetWithBounds> ReiWidgetComponent wrap(W widget) {
        return new ReiWidgetComponent(widget);
    }

    public <W extends WidgetWithBounds> ReiWidgetComponent wrap(Function<Point, W> widgetFactory, Consumer<W> widgetConfigurator) {
        var widget = widgetFactory.apply(LAYOUT);
        widgetConfigurator.accept(widget);
        return new ReiWidgetComponent(widget);
    }

    @Override
    public boolean containsMouse(double mouseX, double mouseY) {
        return this.adapter.method_25405(mouseX, mouseY);
    }

    @Override
    public boolean method_25402(class_11909 click, boolean doubled) {
        return this.adapter.method_25402(new class_11909(click.comp_4798() - this.adapter.x(), click.comp_4799() - this.adapter.y(), click.comp_4800()), doubled);
    }

    @Override
    public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return this.adapter.method_25401(mouseX - this.adapter.x(), mouseY - this.adapter.y(), horizontalAmount, verticalAmount);
    }

    @Override
    public boolean method_25406(class_11909 click) {
        return this.adapter.method_25406(new class_11909(click.comp_4798() - this.adapter.x(), click.comp_4799() - this.adapter.y(), click.comp_4800()));
    }

    @Override
    public boolean method_25403(class_11909 click, double deltaX, double deltaY) {
        return this.adapter.method_25403(new class_11909(click.comp_4798() - this.adapter.x(), click.comp_4799() - this.adapter.y(), click.comp_4800()), deltaX, deltaY);
    }

    @Override
    public boolean method_25404(class_11908 input) {
        return this.adapter.method_25404(input);
    }

    @Override
    public boolean method_16803(class_11908 input) {
        return this.adapter.method_16803(input);
    }

    @Override
    public boolean method_25400(class_11905 input) {
        return this.adapter.method_25400(input);
    }

    @Override
    public void method_25394(class_332 context, int mouseX, int mouseY, float partialTicks) {
        context.method_44379(this.adapter.x(), this.adapter.y(), this.adapter.width(), this.adapter.height());
        this.adapter.method_25394(context, mouseX, mouseY, partialTicks);
        context.method_44380();
    }

    @Override
    public List<? extends class_364> method_25396() {
        return List.of();
    }
}
