# KeybindsGalore — Roadmap and TODO

Single source of truth for planned work. `AGENTS.md` holds durable technical facts and
invariants; this file holds what is *not yet done*. Update it when work lands.

Status legend: `[ ]` open · `[~]` in progress · `[x]` done · `[-]` deliberately dropped

---

## Current state

**Three releases are published and human-verified in game.**

| Branch | Target | Loader | Modrinth version | GitHub tag |
|---|---|---|---|---|
| `recovery/26.2-neoforge` | 26.2 | NeoForge | `1.8.0` | `keybindsgalore-1.8.0+26.2-neoforge` |
| `recovery/26.3-fabric` | 26.3 | Fabric | `1.8.0` | `keybindsgalore-1.8.0+26.3` |
| `recovery/26.2` | 26.2 | Fabric | `1.8.0` | `keybindsgalore-1.8.0+26.2` |

Modrinth project `l6y7RMn7`. Every entry carries the mod jar plus a sources jar, all verified by
downloading back and comparing digests. 45 unit tests pass on every branch.

Verified in game on 26.3 Fabric: pie menu, list menu, K-key priority capture, priority
resolution, and the config screen. Verified in game on 26.2 NeoForge: the same set, plus the
ModMenu-free config screen contributed through NeoForge's extension point.

**The mod works. Everything below is improvement, not repair.**

### Outstanding, small

- [~] **NeoForge 26.3 port.** NeoForge publishes 26.3 only as beta; newest is `26.3.0.26-beta`.
      Branch `recovery/26.3-neoforge` is being prepared for testing. Must handle SDL3 key input
      and the `renderpearl` pipeline, which 26.2 does not.
- [ ] Modrinth project **short description** still reads *"I Learnt how to code Java for the
      explicit purpose of updating this mod... updated to 1.20"*, and `source_url` points at
      `KeybindsGalore_HVB007_1.20.x/tree/Alpha`. Being fixed by hand.
- [ ] The 26.3 Modrinth version has no file flagged `primary`, because the version number was
      corrected after creation and Modrinth cannot set that field afterwards. Downloads are
      correct. A clean fix needs `VERSION_DELETE` and a recreate.
- [ ] Screenshots. The gallery holds 6 images; whether they still show the current UI is
      unverified. The preview thumbnail is the single highest-leverage image on the page.

---

## P0 — Make it trustworthy

The goal of P0 is that nothing in the mod can lie to the player. Mostly done; the
remainder is honesty about unwired options.

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

### P0.2 Non-vanilla rebinding notification

- [ ] Conflict index refreshes on startup, world join, keybind-screen close, config save,
      and priority changes. A mod that changes a binding at runtime (via its own config
      screen) is not detected. Needs an explicit adapter notification rather than a
      polling hack.

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
- [x] Published `1.8.0` to Modrinth for 26.2 Fabric, 26.2 NeoForge, and 26.3 Fabric. Each entry
      carries the mod jar **and** the sources jar, with the sources tagged `sources-jar`. Both
      jars also attach to each GitHub release. Every file was verified by downloading it back
      and comparing digests
- [x] Tagged and pushed all three releases. Modrinth numbers are plain `1.8.0`; GitHub tags
      encode the Minecraft version and loader
- [x] Automated the release path in `prompts/03-release-and-modrinth.md`, including the
      Modrinth API traps: multipart shape, hash-addressed file deletes, and the fact that a
      primary file can only be set at creation
- [ ] Replace the gallery screenshots, which are from 2023-2024 and show a pre-1.21.1 UI

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

- [ ] Make ownership explicit per context rather than always global. The conflict table
      is currently global, which is wrong when the same key means different things in
      creative versus survival
- [ ] Rules like "outside a GUI, prefer movement" or "in a GUI, prefer chat"
- [ ] This is where a real state/context layer earns its keep. `InputOwnershipState`
      is the seed of it

### P1.5 Accessibility

- [ ] **Overlapping long labels.** Labels are now clamped so they can never be cut off by
      the screen edge, which fixed the worst case. Remaining problem: with many conflicts
      and long action names, neighbouring labels collide, because the pie cannot grow.
      Options are shrinking the wedge radius as the count rises, truncating with an
      ellipsis plus tooltip, or moving to a radial list. The list menu has the mirror
      problem and can reuse a vanilla scrollable widget
- [ ] **Keyboard-only navigation** of both selectors (arrow keys, Enter to commit,
      Escape to cancel). The pie is currently mouse-only, which makes it unusable without
      a mouse and is the largest accessibility gap
- [ ] Screen-reader narration of the conflict and the selected action
- [ ] High-contrast mode honouring Minecraft's own accessibility setting
- [ ] Reduced-motion support, which pairs with the `ANIMATE_PIE_MENU` work
- [ ] Minimum text size handling: labels currently use the default font size with no
      scaling

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
- [ ] **Breaking change.** Requires a one-time converter that reads the old file,
      writes the new one, and leaves a note. Do this *after* the first 26.2 release, so
      real players get a working mod before their configs move
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
- [ ] `VERBOSE_DEBUG` currently only means "more `DEBUG` lines". Give it its own meaning,
      or document it as an alias?
- [ ] **Further pie options, if wanted.** Candidates that were considered and not added
      unprompted, since each is a design decision rather than an obvious win:
      wedge outline colour and width, a rotation offset for the starting wedge, and a
      maximum label width that truncates with an ellipsis
- [ ] Profiles: per-world, per-server, or global?
- [ ] Is the `debug` category ever going to get a real English label in 26.2+? If not, the
      legacy `FILTER_DEBUG_KEYS → [Debug]` migration should be retired
- [x] Release tag format, settled: Modrinth uses the plain number, GitHub tags carry the
      Minecraft version and loader, and a port that changes no mod behaviour does **not** bump
      the number
- [ ] Should the pie scale defaults stay at `0.8`/`0.2`? Those are the values the code
      always used; the `0.6`/`0.25` the config file once advertised were never in effect.
      Current behaviour was kept deliberately, but it was never a choice you made
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
