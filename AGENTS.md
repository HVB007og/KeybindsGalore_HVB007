# Keybinds Galore agent memory

## Repository status

Client-side mod for Minecraft keybinds conflict resolution. One codebase, four published
branches plus `master`, no multi-loader shared source sets. Every branch is pushed and clean.

**`master` is the default branch and is a real, buildable target, not a landing page.** It tracks
the 26.3 Fabric build. Its README is deliberately **version-free and loader-agnostic**: it names
no Minecraft version, no loader version, no mod version, and carries no version or branch table.
It points at Modrinth for the version matrix instead.

Do not add a version list, branch table, or requirements table to the README. A README that
enumerates versions goes stale the moment a new one ships, which is exactly how `master` ended
up describing 1.21.7 while every current build lived elsewhere. GeckoLib, which supports three
loaders across 24 branches, works the same way. When a new version ships: add a branch, publish
to Modrinth, leave the README alone.

| Branch | Target | Loader | Version | State |
|---|---|---|---|---|
| `master` | 26.3 | Fabric | `1.8.0+26.3` | **default branch**, a working build, version-free README |
| `recovery/26.2` | 26.2 | Fabric | `1.8.0+26.2` | published, human-verified |
| `recovery/26.2-neoforge` | 26.2 | NeoForge | `1.8.0+26.2-neoforge` | published, human-verified |
| `recovery/26.3-fabric` | 26.3 | Fabric | `1.8.0+26.3` | published, human-verified |
| `recovery/26.3-neoforge` | 26.3 | NeoForge | `1.8.0+26.3-neoforge` | published, human-verified |

Published Modrinth version ids, in case one has to be corrected later: 26.2 fabric `AtKZQVfW`,
26.2 NeoForge `OmOcyOGS`, 26.3 Fabric `zCLq83OM`, 26.3 NeoForge `1IxMtn5z`.

**All four current releases are published and human-verified in game**, on every combination of
26.2/26.3 and Fabric/NeoForge: pie menu, list menu, K-key priority capture, priority resolution,
and the config screen. There is no known untested path left on any branch.

- `AGENTS.md` and `.opencode/` are **local agent memory and stay untracked**. Never commit them.
- `ROADMAP.md`, `AUTONOMOUS_PLAN.md`, `RELEASE_CHECKLIST.md`, `prompts/`, and `bkpjar/README.md`
  plus `bkpjar/SHA256SUMS.txt` are tracked project documentation.
- `bkpjar/<version>/` holds the mod jar and sources jar for every published build. The jars are
  git-ignored; regenerate `SHA256SUMS.txt` on every release and re-verify every entry.
- Each Minecraft version and loader combination is a **separate published Modrinth version**
  under one project, all sharing the mod version `1.8.0`. The Minecraft version and loader are
  separate fields on Modrinth, not part of the number. `1.8.0+26.3` is a jar filename, not a
  Modrinth version.
- `run/` and `build/` are **shared across branches**. This is a known trap: after switching
  branches, leftover `build/moddev` (NeoForge) or `build/loom-cache` (Fabric) and a `run/`
  directory written by the other loader make it look like the build is broken. If a run launches
  the wrong loader, check the log for `net.neoforged.fml` versus `FabricLoader` before
  suspecting the code, and clear `build/` after switching loader branches.
- Never treat `jars/` as evidence of anything; it holds stale 1.21.1 artifacts.

## Product context

- KeybindsGalore is an accessible, conflict-safe control center for Minecraft keybinds, not only
  a pie-menu visualizer.
- The user is not a software developer. Explain each step plainly, make routine decisions
  autonomously, ask all questions up front in one message, and keep working to completion.
- The user permits committing and pushing untested code as a backup, but **reports must stay
  honest**: distinguish *built*, *tests passed*, *client launched*, *mixins applied*, and
  *a human played it*. Only the last one means it works.
- Do not hide configuration options. Unimplemented options are wired or labelled honestly.
- P0 trustworthiness is done. P1 is Conflict Inspector, colour picker, profiles, context rules,
  keyboard navigation and accessibility, resize-safe selectors. P2 is JSON config migration,
  integrations, compatibility testing, synchronized releases.
