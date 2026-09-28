# Changelog

## 1.8.0+26.3-neoforge - Minecraft 26.3 (NeoForge)

The NeoForge build for Minecraft 26.3. **No mod behaviour changed**; this adds a loader, not
features, so the version number stays `1.8.0`.

Cheaper to port than expected, because it was based on the 26.3 Fabric branch rather than the
26.2 NeoForge one, so the SDL3 key handling and the `renderpearl` render pipeline carried over
unchanged. `RingRenderer` needed no edits at all. Only the loader surface changed:

- the entrypoint is a NeoForge `@Mod(dist = CLIENT)` class, and `ModMenuIntegration` was removed
  because NeoForge allows one `@Mod` class per mod id, so its config-screen registration moved
  into the main constructor
- key registration uses `RegisterKeyMappingsEvent`
- tick and connection events use the NeoForge equivalents
- the config directory uses `FMLPaths.CONFIGDIR`
- configuration loading moved to the first client tick, because `Minecraft.options` is still
  null during NeoForge mod construction
- `fabric.mod.json` is replaced by `META-INF/neoforge.mods.toml`

### Requirements

- Minecraft 26.3
- NeoForge 26.3.0.26-beta or newer
- Java 25 or newer
- Cloth Config 26.3.159 or newer

NeoForge publishes 26.3 only as beta at the time of writing, so this build tracks a moving
target.

## 1.8.0+26.3 - Minecraft 26.3 (Fabric)

Port of 1.8.0 to Minecraft 26.3 on Fabric. **No mod behaviour changed.** The pie menu, list
menu, priority system, K-key priority capture, live conflict re-scanning, per-category filters,
and the config screen all work exactly as they do on 26.2. The version number is unchanged
because nothing in the mod itself differs.

The work is entirely in adapting the mod to 26.3's client changes:

- The client moved from GLFW to SDL3, so the capture key and key handling use
  `InputConstants.Type.KEYBOARD` instead of the removed `KEYSYM`
- Unbound keys are detected by their negative key value rather than by scanning GLFW state
- `org.lwjgl.glfw` no longer exists, so its references were replaced with SDL3 equivalents
- Rendering moved to the newer `RenderPipeline` API in the `com.mojang.renderpearl` package;
  the pie menu is drawn through the GPU-batched ring renderer

### Requirements

- Minecraft 26.3
- Fabric Loader 0.19.3 or newer
- Fabric API 0.161.0+26.3 or newer
- Java 25 or newer
- Cloth Config 26.3.159 or newer
- ModMenu 21.0.0 or newer (optional, only for the settings screen)

The same mod is also published for 26.2 on Fabric and on NeoForge, under the same number.

### Verification status

Built and unit-tested, with 45 tests passing. Verified in game: pie menu, list menu, config
screen, **and K-key priority capture**, which was the open question because 26.3 moved the client
to SDL3. The capture path is confirmed working on both the Fabric and NeoForge 26.3 builds.

## 1.8.0+26.2 - Minecraft 26.2

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
