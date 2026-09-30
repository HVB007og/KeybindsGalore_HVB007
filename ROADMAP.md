# KeybindsGalore — Roadmap and TODO

Single source of truth for planned work. `AGENTS.md` holds durable technical facts and
invariants; this file holds what is *not yet done*. Update it when work lands.

Status legend: `[ ]` open · `[~]` in progress · `[x]` done · `[-]` deliberately dropped

---

## Current state

**Three releases are published and human-verified in game.**

| Branch | Target | Loader | Modrinth version | GitHub tag |
|---|---|---|---|---|
| `master` | 26.3 | Fabric | `1.8.0` | same as `recovery/26.3-fabric` |
| `recovery/26.2` | 26.2 | Fabric | `1.8.0` | `keybindsgalore-1.8.0+26.2` |
| `recovery/26.2-neoforge` | 26.2 | NeoForge | `1.8.0` | `keybindsgalore-1.8.0+26.2-neoforge` |
| `recovery/26.3-fabric` | 26.3 | Fabric | `1.8.0` | `keybindsgalore-1.8.0+26.3` |
| `recovery/26.3-neoforge` | 26.3 | NeoForge | `1.8.0` | `keybindsgalore-1.8.0+26.3-neoforge` |

`master` is the default branch and tracks the 26.3 Fabric build, so cloning the repository gives
something that compiles. Its README is deliberately version-free and points at Modrinth for the
version matrix; do not add a version or branch table to it.

Modrinth project `l6y7RMn7`. All four entries are published and `listed`, each carrying the mod
jar plus a sources jar, every file verified by downloading it back and comparing digests. 45
unit tests pass on every branch.

**Every combination is human-verified in game**: pie menu, list menu, K-key priority capture,
priority resolution, and the config screen, on 26.2 Fabric, 26.2 NeoForge, 26.3 Fabric, and 26.3
NeoForge. The NeoForge builds need no ModMenu and contribute their config screen through
NeoForge's own extension point.

**The mod works. Everything below is improvement, not repair.**

### Outstanding, small

- [x] **NeoForge 26.3 port.** Shipped. Branch `recovery/26.3-neoforge`, published as Modrinth
      `1.8.0` for neoforge/26.3, human-verified in game. Built on the 26.3 Fabric branch, so the
      SDL3 and `renderpearl` work carried over and only the loader surface changed. NeoForge
      publishes 26.3 **only as beta**, so this tracks a moving target
Modrinth project page, all four done:

- [x] `source_url` points at the repo root, `https://github.com/HVB007og/KeybindsGalore_HVB007`
- [x] Gallery screenshots replaced with current ones
- [x] `master` fast-forwarded to a working build with a version-free README, so the repo front
      page is accurate
- [x] Project summary replaced. It now reads *"Resolves Minecraft keybind conflicts: when two
      actions share a key, choose the one you meant."*
- [x] The 26.3 Fabric Modrinth version's primary-file flag fixed. The version was recreated with
      `primary_file` set at creation, giving it a new id, and the changelog was carried over
      byte-for-byte. Ids are recorded in `AGENTS.md`.

**On reading a live Modrinth or GitHub page: always bypass the cache.** Every GET to
`api.modrinth.com/v2/project/{id}` can return a cached body, and a stale one led to reporting
a third summary line that had not been on the page for months. Append a cache-busting query
parameter, and check a field you know changed:

```powershell
$nonce = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
Invoke-RestMethod "https://api.modrinth.com/v2/project/l6y7RMn7?cb=$nonce"
```

Do not trust the `updated` field either: Modrinth does not reliably bump it on a description
edit, so it cannot be used to tell whether a save landed. Read the field you care about back and
compare it.

Remaining, neither urgent:

- [ ] Re-check the NeoForge 26.3 beta periodically. A newer beta may change the render or input
      API, and the version range in `neoforge.mods.toml` is `[26.3.0-beta,)`, so it will accept
      whatever comes next without re-testing
