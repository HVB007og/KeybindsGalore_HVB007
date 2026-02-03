package io.wispforest.owo.itemgroup.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import io.wispforest.owo.moddata.ModDataConsumer;
import io.wispforest.owo.util.pond.OwoItemExtensions;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.class_1761;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_6862;
import net.minecraft.class_7706;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages loading and adding JSON-based tabs to preexisting {@code ItemGroup}s
 * without needing to depend on owo
 * <p>
 * This is used instead of a {@link net.minecraft.class_4309} because
 * it needs to load on the client
 */
@ApiStatus.Internal
public class OwoItemGroupLoader implements ModDataConsumer {

    public static final OwoItemGroupLoader INSTANCE = new OwoItemGroupLoader();

    private static final Map<class_2960, JsonObject> BUFFERED_GROUPS = new HashMap<>();

    private OwoItemGroupLoader() {}

    public static void onGroupCreated(class_1761 group) {
        var groupId = class_7923.field_44687.method_10221(group);

        if (!BUFFERED_GROUPS.containsKey(groupId)) return;
        INSTANCE.acceptParsedFile(groupId, BUFFERED_GROUPS.remove(groupId));
    }

    @Override
    public void acceptParsedFile(class_2960 id, JsonObject json) {
        var targetGroupId = class_2960.method_60654(class_3518.method_15265(json, "target_group"));

        class_1761 searchGroup = null;
        for (class_1761 group : class_7706.method_47341()) {
            if (class_7923.field_44687.method_10221(group).equals(targetGroupId)) {
                searchGroup = group;
                break;
            }
        }

        if (searchGroup == null) {
            BUFFERED_GROUPS.put(targetGroupId, json);
            return;
        }

        final var targetGroup = searchGroup;

        var tabsArray = class_3518.method_15292(json, "tabs", new JsonArray());
        var tabs = new ArrayList<ItemGroupTab>();

        tabsArray.forEach(jsonElement -> {
            if (!jsonElement.isJsonObject()) return;
            var tabObject = jsonElement.getAsJsonObject();

            var texture = class_2960.method_60654(class_3518.method_15253(tabObject, "texture", ItemGroupTab.DEFAULT_TEXTURE.toString()));

            var tag = class_6862.method_40092(class_7924.field_41197, class_2960.method_60654(class_3518.method_15265(tabObject, "tag")));
            var icon = class_7923.field_41178.method_63535(class_2960.method_60654(class_3518.method_15265(tabObject, "icon")));
            var name = class_3518.method_15265(tabObject, "name");

            tabs.add(new ItemGroupTab(
                    Icon.of(icon),
                    OwoItemGroup.ButtonDefinition.tooltipFor(targetGroup, "tab", name),
                    (context, entries) -> class_7923.field_41178.method_10220().filter(item -> item.method_40131().method_40220(tag)).forEach(entries::method_45421),
                    texture,
                    false
            ));
        });

        var buttonsArray = class_3518.method_15292(json, "buttons", new JsonArray());
        var buttons = new ArrayList<ItemGroupButton>();

        buttonsArray.forEach(jsonElement -> {
            if (!jsonElement.isJsonObject()) return;
            var buttonObject = jsonElement.getAsJsonObject();

            String link = class_3518.method_15265(buttonObject, "link");
            String name = class_3518.method_15265(buttonObject, "name");

            int u = class_3518.method_15260(buttonObject, "texture_u");
            int v = class_3518.method_15260(buttonObject, "texture_v");

            int textureWidth = class_3518.method_15282(buttonObject, "texture_width", 64);
            int textureHeight = class_3518.method_15282(buttonObject, "texture_height", 64);

            final var textureId = class_3518.method_15253(buttonObject, "texture", null);
            var texture = textureId == null
                    ? ItemGroupButton.ICONS_TEXTURE
                    : class_2960.method_60654(textureId);

            buttons.add(ItemGroupButton.link(targetGroup, Icon.of(texture, u, v, textureWidth, textureHeight), name, link));
        });

        if (targetGroup instanceof WrapperGroup wrapper) {
            wrapper.addTabs(tabs);
            wrapper.addButtons(buttons);

            if (class_3518.method_15258(json, "extend", false)) wrapper.markExtension();
        } else {
            var wrapper = new WrapperGroup(targetGroup, targetGroupId, tabs, buttons);
            wrapper.initialize();
            if (class_3518.method_15258(json, "extend", false)) wrapper.markExtension();

            class_7923.field_41178.method_10220()
                    .filter(item -> ((OwoItemExtensions) item).owo$group() == targetGroup)
                    .forEach(item -> ((OwoItemExtensions) item).owo$setGroup(wrapper));
        }
    }

    @Override
    public String getDataSubdirectory() {
        return "item_group_tabs";
    }

    static {
        RegistryEntryAddedCallback.event(class_7923.field_44687).register((rawId, id, group) -> {
            OwoItemGroupLoader.onGroupCreated(group);
        });
    }

}