- External integrations need explicit source-owned callbacks or public registration surfaces.
  Create has no generic action callback, Controlify has a public API, Amecs is optional,
  MiniHUD/MaLiLib callbacks are private and out of scope.

## Before changing code

Read `.editorconfig`, `gradle.properties`, `build.gradle`, `settings.gradle`, the loader metadata
(`src/main/resources/fabric.mod.json` or `src/main/resources/META-INF/neoforge.mods.toml`), and
`CHANGELOG_LATEST.md`.

## Toolchains

Java 25 at `C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.0.1\jbr`. Gradle wrapper `9.5.1`.
`gradle.properties` must never regain a machine-specific `org.gradle.java.home`.

```powershell
attrib.exe -R "C:\Users\HVB\Desktop\Projects\MC Mod\KeybindsGalore_HVB007\build\*" /S /D
.\gradlew.bat build
.\gradlew.bat test
.\gradlew.bat runClient
```

The `attrib.exe` line is mandatory, not optional. Loom and ModDevGradle both write read-only
files into `build/`, and a later run fails to clean them otherwise.

First setup on a new Minecraft version downloads and decompiles the game, which takes 15-40
minutes. Run it in the background and poll rather than blocking.

## Build configuration per loader

**Fabric (Loom).** `id 'net.fabricmc.fabric-loom' version "${loom_version}"`, with
`loom_version`, `loader_version`, `fabric_api_version`, `modmenu_version` in `gradle.properties`.
Cloth artifact is `cloth-config-fabric`.

**NeoForge (ModDevGradle).** `id 'net.neoforged.moddev' version "${moddevgradle_version}"`, with
`neoforge_version` and `cloth_version`. Cloth artifact is `cloth-config-neoforge`. Requires three
non-obvious settings that cost real time to discover:

- `neoForge { mods { keybindsgalore { sourceSet sourceSets.main } } }`
- `addModdingDependenciesTo sourceSets.test`, or tests that touch `KeyMapping` fail to compile
- `foojay-resolver-convention` **1.0.0** in `settings.gradle`; 0.9.0 references
  `JvmVendorSpec.IBM_SEMERU`, which Gradle 9 removed, and the run tasks then fail looking for a
  Java 21 toolchain. The `plugins {}` block must come **after** `pluginManagement {}`.

Current versions: NeoForge 26.2 is `26.2.0.88`, the last non-beta build of that line. NeoForge
26.3 exists **only as beta**, newest `26.3.0.26-beta`, which is what the 26.3 NeoForge branch
targets. Check for a newer beta before building; a new beta can change the render or input API.

**Port the newer Minecraft version first when both loaders are needed.** The 26.3 NeoForge port
was far cheaper than the 26.2 one because it was based on the 26.3 Fabric branch, so the SDL3 and
`renderpearl` work carried over and only the loader surface changed. Starting from 26.2 and
porting both axes at once would have meant doing that work twice.

## Loader mapping

Only the loader-facing surface differs. The conflict engine, config model, and both selector
screens are pure Minecraft or pure Java and port unchanged.

| Fabric | NeoForge |
|---|---|
| `ClientModInitializer` | `@Mod(dist = Dist.CLIENT)` constructor |
| `KeyMappingHelper.registerKeyMapping` | `RegisterKeyMappingsEvent` |
| `ClientTickEvents.END_CLIENT_TICK` | `ClientTickEvent.Post` |
| `ClientPlayConnectionEvents` | `ClientPlayerNetworkEvent.LoggingIn/LoggingOut` |
| `FabricLoader.getConfigDir()` | `FMLPaths.CONFIGDIR.get()` |
| ModMenu `IConfigScreenFactory` entrypoint | `ModContainer.registerExtensionPoint` |
| `fabric.mod.json` | `META-INF/neoforge.mods.toml` |
| Loom | ModDevGradle |

**NeoForge allows exactly one `@Mod` class per mod id.** ModMenu's single job moved into the
main `KeybindsGalore` constructor; `ModMenuIntegration.java` was deleted rather than ported.

