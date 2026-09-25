package net.hvb007.keybindsgalore.input.minecraft;

import com.mojang.blaze3d.platform.InputConstants;
import net.hvb007.keybindsgalore.core.BindingSnapshot;
import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class MinecraftBindingCatalog {
    public List<Entry> collect(KeyMapping[] keyMappings) {
        List<Entry> entries = new ArrayList<>(keyMappings.length);
        for (KeyMapping binding : keyMappings) {
            InputConstants.Key physicalKey = ((KeyMappingAccessor) binding).getKey();
            if (physicalKey.getValue() == GLFW.GLFW_KEY_UNKNOWN) {
                continue;
            }

            entries.add(new Entry(
                    binding,
                    new BindingSnapshot(
                            physicalKey.getName(),
                            actionId(binding),
                            categoryId(binding),
                            categoryLabel(binding)
                    )
            ));
        }
        return List.copyOf(entries);
    }

    public static String actionId(KeyMapping binding) {
        return binding.getName();
    }

    public static String categoryId(KeyMapping binding) {
        return binding.getCategory().id().toString();
    }

    public static String categoryLabel(KeyMapping binding) {
        return binding.getCategory().label().getString();
    }

    public record Entry(KeyMapping mapping, BindingSnapshot snapshot) {
    }
}
