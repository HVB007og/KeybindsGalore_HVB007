# Changelog

## 1.9.0 - UNRELEASED

Not yet published. Drafted so the changes are reviewable before the Modrinth and GitHub release.
Every branch already carries the version number, but nothing has been uploaded. Verification status
is recorded honestly at the bottom of this section.

### Added

- **A conflict report in the log.** Every conflict scan now writes a one-line summary, so a mod
  that detects nothing is never silently indistinguishable from a broken one. With `DEBUG` on it
  also lists every conflicting physical key, every action on it, the active filters and
  priorities, and marks which conflicts are already settled by a priority so they are not mistaken
  for live ones. This is how the middle mouse default above was found, and it is the tool to reach
  for when asking what a given modpack actually conflicts on. See `core/ConflictReport`.
- **Optional keyboard control for both menus.** Arrow keys, `WASD` or `Tab` move the highlight,
  `Enter` or `Space` confirm, `Escape` cancels. The new `KEYBOARD_CONTROL_MODE` option lives in the
  Behaviour tab and is **off by default**, so an existing config behaves exactly as it did before.
  With it on, the conflicting key also confirms, and the menu stays open when that key is
  released, so the same finger can open and close the menu. The mouse keeps working in both
  states; this is additive, not a separate mode that disables the mouse.
- **Screen reader narration** in both menus. Opening a menu announces the conflicting key and how
  many actions are in conflict, and moving the highlight announces the action and its position,
  for example "Jump, 2 of 3". The key-capture prompt announces that any key or mouse button will
  be captured, replacing vanilla's generic "use the mouse cursor or tab to select an element",
  which was misleading because there is nothing to select there.
- **Keyboard Control Mode is enabled automatically for narrator users.** If the narrator is set to
  All and the option is off, the mod turns it on the first time you join a world and says so in
  chat. A menu that cannot be operated by keyboard cannot usefully be narrated, so leaving that
  contradiction in place would have helped nobody. The new `NARRATOR_AUTO_ENABLE` option controls
  this and switches itself off after firing, so it never repeats.

### Fixed

- **Middle mouse no longer opens a conflict menu on every click.** Vanilla binds both `Pick Block`
  and Spectator's `Select On Hotbar` to middle mouse, so on a fresh install that key is a
  permanent conflict that cannot be rebound apart. `key.pickItem:key.mouse.middle` is now a
  default priority, so Pick Block simply wins and the menu stays out of the way. Pick Block was
  chosen over Select On Hotbar because it is used constantly in normal play while Select On Hotbar
  only matters in spectator mode. Previously this was the mod's most irritating first impression:
  one of only two keys that conflict on a completely default install.
- Keyboard navigation only half respected `KEYBOARD_CONTROL_MODE`. The arrow and `WASD` bindings
  were not gated on the option, so the menus could be driven by keyboard even with the option
  turned off. It now genuinely does nothing when off.
- Releasing the conflicting key always finalised the selection, so a menu closed the instant it
  opened when the key was let go. In keyboard control mode the release is now only recorded, and
  the menu stays up with the highlight intact.
- The pie menu flickered while the conflicting key was held. This was pre-existing and unrelated to
  keyboard control: the key auto-repeats while held, and each repeat built a fresh screen, so the
  menu was destroyed and rebuilt several times a second. A selector already showing that key is now
  left alone.
- An unrelated key pressed while a menu was open could fail, because a key-mapping helper returns
  nothing for keys that are not navigation keys and the result was passed straight into a
  `switch` on it.
- `Enter` marked the selection as finalised but never closed the menu, leaving it stuck open.
- Releasing a key that was not the conflicting key was reported as handled when it had not been.

### Changed

- `FILTERED_CATEGORY_KEYS` no longer needs the legacy `[Debug]` value. It was a 1.21.7-era default
  that no longer matches any 26.2+ category, so it silently filtered nothing while looking
  configured. Existing configs carrying it are harmless but dead, and `[]` is the honest value.
  Cleared from the development config used for testing.

### Known issues in this release

- Moving the highlight by keyboard still feels slightly heavy while a screen reader is narrating.
  The worst of it was fixed, by routing movement through the game's own throttled narration
  instead of interrupting the narrator on every key press, but it is not perfectly smooth.
- The mod's own config screen is not narrated. Cloth Config builds that screen and it is not
  covered. Fixing it needs a mixin into Cloth Config or a wrapper screen; neither is done.

### Verification status

- Built and 65/65 automated tests passing on all five branches.
- **Human-verified in game on 26.3 Fabric only.** Keyboard control, the contested-key confirm, and
  the automatic narrator enable were each confirmed by the maintainer on that build.
- **Not human-verified on 26.2 or on either NeoForge build.** The code is identical across
  branches, but nobody has pressed a key on those three.
- The `Tab` binding and the narration timing have no automated coverage.

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