Config loading happens on the first client tick, not in the constructor, because
`Minecraft.options` is null during NeoForge mod construction.

## Source map

- `KeybindsGalore.java`: entrypoint, capture key, lifecycle handlers, pulse target, global managers.
- `KeybindManager.java`: conflict table, priority migration/resolution, input interception, persistence.
- `KeybindSelectorScreen.java`: list selector.
- `KeybindCircularScreen.java`: pie selector, animation, gradient, labels.
- `RingRenderer.java`: GPU-batched pie renderer, the only pie drawing path.
- `ui/model/`: pure `ConflictListLayout`, `CircularMenuGeometry`, `ConflictSelectionModel`, and
  `ConflictInputActions`, which maps four abstract actions (move previous, move next, commit,
  cancel) onto the selection model. Minecraft-free and unit-tested, so navigation behaviour is
  verifiable without launching the game.
- `SelectorKeyBindings.java`: physical key to action mapping. 26.3 stores SDL scancodes and the
  constant names changed with them, so the main Enter key is `KEY_RETURN` rather than
  `KEY_ENTER`, and the keypad one is `KEY_NUMPADENTER`.
- `SelectionNarration.java`: speaks the focused action using the same custom label the screen
  draws, and supplies `updateNarrationState` output for both selectors.
- **Both selectors carry a `keyboardFocus` flag and it is load-bearing.** The hover pass runs
  every frame, so without it the selection is wiped the moment the mouse sits outside a wedge.
  Do not remove it as redundant. Moving the mouse back over the menu hands control to hover.
- **Navigation must not claim the contested key.** It is resolved on release, which is the most
  delicate path in the mod. Navigation only sees non-contested keys.
- Vanilla 26.3 has **no controller support at all**: no controller or gamepad classes, and no
  client source file referencing an SDL controller API. Controller input therefore needs a
  polling layer plus deadzone and edge detection, not an event hook.
- `core/`: pure policy and state. Must never import Minecraft, Fabric, NeoForge, GUI, config, or API.
- `config/`: `ConfigurationSnapshot`, `ConfigurationSnapshotCodec`, `ConfigurationMigrator`,
  `ConfigurationPersistence`. `ConfigurationSnapshot.defaults()` is the single source of truth.
- `configmanager/ConfigScreenBuilder.java`: Cloth Config screen, one private method per tab.
- `configmanager/KeyCaptureScreen.java`, `ActionSelectionScreen.java`: K-key flow.
- `customdata/DataManager.java`: optional read-only custom display names, category hiding, colours.
- `mixin/`: `KeyMappingMixin`, `KeyMappingAccessor`, `KeyBindsScreenMixin`, `MinecraftAccessor`,
  `GuiGraphicsExtractorAccessor`.
- `input/minecraft/`: `MinecraftConflictIndex`, `MinecraftInputController`,
  `SelectionActivationService`, `PulseController`.
- `integrations/minecraft/MinecraftBindingSource.java`, `api/`: staged versioned public contract.

## Runtime invariants

These are load-bearing. Preserve them.

- `KeyMapping` mixins intercept `set`, `click`, and `setDown`. Signature or injection-point
  changes require a Minecraft compatibility review.
- Presses reach the mod through `KeyMapping.set`. **Releases for an open screen arrive at
  `Screen.keyReleased`/`mouseReleased`**, so a conflicting key legitimately logs `Pressed: true`
  with no matching `Pressed: false`.
- Rapid re-press is correct, not a bug. It was misdiagnosed as auto-repeat once. A deliberate
  5+ second hold is the authoritative test.
- Do not add a physical key-state poll, a `WindowAccessor` mixin, or a `keysAwaitingRelease`
  latch. That machinery was written and reverted: the mod never sees key-up, so a latch set on
  finalise swallows the next genuine press and the menu stops opening entirely.
- Conflict scans refresh on startup, world join, vanilla keybind-screen close, config save, and
  priority change. Non-vanilla rebinding still needs an explicit adapter notification.
- Direct priority is `actionTranslationKey:physicalKeyName`, exclusive per physical key. A
  matching category priority is the fallback.
- The K capture path uses a direct edge-triggered path plus the click-poll fallback, sharing one
  cooldown, and must not reopen a screen while one is active.
