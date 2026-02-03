package io.wispforest.owo.itemgroup.json;

import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import io.wispforest.owo.mixin.itemgroup.CreativeModeTabAccessor;
import io.wispforest.owo.util.pond.OwoSimpleRegistryExtensions;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;
import net.minecraft.class_1761;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.class_9248;

/**
 * Used to replace a vanilla or modded item group to add the JSON-defined
 * tabs while keeping the same name, id and icon
 */
@ApiStatus.Internal
public class WrapperGroup extends OwoItemGroup {

    private final class_1761 parent;
    private boolean extension = false;

    @SuppressWarnings("unchecked")
    public WrapperGroup(class_1761 parent, class_2960 parentId, List<ItemGroupTab> tabs, List<ItemGroupButton> buttons) {
        super(parentId, owoItemGroup -> {}, () -> Icon.of(parent.method_7747()), 4, 4, null, null, null, true, false, false);

        int parentRawId = class_7923.field_44687.method_10206(parent);

        ((OwoSimpleRegistryExtensions<class_1761>) class_7923.field_44687).owo$set(parentRawId, class_5321.method_29179(class_7924.field_44688, parentId), this, class_9248.field_49136);

        ((CreativeModeTabAccessor) this).owo$setDisplayName(parent.method_7737());
        ((CreativeModeTabAccessor) this).owo$setColumn(parent.method_7743());
        ((CreativeModeTabAccessor) this).owo$setRow(parent.method_47309());

        this.parent = parent;

        this.tabs.addAll(tabs);
        this.buttons.addAll(buttons);
    }

    public void addTabs(Collection<ItemGroupTab> tabs) {
        this.tabs.addAll(tabs);
    }

    public void addButtons(Collection<ItemGroupButton> buttons) {
        this.buttons.addAll(buttons);
    }

    public void markExtension() {
        if (this.extension) return;
        this.extension = true;

        if (this.tabs.get(0) == PLACEHOLDER_TAB) {
            this.tabs.remove(0);
        }

        this.tabs.add(0, new ItemGroupTab(
                Icon.of(this.parent.method_7747()),
                this.parent.method_7737(),
                ((CreativeModeTabAccessor) this.parent).owo$getDisplayItemsGenerator()::accept,
                ItemGroupTab.DEFAULT_TEXTURE,
                true
        ));
    }
}