- [ ] Gallery screenshot *ordering*. The images are current, but the featured image drives the
      preview thumbnail in search results and is the highest-leverage image on the page

---

## P0 — Make it trustworthy - closed

The goal was that nothing in the mod can lie to the player. That is met: every option is wired
or retired, the runtime invariants are tested, and the one remaining edge case is documented
rather than hidden. **P0 is closed. Work now moves to P1.**

If something reopens P0, it is a bug report, not a feature request: an option that is persisted
but unread, a refresh that silently does not happen, or a claim in the docs that the code does
not support.

### P0.1 Dead config options — resolved

Every option is now either wired or retired. The manual report of 2026-09-25 is
what surfaced most of these; the full history is in the git log.

Wired and verified in game:

- [x] `PIE_MENU_MARGIN`, `PIE_MENU_SCALE`, `CANCEL_ZONE_SCALE` — pie geometry
- [x] `EXPANSION_FACTOR_WHEN_SELECTED` — hovered wedge grows outward. Default `0.06`,
      range `0.0-0.3`
- [x] `CIRCLE_VERTICES` — budget split across wedges, so it no longer only visibly
      affects the cancel circle
- [x] `LABEL_TEXT_SHADOW` — drawn as an explicit offset dark pass, since the boolean
      passed to the text call was not visibly different
- [x] `DARKENED_BACKGROUND_STRENGTH` — new 0-255 slider. **Pie menu only**; the list
      menu keeps its own fixed dimming because the two layouts have different contrast
      problems
- [x] `SECTOR_GRADATION` + `GRADATION_INTENSITY` — shades each wedge from a lighter
      inner edge outward. Nearly free to draw, because `RingRenderer.drawRing` already
      took separate inner and outer colours. Intensity is a 0-100% blend toward white
      rather than a flat channel offset, so the slider means the same thing for a dark
      wedge as for a pale one and saturates instead of clipping
- [x] `ANIMATE_PIE_MENU` + `ANIMATION_DURATION` — 0-1000 ms eased open animation, where
      0 appears instantly. **Open only**; closing stays immediate because delaying
      teardown would hold input after the selection is already committed, and that is
      the most delicate path in the mod. Labels appear only once the animation finishes,
      because fading them in with it looked wrong — the text slid outward while its wedge
      was still growing
- [x] `PIE_MENU_ALPHA`, `PIE_MENU_COLOR`, `PIE_MENU_HIGHLIGHT_COLOR` — moved to a
      dedicated "Visual Settings (List Menu)" tab, since they are read only by the list
      screen and appeared broken when tested in pie mode

Retired through `ConfigurationMigrator.RETIRED_KEYS`, so existing config files are
cleaned on load instead of raising an unknown-key error:

- [-] `PIE_MENU_COLOR_LIGHTEN_FACTOR` — unread 24-bit RGB offset, and redundant because
      alternating wedge colours are set explicitly
- [-] `PIE_MENU_SELECT_COLOR` — superseded by `PIE_MENU_SECTOR_COLOR_SELECTED`, which is
      what the hovered wedge actually uses
- [-] `PIE_MENU_BLEND` — duplicated what the per-sector colours already express
- [-] `LABEL_TEXT_INSET` — labels now sit a fixed 4px outside the pie and are clamped on
      both axes
- [-] `IGNORED_KEYS`, `INVERT_IGNORED_KEYS_LIST` — a suppress-these-keys feature that
      conflict detection never consulted. Prioritising those keys already stops them
      hijacking movement and mouse actions, which is what the list was for
- [-] `USE_SOFTWARE_RENDERING`, `LAZY_CONFLICT_CHECK`, `USE_KEYBIND_FIX` — all 1.21.x
      leftovers that lost their meaning as the renderer, the refresh triggers, and the
      old keybind fix changed

**Every option is now either wired or retired.** The config screen, the properties file,
the codec, and `en_us.json` all agree on the same 30 options, with no orphaned
translation keys. `VERBOSE_DEBUG` is wired but is only a higher-verbosity subset of
`DEBUG`; give it its own meaning or document the relationship.