- Selection screens snapshot their actions. A release finalises, manually sets the mapping down,
  increments its click count, and starts a pulse.
- On 26.3 the client is **SDL3, not GLFW**: `org.lwjgl.glfw` no longer exists, key type is
  `InputConstants.Type.KEYBOARD`, unbound keys are detected by negative key value, and rendering
  uses `com.mojang.renderpearl.api.pipeline.RenderPipeline`. The K default is
  `SDL_SCANCODE_K = 14`, derived rather than read from vanilla source, but it is now confirmed
  working in game on 26.3 Fabric.
- `FILTERED_CATEGORY_KEYS` defaults to `[]`. The old `[Debug]` default matched no 26.2+ category.
  Category matching is case-insensitive against `Category.id()` or the English label.

## Configuration contract

- 30 options, kept in sync across `ConfigurationSnapshot`, `ConfigurationSnapshotCodec`,
  `Configurations`, `src/main/resources/keybindsgalore.properties`, `ConfigScreenBuilder`, and
  `assets/keybindsgalore/lang/en_us.json`. Check all of them when changing an option.
- `ConfigurationSnapshot.defaults()` is the single source of truth; `Configurations` seeds itself
  from it. Do not reintroduce hand-written literals.
- Retiring an option is three steps, never a deletion: remove it from the snapshot, codec,
  `Configurations`, and the properties file; add the key to `ConfigurationMigrator.RETIRED_KEYS`
  so it is stripped on load; note the replacement in the properties file. `ConfigManager` sets
  `errorFlag` for any unrecognised key, so plain deletion punishes every existing user.
- Retired keys: `PIE_MENU_COLOR_LIGHTEN_FACTOR`, `PIE_MENU_SELECT_COLOR`, `PIE_MENU_BLEND`,
  `LABEL_TEXT_INSET`, `IGNORED_KEYS`, `INVERT_IGNORED_KEYS_LIST`, `USE_SOFTWARE_RENDERING`,
  `LAZY_CONFLICT_CHECK`, `USE_KEYBIND_FIX`.
- **Never bind one config field to two widgets.** Two toggles writing `DARKENED_BACKGROUND`, one
  per Visual tab, silently reverted each other on save.
- The build does not validate `en_us.json`; `processResources` copies it verbatim. Parse it
  explicitly after editing. A trailing comma on the last entry is the easy mistake.
- Cloth Config has **no colour picker**. `startColorField` returns a `ColorEntry`, a hex text
  field plus a button. A real picker means a custom entry.
- Builder arities differ: `startIntSlider(Component, int, int, int)` takes bounds as arguments,
  while `startIntField` and `startFloatField` use `setMin`/`setMax`. `startColorField` takes
  only `(Component, int)`.
- Visual defaults: gradient intensity 30, animation duration 180 ms, 0 means instant, open-only,
  labels after animation. Darken strength is pie-only; the list keeps fixed dimming.
  `PIE_MENU_COLOR`, `PIE_MENU_HIGHLIGHT_COLOR`, and `PIE_MENU_ALPHA` are list-only despite names.
- Saving from the GUI writes the file but does not rescan; call the rescan explicitly.

## Publishing

`prompts/03-release-and-modrinth.md` is the authoritative release procedure. Read it before
releasing. The parts that are easy to get wrong:

- **Modrinth project id `l6y7RMn7`**, slug `keybindsgalore+(hvb007)`.
- **Token**: `$env:MODRINTH_TOKEN`, set with `setx`, 3-month expiry. `setx` only affects new
  processes, so an already-running shell sees it empty; fall back to reading
  `Get-ItemProperty -Path 'HKCU:\Environment'`. Never accept a token pasted into chat, and never
  store one in the repo, not even gitignored.
- Scopes: `VERSION_CREATE`, `VERSION_READ`, `VERSION_WRITE`, and project write for the
  description. `VERSION_DELETE` is the one only needed to fix a wrong version number.
