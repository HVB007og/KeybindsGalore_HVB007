package io.wispforest.owo.itemgroup.gui;

import io.wispforest.owo.itemgroup.OwoItemGroup;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;
import net.minecraft.class_10799;
import net.minecraft.class_332;
import net.minecraft.class_4185;

@ApiStatus.Internal
public class ItemGroupButtonWidget extends class_4185 {

    public boolean isSelected = false;
    private final OwoItemGroup.ButtonDefinition definition;
    private final int baseU;

    public ItemGroupButtonWidget(int x, int y, int baseU, OwoItemGroup.ButtonDefinition definition, Consumer<ItemGroupButtonWidget> onPress) {
        super(x, y, 24, 24, definition.tooltip(), button -> onPress.accept((ItemGroupButtonWidget) button), class_4185.field_40754);
        this.baseU = baseU;
        this.definition = definition;
    }

    @Override
    public void method_75752(class_332 context, int mouseX, int mouseY, float delta) {
        context.method_25290(class_10799.field_56883, this.definition.texture(), this.method_46426(), this.method_46427(), this.baseU, this.method_25367() || this.isSelected ? this.field_22759 : 0, this.field_22758, this.field_22759, 64, 64);

        this.definition.icon().render(context, this.method_46426() + 4, this.method_46427() + 4, mouseX, mouseY, delta);
    }

    public boolean isTab() {
        return this.definition instanceof ItemGroupTab;
    }

    public boolean trulyHovered() {
        return this.field_22762;
    }
}
