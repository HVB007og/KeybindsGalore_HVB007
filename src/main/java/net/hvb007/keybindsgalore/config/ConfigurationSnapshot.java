package net.hvb007.keybindsgalore.config;

import java.util.List;

public record ConfigurationSnapshot(
        boolean debug,
        boolean verboseDebug,
        boolean lazyConflictCheck,
        int circleVertices,
        boolean pieMenuBlend,
        boolean darkenedBackground,
        int darkenedBackgroundStrength,
        boolean labelTextShadow,
        boolean useCircularMenu,
        boolean useSoftwareRendering,
        boolean showConflictWarnings,
        boolean enableAttackWorkaround,
        List<String> filteredCategoryKeys,
        List<Integer> ignoredKeys,
        boolean invertIgnoredKeysList,
        boolean useKeybindFix,
        int pulseTimerDuration,
        List<String> priorityCategories,
        List<String> priorityKeybinds,
        float expansionFactorWhenSelected,
        int pieMenuMargin,
        float pieMenuScale,
        float cancelZoneScale,
        int pieMenuColor,
        int pieMenuSelectColor,
        int pieMenuHighlightColor,
        int pieMenuSectorColorEven,
        int pieMenuSectorColorOdd,
        int pieMenuSectorColorSelected,
        int pieMenuSectorColorLastOdd,
        int pieMenuCancelZoneColor,
        int pieMenuCancelZoneHoverColor,
        int pieMenuColorLightenFactor,
        short pieMenuAlpha,
        boolean sectorGradation,
        int labelTextInset,
        boolean animatePieMenu) {
    public ConfigurationSnapshot {
        filteredCategoryKeys = List.copyOf(filteredCategoryKeys);
        ignoredKeys = List.copyOf(ignoredKeys);
        priorityCategories = List.copyOf(priorityCategories);
        priorityKeybinds = List.copyOf(priorityKeybinds);
    }

    public static ConfigurationSnapshot defaults() {
        return new ConfigurationSnapshot(
                false,
                false,
                true,
                120,
                false,
                true,
                0x60,
                false,
                true,
                true,
                true,
                true,
                List.of(),
                List.of(340, 341, 87, 65, 83, 68, 32),
                false,
                true,
                5,
                List.of("Movement"),
                List.of(
                        "key.forward:key.keyboard.w",
                        "key.left:key.keyboard.a",
                        "key.back:key.keyboard.s",
                        "key.right:key.keyboard.d",
                        "key.jump:key.keyboard.space",
                        "key.sneak:key.keyboard.left.shift",
                        "key.sprint:key.keyboard.left.control"
                ),
                0.0f,
                0,
                0.8f,
                0.2f,
                0x00404040,
                0x00FFFFFF,
                0x00EED202,
                0xC0606060,
                0xC0808080,
                0xC0E0E0E0,
                0xC0A0A0A0,
                0xC0000000,
                0xC0B04232,
                0x191919,
                (short) 0x40,
                true,
                6,
                true
        );
    }
}
