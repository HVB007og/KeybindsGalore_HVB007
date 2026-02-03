package io.wispforest.owo.itemgroup.gui;

import io.wispforest.owo.Owo;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.OwoItemSettingsExtension;
import net.minecraft.class_1761;
import net.minecraft.class_2561;
import net.minecraft.class_2960;

/**
 * Represents a tab inside an {@link OwoItemGroup} that contains all items in the
 * passed {@code contentTag}. If you want to use {@link OwoItemSettingsExtension#tab(int)} to
 * define the contents, use {@code null} as the tag
 */
public record ItemGroupTab(
    Icon icon,
    class_2561 name,
    ContentSupplier contentSupplier,
    class_2960 texture,
    boolean primary
) implements OwoItemGroup.ButtonDefinition {

    public static final class_2960 DEFAULT_TEXTURE = Owo.id("textures/gui/tabs.png");

    @Override
    public class_2561 tooltip() {
        return this.name;
    }

    @FunctionalInterface
    public interface ContentSupplier {
        void addItems(class_1761.class_8128 context, class_1761.class_7704 entries);
    }
}
