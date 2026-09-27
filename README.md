# Keybinds Galore

A client-side mod for Minecraft 26.2 that resolves keybind conflicts instead of
letting actions silently override each other.

When two actions share a physical key, Minecraft picks one and ignores the other, usually
without telling you. Keybinds Galore intercepts that and asks: a selection menu opens, you
point at the action you meant, and the other bindings are released.

Originally created by Cael. Maintained for modern versions by HVB007.

## Features

- **Conflict selection.** Press a contested key and a menu opens with every action bound to
  it. Choose one and it runs; the rest are released for that press.
- **Pie menu and list menu.** The pie is the default. The list is a plain vertical menu,
  selectable with `USE_CIRCULAR_MENU=false` in the config.
- **Priority system.** For keys you never want to be asked about. Set `Space` to always
  jump, permanently, and it will jump even when a mod binds something else to it.
- **K-key priority capture.** Press `K`, then the key you want to prioritise, then pick the
  action. No file editing.
- **Live conflict detection.** Conflicts are re-scanned on startup, on joining a world, when
  you close the vanilla Controls screen, when you save config, and when priorities change.
- **Per-category filters.** Exclude whole categories from conflict detection if you never
  want to be asked about them.
- **In-game config.** Full settings screen through Cloth Config. Nothing requires editing a
  text file. On Fabric this appears under ModMenu.
- **Public API.** A small versioned API lets other mods register their actions so their
  bindings participate in conflict detection.

## Requirements

- Minecraft 26.2
- Java 25 or newer
- Cloth Config 26.2.155 or newer

This build is the **NeoForge** edition. The Fabric edition is published separately as
`1.8.0+26.2` and needs Fabric Loader, Fabric API, and optionally ModMenu; the NeoForge
edition needs none of those.

- NeoForge 26.2.0.88 or newer

Client-side only. Not usable on a dedicated server.

## Using the priority system

If `Jump` and a mod's action share `Space`, the menu opens every time. To make `Jump` always
win instead:

1. Press `K` in game.
2. Press the key you want to lock down, for example `Space`.
3. Pick the action that should always win, for example `Jump`.

That key now runs `Jump` immediately and never opens the menu. Use **Remove Current
Priority** in the same capture flow to undo it, or manage the whole list in the config
screen.

A priority can be set per action (`key.jump:key.keyboard.space`) or per category
(`Movement` covers every movement action). An action priority always beats a category one.

## Configuration

Settings live at `run/config/keybindsgalore.properties` in a development environment, and
`config/keybindsgalore.properties` in a normal install. The in-game screen edits the same
file, so you can use either.

The four tabs:

- **General** — debug logging, pulse duration, attack workaround, background dimming.
- **Behaviour** — pie or list menu, conflict warnings, category filters, priorities.
- **Visual (Pie Menu)** — geometry, colours, gradient, animation, labels.
- **Visual (List Menu)** — list colours and opacity. These affect the list menu only.

Every option is either wired to runtime or deliberately retired. Retired keys are stripped
from your config file automatically on load, and each is listed at the bottom of the
properties file with what replaced it.

Colours are entered as ARGB hex values such as `C0606060`. The Cloth Config version this mod
uses provides a hex text field rather than an RGB slider widget.

## Notes and known limitations

- The pie menu is **mouse-only**. There is no keyboard navigation yet, which is the largest
  accessibility gap.
- Long action names on neighbouring wedges can overlap. Labels are clamped so they are never
  cut off by the screen edge, but they cannot reflow.
- The pie menu does not pause the game. This is intentional, so you can see the world behind
  it.
- Colour inputs are hex text fields, not pickers.
- Bindings changed by another mod at runtime are not detected; only the triggers listed above
  cause a rescan.

## Building from source

```text
git clone https://github.com/HVB007og/KeybindsGalore_HVB007
cd KeybindsGalore_HVB007
.\gradlew.bat build
```

Requires a Java 25 JDK. The output jar lands in `build/libs`. `.\gradlew.bat runClient`
launches a development client.

`.\gradlew.bat test` runs the unit tests, which cover the conflict rules, priority
resolution, pie geometry, configuration parsing and migration, and the public API. Input
handling that needs a running Minecraft is verified manually.

## License

GNU Lesser General Public License v3.0. See [LICENSE](LICENSE).
