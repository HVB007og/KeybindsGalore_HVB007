package io.wispforest.owo.braid.widgets.basic;

import io.wispforest.owo.braid.framework.instance.InstanceHost;
import io.wispforest.owo.braid.framework.instance.SingleChildWidgetInstance;
import io.wispforest.owo.braid.framework.instance.TooltipProvider;
import io.wispforest.owo.braid.framework.widget.SingleChildInstanceWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_5683;
import net.minecraft.class_5684;

public class Tooltip extends SingleChildInstanceWidget {

    public final @Nullable List<class_5684> tooltip;
    public final class_2561 tooltipText;

    public Tooltip(@NotNull List<class_5684> tooltip, Widget child) {
        super(child);
        this.tooltip = tooltip;
        this.tooltipText = null;
    }

    public Tooltip(Collection<class_2561> tooltip, Widget child) {
        this(
            tooltip.stream().map(class_2561::method_30937).<class_5684>map(class_5683::new).toList(),
            child
        );
    }

    public Tooltip(class_2561 tooltip, Widget child) {
        super(child);
        this.tooltip = null;
        this.tooltipText = tooltip;
    }

    @Override
    public SingleChildWidgetInstance<?> instantiate() {
        return new Instance(this);
    }

    public static class Instance extends SingleChildWidgetInstance.ShrinkWrap<Tooltip> implements TooltipProvider {
        private @Nullable List<class_5684> tooltip;

        public Instance(Tooltip widget) {
            super(widget);
        }

        @Override
        public void attachHost(InstanceHost host) {
            super.attachHost(host);
            this.setup();
        }

        @Override
        public void setWidget(Tooltip widget) {
            super.setWidget(widget);
            this.setup();
        }

        private void setup() {
            this.tooltip = widget.tooltipText != null
                ? this.host().client().field_1772
                .method_1728(widget.tooltipText, Integer.MAX_VALUE)
                .stream()
                .<class_5684>map(class_5683::new)
                .toList()
                : widget.tooltip;
        }

        @Override
        public @Nullable List<class_5684> getTooltipComponentsAt(double x, double y) {
            return tooltip;
        }
    }
}