### P0.2 Non-vanilla rebinding notification - closed as a documented limitation

- [-] **Accepted, not fixed.** The conflict index refreshes on startup, world join,
      Controls-screen close, config save, and priority changes. If another mod rebinds a key at
      runtime through its own config screen, that change is not picked up until one of those
      triggers fires. A fix needs an explicit adapter notification, not polling, because polling
      the key state is exactly the anti-pattern that was already tried and reverted elsewhere in
      this codebase. It stays listed under **Known limitations** on the Modrinth page and in the
      README, so it is documented rather than forgotten.

      Worth revisiting only if a mod in the wild actually causes a visible problem, and then
      through a public API rather than a private hook.

### P0.3 Release readiness

- [x] Audit `fabric.mod.json`: version, Java dependency, and Fabric API range now reflect
      26.2. `minecraft` tightened to `~26.2` so a future 26.3 does not silently install an
      incompatible build. `cloth-config` moved from `recommends` to `depends`, because the
      settings screen is built from Cloth Config classes and a user with ModMenu but no
      Cloth Config would crash opening it. `contact` block added with project links
- [x] `README.md` rewritten. The old one had corrupted encoding, and claimed
      OpenGL/Tesselator rendering, a "live RGBA colour picker", a "Debug" category, and
      "zero dependencies", none of which are true on this branch
- [x] `CHANGELOG_LATEST.md` rewritten. It described the 1.21.7 owo-lib release
- [x] `gradle.properties` un-ignored. It pins the Minecraft, Fabric API, and loader versions,
      so it must be tracked for a build to be reproducible. Version bumps belong in that
      file on purpose
- [x] `jars/` untracked. Four stale 1.21.1 Fabric and NeoForge artifacts were committed to
      the repository, with different licensing metadata, and looked like current releases
- [x] Version bumped to `1.8.0+26.2`. Modrinth already had `1.7.2+26.2` published and will
      not accept a duplicate version string; minor bump because the release adds features
      and removes nine config options
- [x] `MODRINTH_DESCRIPTION.md` rewritten for 26.2, leading with the config screen fix
- [x] `RELEASE_CHECKLIST.md` written, with the values to paste and the gallery plan
- [x] `bkpjar/` created as the durable local archive of every published build, with
      `SHA256SUMS.txt` and a documented release procedure. Kept out of git because the
      authoritative copy of a published build is the Modrinth version plus the GitHub
      release assets
- [x] Removed the dead `keybindsgaloreplus` language file and an unused duplicate icon from
      the jar. The Plus mod is a separate historical project; its translations shipped in
      every build and referred to a reload-config feature that no longer exists
- [x] Verified the built jar: correct version, 60 classes, mixins config, default
      properties, icon, translations, embedded licence, and no stale assets
- [x] Published `1.8.0` to Modrinth for all four combinations: 26.2 Fabric, 26.2 NeoForge,
      26.3 Fabric, and 26.3 NeoForge. Each entry carries the mod jar **and** the sources jar,
      with the sources tagged `sources-jar`. Both jars also attach to each GitHub release.
      Every file was verified by downloading it back and comparing digests
- [x] Tagged and pushed all four releases. Modrinth numbers are plain `1.8.0`; GitHub tags
      encode the Minecraft version and loader
- [x] Automated the release path in `prompts/03-release-and-modrinth.md`, including the
      Modrinth API traps: multipart shape, hash-addressed file deletes, and the fact that a
      primary file can only be set at creation
- [x] Replace the gallery screenshots, which were from 2023-2024 and showed a pre-1.21.1 UI

---

## P1 — Differentiate

This is where the mod stops being "a pie menu" and becomes a control centre.

### P1.1 Colour picker widget

- [ ] Write a custom Cloth Config entry with RGB and alpha sliders, so sector colours can
      be chosen without typing an ARGB hex value
