# Changelog

## 1.8.0+26.2-neoforge — Minecraft 26.2 (NeoForge)

**First NeoForge release.** This is the same mod as the Fabric `1.8.0+26.2` build, ported to
NeoForge. Behaviour is unchanged: the pie menu, the list menu, the priority system, K-key
priority capture, live conflict re-scanning, per-category filters, and the in-game config
screen all work the same way.

The Fabric edition is published separately and keeps its own version number. The two are
maintained on separate branches and are versioned independently.

### Notes

- Requires NeoForge 26.2.0.88 or newer, Java 25, and Cloth Config 26.2.155.
- ModMenu is not used on NeoForge. The config screen is contributed through NeoForge's own
  settings extension point, so it appears in the Mods list instead.
- One harmless deprecation warning appears at startup, from Cloth Config 26.2.155 itself. It
  is upstream and already fixed in the 26.3 build of Cloth Config.

## 1.8.0+26.2 — Minecraft 26.2 (Fabric)

**The config screen now works on 26.2.** The previous 26.2 build shipped with a known issue
where the ModMenu config screen failed, because the Cloth Config release available at the
time referenced a Minecraft class that no longer exists. Use the in-game settings screen
instead of editing `keybindsgalore.properties` by hand.

Beyond that, conflict detection was rebuilt around a single canonical index, and the pie
menu was substantially reworked.

### Fixed

- Rebuilt conflict detection around a single canonical index. The conflict table is now
  produced in one place instead of being assembled by scattered callers.
- Conflict state is re-scanned on startup, on joining a world, when the vanilla Controls
  screen closes, when config is saved, and when priorities change. Previously a rebind done
  outside those moments could go unnoticed.
- The `K` capture key now opens reliably. A direct edge-triggered path runs alongside the
  previous poll-based one, so the first press is no longer sometimes swallowed.
- Pie menu labels are positioned radially and clamped to the screen. They were previously
  nudged in screen space, which pushed the label on a bottom wedge upward into the pie.
- Pie smoothness now divides its vertex budget across the wedges, so lowering
  `CIRCLE_VERTICES` no longer only visibly degrades the cancel circle.

### Added

- **Sector gradation** with a `GRADATION_INTENSITY` slider. Each wedge shades from a
  lighter inner edge to its outer colour.
- **Open animation** with an `ANIMATION_DURATION` slider, 0 to 1000 ms. Labels appear when
  it finishes. Closing stays immediate so input is never delayed.
- **Darken strength** slider for the pie menu background.
- **Selected sector expansion** actually applies, and defaults to a subtle `0.06`.
- **Label text shadow** now follows its setting instead of being hardcoded on.
- **Visual (List Menu)** tab. The list menu's colours and opacity were previously in the pie
  tab, where they had no effect and looked broken.
- Public API for other mods to register their actions, versioned via `ApiVersion`.

### Changed

- Config is read and written through a typed snapshot with atomic file replacement, instead
  of reflection over static fields.
- Colours use a real colour field with an alpha channel. Bounded numbers use sliders.
- Retired keys are stripped from existing config files on load, so upgrading no longer
  produces "no matching config field" errors.

### Removed

Nine configuration options were retired because they were unread, redundant, or 1.21.x
leftovers. Each is listed in `keybindsgalore.properties` with its replacement, and old
values are cleaned up automatically:

`PIE_MENU_COLOR_LIGHTEN_FACTOR`, `PIE_MENU_SELECT_COLOR`, `PIE_MENU_BLEND`,
`LABEL_TEXT_INSET`, `IGNORED_KEYS`, `INVERT_IGNORED_KEYS_LIST`,
`USE_SOFTWARE_RENDERING`, `LAZY_CONFLICT_CHECK`, `USE_KEYBIND_FIX`

- `FILTERED_CATEGORY_KEYS` now defaults to empty. The old default was `Debug`, which does
  not match any category in 26.2 and therefore did nothing.

### Requirements

- Minecraft 26.2
- Fabric Loader 0.19.3+
- Fabric API 0.152.1+26.2+
- Java 25+
- Cloth Config 26.2.155+
- ModMenu 20.0.2+ (optional)

### Known limitations

- The pie menu has no keyboard navigation.
- Neighbouring wedge labels can overlap when action names are long.
- Colour inputs are hex fields, not RGB pickers.
- Runtime rebinds performed by other mods are not detected.
