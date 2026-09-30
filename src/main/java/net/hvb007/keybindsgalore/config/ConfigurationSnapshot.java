package net.hvb007.keybindsgalore.config;

import java.util.List;

public record ConfigurationSnapshot(
        boolean debug,
        boolean verboseDebug,
        int circleVertices,
        boolean darkenedBackground,
        int darkenedBackgroundStrength,
        boolean labelTextShadow,
        boolean useCircularMenu,
        boolean keyboardControlMode,
        boolean narratorAutoEnable,
        boolean showConflictWarnings,
        boolean enableAttackWorkaround,
        List<String> filteredCategoryKeys,
        int pulseTimerDuration,
        List<String> priorityCategories,
        List<String> priorityKeybinds,
        float expansionFactorWhenSelected,
        int pieMenuMargin,
        float pieMenuScale,
        float cancelZoneScale,
        int pieMenuColor,
        int pieMenuHighlightColor,
        int pieMenuSectorColorEven,
        int pieMenuSectorColorOdd,
        int pieMenuSectorColorSelected,
        int pieMenuSectorColorLastOdd,
        int pieMenuCancelZoneColor,
        int pieMenuCancelZoneHoverColor,
        short pieMenuAlpha,
        boolean sectorGradation,
        int gradationIntensity,
        boolean animatePieMenu,
        int animationDuration) {
    public ConfigurationSnapshot {
        filteredCategoryKeys = List.copyOf(filteredCategoryKeys);
        priorityCategories = List.copyOf(priorityCategories);
        priorityKeybinds = List.copyOf(priorityKeybinds);
    }

    public static ConfigurationSnapshot defaults() {
        return new ConfigurationSnapshot(
                false,
                false,
                120,
                true,
                0x60,
                false,
                true,
                // keyboardControlMode defaults to false so existing configs behave exactly as
                // they did before keyboard navigation existed. Opting in is a deliberate choice.
                false,
                // narratorAutoEnable defaults to true so a screen-reader user gets a menu they can
                // actually operate. It turns itself off after firing, so it never nags twice.
                true,
                true,
                true,
                List.of(),
                5,
                List.of("Movement"),
                List.of(
                        "key.forward:key.keyboard.w",
                        "key.left:key.keyboard.a",
                        "key.back:key.keyboard.s",
                        "key.right:key.keyboard.d",
                        "key.jump:key.keyboard.space",
                        "key.sneak:key.keyboard.left.shift",
                        "key.sprint:key.keyboard.left.control",
                        // Vanilla binds both Pick Block and Spectator's Select On Hotbar to middle
                        // mouse, so this is a permanent conflict on a fresh install with no way to
                        // separate the two. Pick Block wins because it is used constantly in normal
                        // play, while Select On Hotbar only matters in spectator mode. Without this
                        // the middle mouse menu opens on every single middle click, which is the
                        // mod's worst possible first impression.
                        "key.pickItem:key.mouse.middle"
                ),
                0.06f,
                0,
                0.8f,
                0.2f,
                0x00404040,
                0x00EED202,
                0xC0606060,
                0xC0808080,
                0xC0E0E0E0,
                0xC0A0A0A0,
                0xC0000000,
                0xC0B04232,
                (short) 0x40,
                true,
                30,
                true,
                180
        );
    }
}
