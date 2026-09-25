package net.hvb007.keybindsgalore.ui.minecraft;

import net.hvb007.keybindsgalore.KeybindManager;
import net.hvb007.keybindsgalore.customdata.DataManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ConflictActionPresentation {
    private final List<KeyMapping> actions;
    private final DataManager customData;

    public ConflictActionPresentation(List<KeyMapping> actions, DataManager customData) {
        this.actions = List.copyOf(actions);
        this.customData = customData;
    }

    public int size() {
        return actions.size();
    }

    public KeyMapping action(int index) {
        return actions.get(index);
    }

    public List<String> labels() {
        List<String> labels = new ArrayList<>(actions.size());
        for (int index = 0; index < actions.size(); index++) {
            labels.add(label(index));
        }
        return labels;
    }

    public String label(int index) {
        KeyMapping action = actions.get(index);
        String id = KeybindManager.safeGetTranslationKey(action);
        String category = KeybindManager.safeGetCategoryLabel(action);
        String name = category + ": " + Component.translatable(id).getString();
        if (customData != null && customData.hasCustomData) {
            try {
                if (customData.customData.get(id).hideCategory) {
                    name = Component.translatable(id).getString();
                }
                name = Objects.requireNonNull(customData.customData.get(id).displayName);
            } catch (Exception ignored) {
            }
        }
        return name;
    }

    public int color(int index, int fallback) {
        KeyMapping action = actions.get(index);
        String id = KeybindManager.safeGetTranslationKey(action);
        if (customData != null && customData.hasCustomData) {
            try {
                return customData.customData.get(id).sectorColor;
            } catch (Exception ignored) {
            }
        }
        return fallback;
    }
}