- Modrinth numbers are plain semantic versions; the Minecraft version and loader are separate
  fields. GitHub tags encode both: `keybindsgalore-1.8.0+26.2-neoforge` for a Modrinth number of
  `1.8.0`. Jar filenames carry the Minecraft version and full loader name. **GitHub release
  titles are `1.8.0+<mc>Neo` for NeoForge and `1.8.0+<mc>` for Fabric** - the `Neo` marker
  exists because a release list showing four entries all called `1.8.0` is ambiguous.
  **Do not bump the number for a port that changes no mod behaviour** - that was a real mistake
  on the 26.3 build.
- `POST /v2/version` is `multipart/form-data` with a `data` JSON part, and `data` **must precede**
  the file parts. `project_id` and `environment` are required, and `environment` is a single
  string, not an array.
- **Set the primary file at creation.** `primary_file` is the multipart field name there. The
  edit endpoint has no primary field at all and returns `204` while doing nothing, and there is no
  `PATCH /v2/version_file`. A version number and its file set must both be correct before the
  first upload.
- Version files are addressed by **sha1 hash**, not base62 id, for `DELETE /v2/version_file/{sha1}`.
- Tag the sources jar with `file_types: { sources: "sources-jar" }`; that is what makes Modrinth
  resolve the mod jar as the download.
- Upload **both** jars to Modrinth and **both** to GitHub. The sources jar is not GitHub-only.
- Never copy a dependency `project_id` from another loader's version. `P7dR8mSH` is Fabric API,
  not NeoForge, and **NeoForge has no Modrinth project page at all** - it is a loader, expressed
  through the `loaders` tag. Resolve every id by slug and confirm the title.
- Verify every upload by downloading it back and comparing hashes. `gh release view --json
  assets` returns a `sha256` per asset.
- `powershell`: `Get-Content -Raw` returns a `PSObject` that `ConvertTo-Json` wraps as
  `{"value": ...}`, and it reads files as **ANSI**, not UTF-8. Use `[System.IO.File]::ReadAllText`
  and keep published prose pure ASCII. **Never fix text with a `-replace` chain and trust it** -
  one silently deleted every lowercase `i` and briefly published "KeybndsGalore" as
  "KeybndsGalore". Verify by comparing the fetched result to the intended text as whole strings.

## Style and change discipline

- `.editorconfig` is the formatting source of truth: four-space indent, 120 columns, LF, two-space
  JSON. No Checkstyle, Spotless, or google-java-format.
- Javadoc on `api/` only. Implementation classes use no Javadoc; `//` at column 0 only for
  non-obvious *why*.
- Keep branches within three levels of indentation; extract a method instead.
- SLF4J via `KeybindsGalore.LOGGER`. High-volume tracing goes through
  `KeybindsGalore.verboseLog`, gated on `VERBOSE_DEBUG`, which is the fastest way to diagnose
  selector behaviour from a user log.
- Delete dead code rather than deprecating it. `KBRenderer` was removed for this reason.
- Prefer the smallest change that preserves the input-state and priority invariants.
- Never commit or push unless asked, except where the user has already authorised a workflow.
- Do not commit generated output, build directories, stale jars, or agent memory.

## Known risks

- Minecraft upgrades can break the mixins, private accessors, the render API, and the Java target
  together. 26.2 and 26.3 needed different work in the same area.
- Cloth Config 26.2.155 emits a deprecation warning at startup from its own jar. It is upstream,
  already fixed in 26.3.159, and is not ours to patch. Do not claim it is absent.
- `neoforge.mods.toml` on 26.3 uses `versionRange = "[26.3.0-beta,)"`, which accepts any future
  26.3 beta untested. Narrow it if a NeoForge 26.3 release lands and we have tested it.
- Selection screens have no scrolling and long labels can overlap.
- The live Modrinth project short description and `source_url` are still stale and are being
  fixed by hand by the user.

## Verification reference

Runtime behaviour to check after any input-flow change: keyboard and mouse conflicts, direct
priority versus category fallback, list and pie menus, held versus one-shot actions, rebinding
and closing the vanilla Controls screen, multiplayer join refresh, K-key capture and priority
removal, and config save behaviour. `VERBOSE_DEBUG` plus `run/logs/latest.log` is the primary
diagnostic.