- [ ] Cloth Config 26.2.155 provides no picker. `startColorField` returns a `ColorEntry`
      that extends `TextFieldListEntry<Integer>`, so it is a hex text field plus a small
      button, and the library ships no picker screen to open. There is no way to get
      sliders out of the stock builder
- [ ] Estimated as a contained piece of work: one custom entry class plus a screen with
      four sliders and a live preview, registered in place of `startColorField`
- [ ] Affects 8 colour options. Until it exists, the tooltips tell players to type an
      ARGB value such as `C0606060`

### P1.2 Conflict Inspector

- [ ] A screen that lists *every* conflict across all keys at once, not just the one
      you happened to press
- [ ] Show the resolved owner of each conflict and why it won
- [ ] Jump from an inspector row straight into that key's resolution flow
- [ ] This is the single highest-value P1 feature. It is what turns the mod from a
      reactive popup into a tool people open deliberately

### P1.3 Profiles

- [ ] Named sets of bindings and priorities that can be switched (e.g. "default",
      "creative", "PvP", "streaming")
- [ ] Switch on world join or on a manual hotkey
- [ ] Needs a profile-aware layer above the current flat priority lists
- [ ] Decide whether profiles are per-world, per-server, or global before implementing

### P1.4 Context rules

- [ ] **Only offer the menu for bindings that can actually fire right now.** Requested by the
      maintainer. Today every key on one physical key is offered, including bindings that are
      inactive in the current state, so pressing middle mouse in survival pops a menu that
      includes spectator-only actions, and F3 combinations can offer a debug key that is not
      usable in the current gamemode. Nothing in the codebase considers gamemode at all right
      now; `MinecraftBindingCatalog` collects every non-unbound `KeyMapping` unconditionally.
- [ ] **Verified against the 26.3 vanilla source, and the fix is not what it looks like.**
      `KeyMapping` has **no gamemode field at all**. It carries `name`, `defaultKey`,
      `category`, `order`, `clickCount`, a `keyModifier`, and an `IKeyConflictContext`
      (`UNIVERSAL` by default). So "which gamemodes is this binding for" cannot be read off
      the binding. It has to be **inferred by convention from the action name and category**,
      for example names containing `spectator` or the `Game Interface` category for F3 debug
      bindings. That inference is a maintenance risk: it is a naming convention, not a contract,
      and a vanilla rename would silently break it.
- [ ] Decide the mechanism before implementing. Options, cheapest first:
      1. a curated name/category exclusion list, which is guessable and would drift,
      2. a config option letting the player hide actions from the menu, which is honest and
         puts the burden where the knowledge is,
      3. a live probe of whether the action did anything, which is the most accurate but cannot
         work for the decision itself, since the menu opens *before* the action would have run.
- [ ] Filter on the *offered list*, never on the *winner*. If a hidden action were still allowed
      to win a contested press it would fire without the player ever seeing it, which is worse
      than the current behaviour.
- [ ] Whichever way this goes, it needs a **refresh trigger**. Gamemode changes at runtime, and
      none of the five existing triggers fire on it, so this composes with the P0.2 limitation
      rather than replacing it.
- [ ] **Does not replace context rules.** Those are about preference ordering, such as
      "outside a GUI prefer movement". This is about relevance, not priority. Keep them as
      separate items.
- [ ] Rules like "outside a GUI, prefer movement" or "in a GUI, prefer chat"
- [ ] This is where a real state/context layer earns its keep. `InputOwnershipState`
      is the seed of it

### P1.4b Hold mode

- [ ] **Left or right click a wedge to put that key into hold mode.** Requested by the
      maintainer. The key would stay held while the menu is open, so a movement or attack
      binding does not stall behind the choice. Reuses the existing wedge picking, and the
      existing pulse and input-ownership machinery, so it should not need a new state model.
- [ ] Needs a decision on the interaction: left click currently commits a selection, so either
      the hold is set by right click only, or by a modifier, or the click-to-commit behaviour
      changes. Ask before implementing rather than guessing.
- [ ] Must respect the existing rule that a key released while the menu is open finalises the
      selection. Hold mode interacts directly with that, so it is not as small as it looks.

