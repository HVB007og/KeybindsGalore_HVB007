package io.wispforest.owo.ui.base;

import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.ParentUIComponent;
import io.wispforest.owo.ui.core.Size;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5684;

@ApiStatus.Experimental
public abstract class BaseOwoTooltipComponent<R extends ParentUIComponent> implements class_5684 {

    protected final R rootComponent;
    protected int virtualWidth = 1000, virtualHeight = 1000;

    protected BaseOwoTooltipComponent(Supplier<R> components) {
        this.rootComponent = components.get();

        this.rootComponent.inflate(Size.of(this.virtualWidth, this.virtualHeight));
        this.rootComponent.mount(null, 0, 0);
    }

    @Override
    public void method_32666(class_327 textRenderer, int x, int y, int width, int height, class_332 context) {
        var tickCounter = class_310.method_1551().method_61966();

        this.rootComponent.moveTo(x, y);
        this.rootComponent.draw(OwoUIGraphics.of(context), -1000, -1000, tickCounter.method_60637(false), tickCounter.method_60636());
    }

    @Override
    public int method_32661(class_327 textRenderer) {
        return this.rootComponent.fullSize().height();
    }

    @Override
    public int method_32664(class_327 textRenderer) {
        return this.rootComponent.fullSize().width();
    }
}
