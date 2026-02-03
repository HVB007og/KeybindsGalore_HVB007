package io.wispforest.owo.config.ui.component;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.class_11907;
import net.minecraft.class_2561;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ConfigToggleButton extends ButtonComponent implements OptionValueProvider {

    protected static final class_2561 ENABLED_MESSAGE = class_2561.method_43471("text.owo.config.boolean_toggle.enabled");
    protected static final class_2561 DISABLED_MESSAGE = class_2561.method_43471("text.owo.config.boolean_toggle.disabled");

    protected boolean enabled = false;

    public ConfigToggleButton() {
        super(class_2561.method_43473(), button -> {});
        this.verticalSizing(Sizing.fixed(20));
        this.updateMessage();
    }

    @Override
    public void method_25306(class_11907 input) {
        this.enabled = !this.enabled;
        this.updateMessage();
        super.method_25306(input);
    }

    protected void updateMessage() {
        this.method_25355(this.enabled ? ENABLED_MESSAGE : DISABLED_MESSAGE);
    }

    public ConfigToggleButton enabled(boolean enabled) {
        this.enabled = enabled;
        this.updateMessage();
        return this;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Object parsedValue() {
        return this.enabled;
    }
}