### P1.5 Accessibility

- [ ] **Overlapping long labels.** Labels are now clamped so they can never be cut off by
      the screen edge, which fixed the worst case. Remaining problem: with many conflicts
      and long action names, neighbouring labels collide, because the pie cannot grow.
      Options are shrinking the wedge radius as the count rises, truncating with an
      ellipsis plus tooltip, or moving to a radial list. The list menu has the mirror
      problem and can reuse a vanilla scrollable widget
- [x] **Input navigation for both selectors.** Both selectors were mouse-only, which made them
      unusable without a mouse. One input layer now maps *actions* (move previous, move next,
      commit, cancel) onto the selection model, so the device is a detail rather than something
      baked into the selection logic. `ui/model/ConflictInputActions` is Minecraft-free and
      unit-tested, so navigation behaviour is verified without launching the game.
      Keyboard: arrow keys or WASD to move, Enter or Space to commit, Escape to cancel.
      Opt in with `KEYBOARD_CONTROL_MODE` in the Behaviour tab, default **off** so an existing
      config behaves exactly as it did before. With the option on, the contested key also
      confirms, and the menu stays up when the key is released, so the same finger can open and
      close it. The mouse keeps working in both states. Controller is deliberately not pursued;
      see "Explicitly not doing"
- [x] **Screen-reader narration** of the conflict and the selected action. Ranked high on
      purpose: a screen-reader user is a real player this mod currently *hurts*, because a menu
      appearing silently is a menu they can neither perceive nor diagnose. Without narration the
      mod removes the feedback vanilla gave them. Both menus announce the conflicting key and the
      option count on open, then the action and its position as the highlight moves. The
      key-capture prompt announces that any key or button will be captured, replacing vanilla's
      generic "use the mouse cursor or tab to select an element" hint, which was actively wrong
      there because nothing is selectable
- [x] **Automatic keyboard mode for narrator users.** Added alongside narration, because
      narration is close to pointless if the menu cannot be operated by keyboard. If the narrator
      is set to All and keyboard mode is off, it is turned on at the first world join and
      announced in chat. `NARRATOR_AUTO_ENABLE` controls this and clears itself after firing so
      it never repeats. Declined as a silent change: it is announced, and reversible in settings
- [ ] **High-contrast mode** honouring Minecraft's own accessibility setting
- [ ] **Reduced-motion support**, which pairs with the `ANIMATE_PIE_MENU` work and can collapse
      the open animation to instant
- [ ] **Minimum text size handling**: labels currently use the default font size with no
      scaling
- [ ] **Narrate the config screen.** The gap that remains from the narration work. The mod's own
      settings screen is built by Cloth Config via `builder.build()`, which hands back a bare
      `Screen`, so there is nowhere to put `updateNarrationState` without reaching into Cloth.
      Two options, neither taken: a narrow mixin into Cloth's screen, or a wrapper that delegates
      every `Screen` method. A wrapper risks breaking Cloth's tab handling and parent-screen
      behaviour. Left undone deliberately for 1.9.0 rather than half-done
- [ ] **Narration performance.** Moving the highlight with the keyboard feels slightly heavy while
      a screen reader is running. The worst cause was fixed: selection movement was calling
      `saySystemNow`, which *interrupts* whatever is being spoken, once per key press, and now
      goes through the game's own throttled `triggerImmediateNarration`, which checks
      `shouldRunNarration` first. Some heaviness remains and the maintainer has judged it not
      worth further pursuit. Recorded so it is a decision rather than an oversight, and so nobody
      re-investigates it from scratch. If it is revisited, measure before changing: the next
      suspects are `updateNarrationState` running per frame and the narrator's own queue depth,
      and neither is a guess to ship

### P1.6 Robustness of the selection flow

- [ ] Reliable Escape and focus-loss cancellation. Losing window focus mid-selection
      currently leaves ambiguous state
- [ ] Resize-safe selectors: verify behaviour when the window changes size with the menu
      open
