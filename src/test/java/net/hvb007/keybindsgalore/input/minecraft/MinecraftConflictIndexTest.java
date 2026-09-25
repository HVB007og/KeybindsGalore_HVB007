package net.hvb007.keybindsgalore.input.minecraft;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MinecraftConflictIndexTest {
    @Test
    void recordsRefreshReasonAndClearsPreviousIndex() {
        Map<InputConstants.Key, List<KeyMapping>> target = new HashMap<>();
        target.put(InputConstants.Type.KEYSYM.getOrCreate(65), List.of());
        MinecraftConflictIndex index = new MinecraftConflictIndex(target, ignored -> List.of());

        index.refresh(new KeyMapping[0], List.of(), MinecraftConflictIndex.RefreshReason.STARTUP);
        assertTrue(target.isEmpty());
        assertEquals(MinecraftConflictIndex.RefreshReason.STARTUP, index.lastRefreshReason());
        assertEquals(1, index.refreshCount());

        index.refresh(new KeyMapping[0], List.of(), MinecraftConflictIndex.RefreshReason.CONFIG_SAVED);
        assertEquals(MinecraftConflictIndex.RefreshReason.CONFIG_SAVED, index.lastRefreshReason());
        assertEquals(2, index.refreshCount());
    }
}
