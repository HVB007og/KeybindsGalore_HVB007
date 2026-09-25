package net.hvb007.keybindsgalore.input.minecraft;

import com.mojang.blaze3d.platform.InputConstants;
import net.hvb007.keybindsgalore.core.BindingSnapshot;
import net.hvb007.keybindsgalore.core.ConflictGroup;
import net.hvb007.keybindsgalore.core.ConflictIndexBuilder;
import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class MinecraftConflictIndex {
    private final Map<InputConstants.Key, List<KeyMapping>> target;
    private final Function<KeyMapping[], List<MinecraftBindingCatalog.Entry>> collector;
    private final ConflictIndexBuilder builder = new ConflictIndexBuilder();
    private RefreshReason lastRefreshReason = RefreshReason.NONE;
    private long refreshCount;

    public MinecraftConflictIndex(Map<InputConstants.Key, List<KeyMapping>> target) {
        this(target, new MinecraftBindingCatalog()::collect);
    }

    MinecraftConflictIndex(
            Map<InputConstants.Key, List<KeyMapping>> target,
            Function<KeyMapping[], List<MinecraftBindingCatalog.Entry>> collector
    ) {
        this.target = Objects.requireNonNull(target, "target");
        this.collector = Objects.requireNonNull(collector, "collector");
    }

    public void refresh(KeyMapping[] keyMappings, List<String> filteredCategories, RefreshReason reason) {
        Objects.requireNonNull(keyMappings, "keyMappings");
        List<MinecraftBindingCatalog.Entry> entries = collector.apply(keyMappings);
        List<BindingSnapshot> snapshots = new ArrayList<>(entries.size());
        for (MinecraftBindingCatalog.Entry entry : entries) {
            snapshots.add(entry.snapshot());
        }

        target.clear();
        for (ConflictGroup group : builder.build(
                snapshots,
                filteredCategories == null ? List.of() : filteredCategories
        )) {
            List<KeyMapping> bindings = new ArrayList<>(group.bindingIndexes().size());
            for (int index : group.bindingIndexes()) {
                bindings.add(entries.get(index).mapping());
            }
            target.put(((KeyMappingAccessor) bindings.getFirst()).getKey(), List.copyOf(bindings));
        }
        lastRefreshReason = Objects.requireNonNull(reason, "reason");
        refreshCount++;
    }

    public RefreshReason lastRefreshReason() {
        return lastRefreshReason;
    }

    public long refreshCount() {
        return refreshCount;
    }

    public enum RefreshReason {
        NONE,
        STARTUP,
        WORLD_JOIN,
        KEYBINDS_SCREEN_CLOSED,
        CONFIG_SAVED,
        MANUAL
    }
}