- [ ] Verify the selection flow at extreme GUI scales and with long action names

---

## P2 — Reach

### P2.1 Config format migration

- [ ] Move from `.properties` to JSON via Gson. 8 of 9 popular Fabric mods use JSON;
      only REI uses Cloth Config for storage
- [ ] This deletes the hand-rolled `ConfigurationCodec` **and** the parallel reflection
      path in `ConfigManager`, roughly 150 lines and one whole duplicate system
- [ ] **Breaking for players, not for the mod.** Cloth Config and ModMenu are unrelated and
      stay exactly as they are; only the on-disk file format changes. The cost is a one-time
      converter that reads the old `.properties`, writes the new file, and leaves a note, so
      nobody's hand-made priorities silently disappear. Players who never touched the file are
      unaffected in practice. Worth doing only if the format itself starts causing real problems,
      since the current file works
- [ ] Not a cleanup. A project.

### P2.2 API isolation

- [ ] Move `api/` into a separate `src/api` source set, following Sodium and Iris
- [ ] Makes "nothing outside the API may use API internals" a compile error rather than
      a comment in a text file
- [ ] Low effort, low risk, low urgency. Insurance against a future mistake
- [ ] Do *not* ship a separate Maven artifact. That is REI/Cloth-API scale

### P2.3 External integrations

- [ ] **Create** — no generic action callback exists; integration needs a real
      Create-side hook, not a synthetic one
- [ ] **Controlify** — has a public registration API; the most tractable first target
- [ ] **Amecs** — optional compatibility direction, not a current requirement. It
      intentionally supports multiple actions per one key, so any support must make
      ownership explicit rather than pretending the conflict model applies unchanged
- [ ] **MiniHUD / MaLiLib** — callbacks are private; treat as out of scope until they
      expose a public surface
- [ ] Rule for all of these: explicit source-owned callbacks or public registration
      only. No private or synthetic input hooks

### P2.4 Testing depth

- [ ] `fabric-loader-junit` for real Minecraft input simulation. **Currently skipped
      deliberately** — it is slow, fragile, and breaks on Minecraft updates, and the
      extract-pure-logic-then-test-manually approach already works
- [ ] Revisit only if input regressions become frequent enough to justify the upkeep
- [ ] 9 of 9 popular client Fabric mods have no automated tests at all

### P2.5 Ongoing

