package io.wispforest.owo.itemgroup;

import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.itemgroup.gui.ItemGroupButtonWidget;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import io.wispforest.owo.mixin.itemgroup.CreativeModeTabAccessor;
import io.wispforest.owo.util.pond.OwoItemExtensions;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1761;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_6862;
import net.minecraft.class_7699;
import net.minecraft.class_7923;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Extensions for  {@link class_1761} which support multiple sub-tabs
 * within, as well as arbitrary buttons with defaults provided for links
 * to places like GitHub, Modrinth, etc.
 * <p>
 * Tabs can be populated by setting the {@link OwoItemSettingsExtension#tab(int)}.
 * Furthermore, tags can be used for easily populating tabs from data
 * <p>
 * The roots of this implementation originated in Biome Makeover, where it was written by Lemonszz
 */
public abstract class OwoItemGroup extends class_1761 {

    public static final BiConsumer<class_1792, class_7704> DEFAULT_STACK_GENERATOR = (item, stacks) -> stacks.method_45420(item.method_7854());

    protected static final ItemGroupTab PLACEHOLDER_TAB = new ItemGroupTab(Icon.of(class_1802.field_8162), class_2561.method_43473(), (br, uh) -> {}, ItemGroupTab.DEFAULT_TEXTURE, false);

    public final List<ItemGroupTab> tabs = new ArrayList<>();
    public final List<ItemGroupButton> buttons = new ArrayList<>();

    private final Consumer<OwoItemGroup> initializer;

    private final Supplier<Icon> iconSupplier;
    private Icon icon;

    private final IntSet activeTabs = new IntAVLTreeSet(IntComparators.NATURAL_COMPARATOR);
    private final IntSet activeTabsView = IntSets.unmodifiable(this.activeTabs);
    private boolean initialized = false;

    private final @Nullable class_2960 backgroundTexture;
    private final @Nullable ScrollerTextures scrollerTextures;
    private final @Nullable TabTextures tabTextures;

    private final int tabStackHeight;
    private final int buttonStackHeight;
    private final boolean useDynamicTitle;
    private final boolean displaySingleTab;
    private final boolean allowMultiSelect;

    protected OwoItemGroup(class_2960 id, Consumer<OwoItemGroup> initializer, Supplier<Icon> iconSupplier, int tabStackHeight, int buttonStackHeight, @Nullable class_2960 backgroundTexture, @Nullable ScrollerTextures scrollerTextures, @Nullable TabTextures tabTextures, boolean useDynamicTitle, boolean displaySingleTab, boolean allowMultiSelect) {
        super(null, -1, class_7916.field_41052, class_2561.method_43471("itemGroup.%s.%s".formatted(id.method_12836(), id.method_12832())), () -> class_1799.field_8037, (displayContext, entries) -> {});
        this.initializer = initializer;
        this.iconSupplier = iconSupplier;
        this.tabStackHeight = tabStackHeight;
        this.buttonStackHeight = buttonStackHeight;
        this.backgroundTexture = backgroundTexture;
        this.scrollerTextures = scrollerTextures;
        this.tabTextures = tabTextures;
        this.useDynamicTitle = useDynamicTitle;
        this.displaySingleTab = displaySingleTab;
        this.allowMultiSelect = allowMultiSelect;

        ((CreativeModeTabAccessor) this).owo$setDisplayItemsGenerator((context, entries) -> {
            if (!this.initialized) {
                throw new IllegalStateException("oωo item group not initialized, was 'initialize()' called?");
            }

            this.activeTabs.forEach(tabIdx -> {
                this.tabs.get(tabIdx).contentSupplier().addItems(context, entries);
                this.collectItemsFromRegistry(entries, tabIdx);
            });
        });
    }

    public static Builder builder(class_2960 id, Supplier<Icon> iconSupplier) {
        return new Builder(id, iconSupplier);
    }

    // ---------

    /**
     * Executes {@link #initializer} and makes sure this item group is ready for use
     * <p>
     * Call this after all of your items have been registered to make sure your icons
     * show up correctly
     */
    public void initialize() {
        if (this.initialized) return;

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) this.initializer.accept(this);
        if (this.tabs.isEmpty()) this.tabs.add(PLACEHOLDER_TAB);

        if (this.allowMultiSelect) {
            for (int tabIdx = 0; tabIdx < this.tabs.size(); tabIdx++) {
                if (!this.tabs.get(tabIdx).primary()) continue;
                this.activeTabs.add(tabIdx);
            }

            if (this.activeTabs.isEmpty()) this.activeTabs.add(0);
        } else {
            this.activeTabs.add(0);
        }

        this.initialized = true;
    }

    /**
     * Adds the specified button to the buttons on
     * the right side of the creative menu
     *
     * @param button The button to add
     * @see ItemGroupButton#link(class_1761, Icon, String, String)
     * @see ItemGroupButton#curseforge(class_1761, String)
     * @see ItemGroupButton#discord(class_1761, String)
     */
    public void addButton(ItemGroupButton button) {
        this.buttons.add(button);
    }

    /**
     * Adds a new tab to this group
     *
     * @param icon       The icon to use
     * @param name       The name of the tab, used for the translation key
     * @param contentTag The tag used for filling this tab
     * @param texture    The texture to use for drawing the button
     * @see Icon#of(class_1935)
     */
    public void addTab(Icon icon, String name, @Nullable class_6862<class_1792> contentTag, class_2960 texture, boolean primary) {
        this.tabs.add(new ItemGroupTab(
                icon,
                ButtonDefinition.tooltipFor(this, "tab", name),
                contentTag == null
                        ? (context, entries) -> {}
                        : (context, entries) -> class_7923.field_41178.method_10220().filter(item -> item.method_40131().method_40220(contentTag)).forEach(entries::method_45421),
                texture,
                primary
        ));
    }

    /**
     * Adds a new tab to this group, using the default button texture
     *
     * @param icon       The icon to use
     * @param name       The name of the tab, used for the translation key
     * @param contentTag The tag used for filling this tab
     * @see Icon#of(class_1935)
     */
    public void addTab(Icon icon, String name, @Nullable class_6862<class_1792> contentTag, boolean primary) {
        addTab(icon, name, contentTag, ItemGroupTab.DEFAULT_TEXTURE, primary);
    }

    /**
     * Adds a new tab to this group, using the default button texture
     *
     * @param icon            The icon to use
     * @param name            The name of the tab, used for the translation key
     * @param contentSupplier The function used for filling this tab
     * @param texture         The texture to use for drawing the button
     * @see Icon#of(class_1935)
     */
    public void addCustomTab(Icon icon, String name, ItemGroupTab.ContentSupplier contentSupplier, class_2960 texture, boolean primary) {
        this.tabs.add(new ItemGroupTab(
                icon,
                ButtonDefinition.tooltipFor(this, "tab", name),
                contentSupplier, texture, primary
        ));
    }

    /**
     * Adds a new tab to this group
     *
     * @param icon            The icon to use
     * @param name            The name of the tab, used for the translation key
     * @param contentSupplier The function used for filling this tab
     * @see Icon#of(class_1935)
     */
    public void addCustomTab(Icon icon, String name, ItemGroupTab.ContentSupplier contentSupplier, boolean primary) {
        this.addCustomTab(icon, name, contentSupplier, ItemGroupTab.DEFAULT_TEXTURE, primary);
    }

    @Override
    public void method_47306(class_8128 context) {
        super.method_47306(context);

        var searchEntries = new SearchOnlyEntries(this, context.comp_1251());

        this.collectItemsFromRegistry(searchEntries, -1);
        this.tabs.forEach(tab -> tab.contentSupplier().addItems(context, searchEntries));

        ((CreativeModeTabAccessor) this).owo$setDisplayItemsSearchTab(searchEntries.field_40188);
    }

    protected void collectItemsFromRegistry(class_7704 entries, int tab) {
        class_7923.field_41178.method_10220()
                .filter(item -> ((OwoItemExtensions) item).owo$group() == this && (tab < 0 || tab == ((OwoItemExtensions) item).owo$tab()))
                .forEach(item -> ((OwoItemExtensions) item).owo$stackGenerator().accept(item, entries));
    }

    // Getters and setters

    /**
     * Select only {@code tab}, deselecting all other tabs,
     * using {@code context} for re-population
     */
    public void selectSingleTab(int tab, class_8128 context) {
        this.activeTabs.clear();
        this.activeTabs.add(tab);

        this.method_47306(context);
    }

    /**
     * Select {@code tab} in addition to other currently selected
     * tabs, using {@code context} for re-population.
     * <p>
     * If this group does not allow multiple selection, behaves
     * like {@link #selectSingleTab(int, class_8128)}
     */
    public void selectTab(int tab, class_8128 context) {
        if (!this.allowMultiSelect) {
            this.activeTabs.clear();
        }

        this.activeTabs.add(tab);
        this.method_47306(context);
    }

    /**
     * Deselect {@code tab} if it is currently selected, using {@code context} for
     * re-population. If this results in no tabs being selected, all tabs are
     * automatically selected instead
     */
    public void deselectTab(int tab, class_8128 context) {
        if (!this.allowMultiSelect) return;

        this.activeTabs.remove(tab);
        if (this.activeTabs.isEmpty()) {
            for (int tabIdx = 0; tabIdx < this.tabs.size(); tabIdx++) {
                this.activeTabs.add(tabIdx);
            }
        }

        this.method_47306(context);
    }

    /**
     * Shorthand for {@link #selectTab(int, class_8128)} or
     * {@link #deselectTab(int, class_8128)}, depending on the tabs
     * current state
     */
    public void toggleTab(int tab, class_8128 context) {
        if (this.isTabSelected(tab)) {
            this.deselectTab(tab, context);
        } else {
            this.selectTab(tab, context);
        }
    }

    /**
     * @return A set containing the indices of all currently
     * selected tabs
     */
    public IntSet selectedTabs() {
        return this.activeTabsView;
    }

    /**
     * @return {@code true} if {@code tab} is currently selected
     */
    public boolean isTabSelected(int tab) {
        return this.activeTabs.contains(tab);
    }

    public @Nullable class_2960 getOwoBackgroundTexture() {
        return this.backgroundTexture;
    }

    public @Nullable ScrollerTextures getScrollerTextures() {
        return this.scrollerTextures;
    }

    public @Nullable TabTextures getTabTextures() {
        return this.tabTextures;
    }

    public int getTabStackHeight() {
        return tabStackHeight;
    }

    public int getButtonStackHeight() {
        return buttonStackHeight;
    }

    public boolean hasDynamicTitle() {
        return this.useDynamicTitle && (this.tabs.size() > 1 || this.shouldDisplaySingleTab());
    }

    public boolean shouldDisplaySingleTab() {
        return this.displaySingleTab;
    }

    public boolean canSelectMultipleTabs() {
        return this.allowMultiSelect;
    }

    public List<ItemGroupButton> getButtons() {
        return buttons;
    }

    public ItemGroupTab getTab(int index) {
        return index < this.tabs.size() ? this.tabs.get(index) : null;
    }

    public Icon icon() {
        return this.icon == null
                ? this.icon = this.iconSupplier.get()
                : this.icon;
    }

    @Override
    public boolean method_47311() {
        return true;
    }

    public class_2960 id() {
        return class_7923.field_44687.method_10221(this);
    }

    public static class Builder {

        private final class_2960 id;
        private final Supplier<Icon> iconSupplier;

        private Consumer<OwoItemGroup> initializer = owoItemGroup -> {};
        private int tabStackHeight = 4;
        private int buttonStackHeight = 4;
        private @Nullable class_2960 backgroundTexture = null;
        private @Nullable ScrollerTextures scrollerTextures = null;
        private @Nullable TabTextures tabTextures = null;
        private boolean useDynamicTitle = true;
        private boolean displaySingleTab = false;
        private boolean allowMultiSelect = true;

        private Builder(class_2960 id, Supplier<Icon> iconSupplier) {
            this.id = id;
            this.iconSupplier = iconSupplier;
        }

        public Builder initializer(Consumer<OwoItemGroup> initializer) {
            this.initializer = initializer;
            return this;
        }

        public Builder tabStackHeight(int tabStackHeight) {
            this.tabStackHeight = tabStackHeight;
            return this;
        }

        public Builder buttonStackHeight(int buttonStackHeight) {
            this.buttonStackHeight = buttonStackHeight;
            return this;
        }

        public Builder backgroundTexture(@Nullable class_2960 backgroundTexture) {
            this.backgroundTexture = backgroundTexture;
            return this;
        }

        public Builder scrollerTextures(ScrollerTextures scrollerTextures) {
            this.scrollerTextures = scrollerTextures;
            return this;
        }

        public Builder tabTextures(TabTextures tabTextures) {
            this.tabTextures = tabTextures;
            return this;
        }

        public Builder disableDynamicTitle() {
            this.useDynamicTitle = false;
            return this;
        }

        public Builder displaySingleTab() {
            this.displaySingleTab = true;
            return this;
        }

        public Builder withoutMultipleSelection() {
            this.allowMultiSelect = false;
            return this;
        }

        public OwoItemGroup build() {
            final var group = new OwoItemGroup(id, initializer, iconSupplier, tabStackHeight, buttonStackHeight, backgroundTexture, scrollerTextures, tabTextures, useDynamicTitle, displaySingleTab, allowMultiSelect) {};
            class_2378.method_10230(class_7923.field_44687, this.id, group);
            return group;
        }
    }

    protected static class SearchOnlyEntries extends class_7703 {

        public SearchOnlyEntries(class_1761 group, class_7699 enabledFeatures) {
            super(group, enabledFeatures);
        }

        @Override
        public void method_45417(class_1799 stack, class_7705 visibility) {
            if (visibility == class_7705.field_40192) return;
            super.method_45417(stack, class_7705.field_40193);
        }
    }

    public record ScrollerTextures(class_2960 enabled, class_2960 disabled) {}
    public record TabTextures(class_2960 topSelected, class_2960 topSelectedFirstColumn, class_2960 topUnselected, class_2960 bottomSelected, class_2960 bottomSelectedFirstColumn, class_2960 bottomUnselected) {}

    // Utility

    /**
     * Defines a button's appearance and translation key
     * <p>
     * Used by {@link ItemGroupButtonWidget}
     */
    public interface ButtonDefinition {

        Icon icon();

        class_2960 texture();

        class_2561 tooltip();

        static class_2561 tooltipFor(class_1761 group, String component, String componentName) {
            var registryId = class_7923.field_44687.method_10221(group);
            var groupId = registryId.method_12836().equals("minecraft")
                    ? registryId.method_12832()
                    : registryId.method_12836() + "." + registryId.method_12832();

            return class_2561.method_43471("itemGroup." + groupId + "." + component + "." + componentName);
        }

    }
}
