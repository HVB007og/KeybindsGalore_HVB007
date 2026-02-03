package io.wispforest.owo.config.ui;

import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.class_310;
import net.minecraft.class_437;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class RestartRequiredScreen extends BaseUIModelScreen<FlowLayout> {

    protected final class_437 parent;

    public RestartRequiredScreen(class_437 parent) {
        super(FlowLayout.class, DataSource.asset(Owo.id("restart_required")));
        this.parent = parent;
    }

    @Override
    public void method_25419() {
        this.field_22787.method_1507(parent);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void build(FlowLayout rootComponent) {
        if (this.field_22787.field_1687 == null) {
            rootComponent.surface(Surface.optionsBackground());
        }

        rootComponent.childById(ButtonComponent.class, "exit-button")
                .onPress(button -> class_310.method_1551().method_1592());

        rootComponent.childById(ButtonComponent.class, "ignore-button")
                .onPress(button -> this.method_25419());
    }
}
