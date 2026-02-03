package io.wispforest.owo.itemgroup.gui;

import io.wispforest.owo.Owo;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import net.minecraft.class_156;
import net.minecraft.class_1761;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_407;

/**
 * A button placed to the right side of the creative inventory. Provides defaults
 * for linking to sites, but can execute arbitrary actions
 */
public final class ItemGroupButton implements OwoItemGroup.ButtonDefinition {

    public static final class_2960 ICONS_TEXTURE = Owo.id("textures/gui/icons.png");

    private final Icon icon;
    private final class_2561 tooltip;
    private final class_2960 texture;
    private final Runnable action;

    public ItemGroupButton(class_1761 group, Icon icon, String name, class_2960 texture, Runnable action) {
        this.icon = icon;
        this.tooltip = OwoItemGroup.ButtonDefinition.tooltipFor(group, "button", name);
        this.action = action;
        this.texture = texture;
    }

    public ItemGroupButton(class_1761 group, Icon icon, String name, Runnable action) {
        this(group, icon, name, ItemGroupTab.DEFAULT_TEXTURE, action);
    }

    public static ItemGroupButton github(class_1761 group, String url) {
        return link(group, Icon.of(ICONS_TEXTURE, 0, 0, 64, 64), "github", url);
    }

    public static ItemGroupButton modrinth(class_1761 group, String url) {
        return link(group, Icon.of(ICONS_TEXTURE, 16, 0, 64, 64), "modrinth", url);
    }

    public static ItemGroupButton curseforge(class_1761 group, String url) {
        return link(group, Icon.of(ICONS_TEXTURE, 32, 0, 64, 64), "curseforge", url);
    }

    public static ItemGroupButton discord(class_1761 group, String url) {
        return link(group, Icon.of(ICONS_TEXTURE, 48, 0, 64, 64), "discord", url);
    }

    /**
     * Creates a button that opens the given link when clicked
     *
     * @param icon The icon for this button to use
     * @param name The name of this button, used for the translation key
     * @param url  The url to open
     * @return The created button
     */
    public static ItemGroupButton link(class_1761 group, Icon icon, String name, String url) {
        return new ItemGroupButton(group, icon, name, () -> {
            final var client = class_310.method_1551();
            var screen = client.field_1755;
            client.method_1507(new class_407(confirmed -> {
                if (confirmed) class_156.method_668().method_670(url);
                client.method_1507(screen);
            }, url, true));
        });
    }

    @Override
    public class_2960 texture() {
        return this.texture;
    }

    @Override
    public Icon icon() {
        return this.icon;
    }

    @Override
    public class_2561 tooltip() {
        return this.tooltip;
    }

    public Runnable action() {
        return this.action;
    }

}
