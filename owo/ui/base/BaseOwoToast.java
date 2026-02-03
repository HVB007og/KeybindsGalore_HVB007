package io.wispforest.owo.ui.base;

import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.ParentUIComponent;
import io.wispforest.owo.ui.core.Size;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_368;
import net.minecraft.class_374;

@ApiStatus.Experimental
public abstract class BaseOwoToast<R extends ParentUIComponent> implements class_368 {

    protected final R rootComponent;
    protected final VisibilityPredicate<R> visibilityPredicate;

    protected int virtualWidth = 1000, virtualHeight = 1000;

    protected BaseOwoToast(Supplier<R> components, VisibilityPredicate<R> predicate) {
        this.rootComponent = components.get();
        this.visibilityPredicate = predicate;

        this.rootComponent.inflate(Size.of(this.virtualWidth, this.virtualHeight));
        this.rootComponent.mount(null, 0, 0);
    }

    protected BaseOwoToast(Supplier<R> rootComponent, Duration timeout) {
        this(rootComponent, VisibilityPredicate.timeout(timeout));
    }

    private class_369 visibility = class_369.field_2209;

    @Override
    public void method_61989(class_374 manager, long time) {
        final var delta = class_310.method_1551().method_61966().method_60636();

        var client = manager.method_1995();
        var window = client.method_22683();

        int mouseX = -1000; //(int)(client.mouse.getX() * (double) window.getScaledWidth() / (double) window.getWidth());
        int mouseY = -1000; //(int)(client.mouse.getY() * (double) window.getScaledHeight() / (double) window.getHeight());

        this.rootComponent.update(delta, mouseX, mouseY);

        this.visibility = this.visibilityPredicate.test(this, time);
    }

    @Override
    public class_369 method_61988() {
        return this.visibility;
    }

    @Override
    public void method_1986(class_332 context, class_327 textRenderer, long startTime) {
        var tickCounter = class_310.method_1551().method_61966();

        this.rootComponent.draw(OwoUIGraphics.of(context), -1000, -1000, tickCounter.method_60637(false), tickCounter.method_60636());
    }

    @Override
    public int method_29050() {
        return this.rootComponent.fullSize().height();
    }

    @Override
    public int method_29049() {
        return this.rootComponent.fullSize().width();
    }

    @FunctionalInterface
    public interface VisibilityPredicate<R extends ParentUIComponent> {
        class_369 test(BaseOwoToast<R> toast, long startTime);

        static <R extends ParentUIComponent> VisibilityPredicate<R> timeout(Duration timeout) {
            return (toast, startTime) -> System.currentTimeMillis() - startTime <= timeout.get(ChronoUnit.MILLIS) ? class_369.field_2209 : class_369.field_2210;
        }
    }
}