- [ ] Compatibility matrix across Minecraft versions and other keybind mods
- [ ] Screenshots and a proper changelog per release
- [ ] Respond to community issues (#13 Amecs, #14 F3 behaviour, #17 Escape/left-click)

---

## Open questions

Decisions that need a human answer before the work can start.

- [x] Release or keep building? **Release.** P0 is closed enough to ship; see P0.3
- [x] `PIE_MENU_SELECT_COLOR`? **Retired**, along with `PIE_MENU_BLEND`,
      `IGNORED_KEYS`, and `INVERT_IGNORED_KEYS_LIST`
- [x] `IGNORED_KEYS`? **Retired.** Prioritising those keys already prevents them
      hijacking movement and mouse actions, which is what the list was for
- [x] The three pie cosmetics? **`SECTOR_GRADATION` and `ANIMATE_PIE_MENU` implemented.**
      `PIE_MENU_BLEND` retired, since it duplicated the per-sector colours
- [x] Commit `ROADMAP.md`? **Yes** — it is project documentation, not agent memory
- [x] The three remaining unwired 1.21.x options? **Retired.**
- [x] Commit `ROADMAP.md`? **Yes** — it is project documentation, not agent memory
Closed, mostly by decision rather than by further work:

- [x] `VERBOSE_DEBUG` needing its own meaning? **No.** One debug toggle is enough, and
      `VERBOSE_DEBUG` is simply "debug, but louder". Everything worth knowing should be in
      `DEBUG`. Keeping the second toggle is fine for users who want per-frame tracing without a
      wall of startup output.
- [x] Profiles being per-world, per-server, or global? **Global, and only if profiles get built
      at all.** Revisit only as part of the profiles work itself, not as a standalone decision.
- [x] Pie scale defaults of `0.8`/`0.2` vs the advertised `0.6`/`0.25`? **Keep the current
      values.** They are what the code has always done and players are used to them. Changing a
      default nobody asked to change is a regression, not a fix.
- [x] The `debug` category question: the category has no English label in 26.2+, so the legacy
      `FILTER_DEBUG_KEYS to [Debug]` migration produces a value that matches nothing. It is
      harmless but pointless, and the default is already `[]`. Low priority; revisit only if
      someone reports a problem with an old config.

Still open:

- [ ] **Further pie options, if wanted.** Candidates considered and not added unprompted, since
      each is a design decision rather than an obvious win: wedge outline colour and width, a
      rotation offset for the starting wedge, and a maximum label width that truncates with an
      ellipsis
- [ ] Give each loader branch its own run directory. `run/` and `build/` are shared, and
      leftover `build/moddev` plus a NeoForge-written `run/` makes a Fabric branch look
      broken when `runClient` launches the wrong loader

---

## Explicitly not doing

- Auto-repeat suppression. Verified unnecessary: a 10-second hold produced one press
  event. A latch-based guard was written and reverted because it would have swallowed
  the *next genuine press*
- Per-file licence headers. 6 of 11 popular mods have none and `fabric-example-mod`
  ships none; the root `LICENSE` plus the `jar { from("LICENSE") }` snippet is enough
- Checkstyle, Spotless, or google-java-format. No published Fabric mod in the reference
  set uses them; `.editorconfig` is the formatting source of truth
- `fabric-loader-junit` as a default. See P2.4
- **Autoclicker integration.** Considered and declined by the maintainer, who also judged it
  unnecessary. Recording it so the question is not reopened: an autoclicker is a tool for
  bypassing server restrictions, and a conflict-resolution mod has no business shipping one or
  integrating with one. It would also put the project in a different category of software
  entirely, on Modrinth and with other modders, for no benefit to conflict handling. If this
  ever changes, treat it as a product decision, not a feature request.

  What *would* be legitimate and adjacent: an integration with a **macro** tool that plays
  back a recorded input sequence, or a keybind macro mod with a public API. The distinction is
  user-authored macros versus the mod generating clicks itself.

- **Native controller support.** Closed by the maintainer: not wanted, and the research shows it
  is not worth the cost. Recording the findings so the question is not reopened.

  There is no vanilla controller support to build on. Minecraft 26.3 has no controller or gamepad
  classes and no client source referencing an SDL controller API, so a from-scratch
  implementation would need polling, deadzones, and button-edge detection written by hand. An
  earlier note in this file claimed "Minecraft already routes controller input, so most of the
  work is the shared abstraction". That was wrong, and checking it rather than assuming is the
  only reason the estimate was corrected.

  Four mods give Java Edition real controller support. Only **Controlify** has a mod-developer
  API; Midnight Controls, Controllable, and Controller Support Mod have none, and the latter two
  are in maintenance mode. Controlify is current, targets 26.2 and 26.3 on both loaders from a
  single universal jar, and uses SDL3, the same input stack as 26.3.

  Two of its documented behaviours would probably cover our selectors with no code at all: it
  auto-converts unhandled modded `KeyMapping`s into controller bindings, and it emulates arrow
  keys for GUI navigation, which our keyboard mode already consumes. Neither has been tested in
  game, so treat it as a cheap thing to try rather than a known result.

  A proper integration would be a `ScreenProcessor` mapping controller bindings straight onto
  `ConflictInputActions.Action`, plus button-guide glyphs. That fits our existing
  `ui/minecraft` versus `ui/model` split almost exactly, because Controlify's rule is that
  controller handling lives in a separate class so the screen does not depend on the mod loading.
  Against that: it adds a compile dependency that differs per Minecraft version, and Controlify
  churns fast, replacing its entire guide and trigger API with a Contextual API in 3.5.0. Not a
  stable contract to bind a small mod to for a feature nobody asked for.

