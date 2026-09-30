# Keybinds Galore

A client-side mod for Minecraft that resolves keybind conflicts instead of letting actions
silently override each other.

When two actions share a physical key, Minecraft picks one and ignores the other, usually
without telling you. Keybinds Galore intercepts that and asks: a selection menu opens, you
point at the action you meant, and the other bindings are released.

Originally created by Cael. Maintained for modern versions by HVB007.

## Get it

**[Modrinth](https://modrinth.com/mod/keybindsgalore+(hvb007))** is the source of truth for
which builds exist, which Minecraft versions they support, and which loaders they run on. It
lists every published version with its dependencies, so use it rather than this page to pick a
download.

The mod is published for **Fabric** and **NeoForge**, for several Minecraft versions. Every
combination is a separate published version, and each has its own branch in this repository.

Client-side only. It does nothing on a dedicated server.

## Features

- **Conflict selection.** Press a contested key and a menu opens with every action bound to
  it. Choose one and it runs; the rest are released for that press.
- **Pie menu and list menu.** The pie is the default. The list is a plain vertical menu,
  selectable with `USE_CIRCULAR_MENU=false` in the config.
- **Priority system.** For keys you never want to be asked about. Set `Space` to always jump,
  permanently, and it will jump even when a mod binds something else to it.
- **K-key priority capture.** Press `K`, then the key you want to prioritise, then pick the
  action. No file editing.
- **Live conflict detection.** Conflicts are re-scanned on startup, on joining a world, when
  you close the vanilla Controls screen, when you save config, and when priorities change.
- **Per-category filters.** Exclude whole categories from conflict detection if you never want
  to be asked about them.
- **Mouse support.** Works with conflicting mouse button bindings, not just keyboard.
- **Keyboard control.** Optionally drive either menu without a mouse: arrow keys, `WASD` or `Tab`
  move the highlight, `Enter` or `Space` confirm, `Escape` cancels. With the mode on, the
  conflicting key itself also confirms, so the same finger that opened the menu can close it. The
  mouse keeps working either way. It is **off by default** in **Behaviour**, so existing configs
  behave exactly as they did before. If you use a screen reader with the narrator set to All, the
  mod turns this on for you once and says so in chat.
- **Screen reader narration.** Both menus announce the conflicting key, the number of options, and
  which action is highlighted and where it sits in the list.
- **In-game config.** A full settings screen through Cloth Config, so nothing requires editing
  a text file. On Fabric it appears in ModMenu; on NeoForge it appears in the mod list.
- **Public API.** A small versioned API lets other mods register their actions so their
  bindings participate in conflict detection.

## Using the priority system

If `Jump` and a mod's action share `Space`, the menu opens every time. To make `Jump` always
win instead:

1. Press `K` in game.
2. Press the key you want to lock down, for example `Space`.
3. Pick the action that should always win, for example `Jump`.

That key now runs `Jump` immediately and never opens the menu. Use **Remove Current Priority**
in the same capture flow to undo it, or manage the whole list in the config screen.

A priority can be set per action (`key.jump:key.keyboard.space`) or per category (`Movement`
covers every movement action). An action priority always beats a category one.

## Configuration

Settings live in `config/keybindsgalore.properties`. The in-game screen edits the same file, so
you can use either.

The four tabs:

- **General** — debug logging, pulse duration, attack workaround, background dimming.
- **Behaviour** — pie or list menu, keyboard control, conflict warnings, category filters,
  priorities.
- **Visual (Pie Menu)** — geometry, colours, gradient, animation, labels.
- **Visual (List Menu)** — list colours and opacity. These affect the list menu only.

Every option is either wired to runtime or deliberately retired. Retired keys are stripped from
your config file automatically on load, and each is listed at the bottom of the properties file
with what replaced it.

Colours are entered as ARGB hex values such as `C0606060`. The Cloth Config version this mod
uses provides a hex text field rather than an RGB slider widget.

## Known limitations

- The mod's own config screen is not narrated. The selector menus and the key-capture prompt are,
  but Cloth Config builds that screen and is not covered, so navigating options there is still
  silent. It needs either a mixin into Cloth Config or a wrapper screen, and it is not done.
- Moving the highlight with the keyboard can feel slightly heavy when a screen reader is
  narrating. It is noticeably better than before, but not perfectly smooth.
- Long action names on neighbouring wedges can overlap. Labels are clamped so they are never cut
  off by the screen edge, but they cannot reflow.
- The pie menu does not pause the game. This is intentional, so you can see the world behind it.
- Colour inputs are hex text fields, not pickers.
- Bindings changed by another mod at runtime are not detected; only the triggers listed above
  cause a rescan.

## Building from source

Each Minecraft version and loader combination lives on its own branch. The default branch is one
working build, so cloning gives you something that compiles; switch branches for the others.

```text
git clone https://github.com/HVB007og/KeybindsGalore_HVB007
cd KeybindsGalore_HVB007
.\gradlew.bat build
```

Requires a Java 25 JDK. The output jar lands in `build/libs`. `.\gradlew.bat runClient` launches
a development client, and `.\gradlew.bat test` runs the unit tests, which cover the conflict
rules, priority resolution, pie geometry, configuration parsing and migration, and the public
API. Input handling that needs a running Minecraft is verified manually.

Branches are named `recovery/<minecraft>-<loader>`, for example `recovery/26.3-fabric` or
`recovery/26.3-neoforge`.

## License

GNU Lesser General Public License v3.0. See [LICENSE](LICENSE).
