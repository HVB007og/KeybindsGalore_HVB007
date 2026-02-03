package io.wispforest.owo.config.ui.component;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Expanded;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.CollapsibleContainer;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.UISounds;
import io.wispforest.owo.util.NumberReflection;
import io.wispforest.owo.util.ReflectionUtils;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.class_1074;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_4185;

@ApiStatus.Internal
public class ListOptionContainer<T> extends CollapsibleContainer implements OptionValueProvider {

    protected final Option<List<T>> backingOption;
    protected final List<T> backingList;

    protected final class_4185 resetButton;

    @SuppressWarnings("unchecked")
    public ListOptionContainer(Option<List<T>> option) {
        super(
                Sizing.fill(100), Sizing.content(),
                class_2561.method_43471("text.config." + option.configName() + ".option." + option.key().asString()),
                option.backingField().field().isAnnotationPresent(Expanded.class)
        );

        this.backingOption = option;
        this.backingList = new ArrayList<>(option.value());

        this.padding(this.padding.get().add(0, 5, 0, 0));

        this.titleLayout.horizontalSizing(Sizing.fill(100));
        this.titleLayout.verticalSizing(Sizing.fixed(30));
        this.titleLayout.verticalAlignment(VerticalAlignment.CENTER);

        if (!option.detached()) {
            this.titleLayout.child(UIComponents.label(class_2561.method_43471("text.owo.config.list.add_entry").method_27692(class_124.field_1080)).<LabelComponent>configure(label -> {
                label.cursorStyle(CursorStyle.HAND);

                label.mouseEnter().subscribe(() -> label.text(label.text().method_27661().method_27694(style -> style.method_10977(class_124.field_1054))));
                label.mouseLeave().subscribe(() -> label.text(label.text().method_27661().method_27694(style -> style.method_10977(class_124.field_1080))));
                label.mouseDown().subscribe((click, doubled) -> {
                    UISounds.playInteractionSound();
                    this.backingList.add((T) "");

                    if (!this.expanded) this.toggleExpansion();
                    this.refreshOptions();

                    var lastEntry = (ParentUIComponent) this.collapsibleChildren.get(this.collapsibleChildren.size() - 1);
                    this.focusHandler().focus(
                            lastEntry.children().get(lastEntry.children().size() - 1),
                            FocusSource.MOUSE_CLICK
                    );

                    return true;
                });
            }));
        }

        this.resetButton = UIComponents.button(class_2561.method_43470("⇄"), (ButtonComponent button) -> {
            this.backingList.clear();
            this.backingList.addAll(option.defaultValue());

            this.refreshOptions();
            button.field_22763 = false;
        });
        this.resetButton.margins(Insets.right(10));
        this.resetButton.positioning(Positioning.relative(100, 50));
        this.titleLayout.child(resetButton);
        this.refreshResetButton();

        this.refreshOptions();

        this.titleLayout.child(new SearchAnchorComponent(
                this.titleLayout,
                option.key(),
                () -> class_1074.method_4662("text.config." + option.configName() + ".option." + option.key().asString()),
                () -> this.backingList.stream().map(Objects::toString).collect(Collectors.joining())
        ));
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    protected void refreshOptions() {
        this.collapsibleChildren.clear();

        var listType = ReflectionUtils.getTypeArgument(this.backingOption.backingField().field().getGenericType(), 0);
        for (int i = 0; i < this.backingList.size(); i++) {
            var container = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
            container.verticalAlignment(VerticalAlignment.CENTER);

            int optionIndex = i;
            final var label = UIComponents.label(TextOps.withFormatting("- ", class_124.field_1080));
            label.margins(Insets.left(10));
            if (!this.backingOption.detached()) {
                label.cursorStyle(CursorStyle.HAND);
                label.mouseEnter().subscribe(() -> label.text(TextOps.withFormatting("x ", class_124.field_1080)));
                label.mouseLeave().subscribe(() -> label.text(TextOps.withFormatting("- ", class_124.field_1080)));
                label.mouseDown().subscribe((click, doubled) -> {
                    this.backingList.remove(optionIndex);
                    this.refreshResetButton();
                    this.refreshOptions();
                    UISounds.playInteractionSound();

                    return true;
                });
            }
            container.child(label);

            final var box = new ConfigTextBox();
            box.method_1852(this.backingList.get(i).toString());
            box.method_1870(false);
            box.method_1858(false);
            box.margins(Insets.vertical(2));
            box.horizontalSizing(Sizing.fill(95));
            box.verticalSizing(Sizing.fixed(8));

            if (!this.backingOption.detached()) {
                box.onChanged().subscribe(s -> {
                    if (!box.isValid()) return;

                    this.backingList.set(optionIndex, (T) box.parsedValue());
                    this.refreshResetButton();
                });
            } else {
                box.field_22763 = false;
            }

            if (NumberReflection.isNumberType(listType)) {
                box.configureForNumber((Class<? extends Number>) listType);
            }

            container.child(box);
            this.collapsibleChildren.add(container);
        }

        this.contentLayout.<FlowLayout>configure(layout -> {
            layout.clearChildren();
            if (this.expanded) layout.children(this.collapsibleChildren);
        });
        this.refreshResetButton();
    }

    protected void refreshResetButton() {
        this.resetButton.field_22763 = !this.backingOption.detached() && !this.backingList.equals(this.backingOption.defaultValue());
    }

    @Override
    public boolean shouldDrawTooltip(double mouseX, double mouseY) {
        return ((mouseY - this.y) <= this.titleLayout.height()) && super.shouldDrawTooltip(mouseX, mouseY);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Object parsedValue() {
        return this.backingList;
    }
}
