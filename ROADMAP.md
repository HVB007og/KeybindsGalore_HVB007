# KeybindsGalore — Roadmap and TODO

Single source of truth for planned work. `AGENTS.md` holds durable technical facts and
invariants; this file holds what is *not yet done*. Update it when work lands.

Status legend: `[ ]` open · `[~]` in progress · `[x]` done · `[-]` deliberately dropped

---

## Current state

Branch `recovery/26.2`, Minecraft 26.2, Java 25. Pushed through `8aebb26`; six
commits are local only:

- `6f2a6cd` — wire the pie scale options
- `347bb7c` — regroup the config screen by concern
- `28bea80` — fix four visual settings that appeared to do nothing
- `73d1845` — retire `PIE_MENU_COLOR_LIGHTEN_FACTOR`
- `e1a2c52` — retire label inset, de-duplicate the darken toggle, visible label shadow
- `f53d0a5` — keep darken strength out of the list menu

45 unit tests pass. Every visual option has now been manually tested in game, which
is how most of the items below were found.

**The mod works. Everything below is improvement, not repair.**

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
- [x] `SECTOR_GRADATION` — shades each wedge from a lighter inner edge outward. Free to
      draw, because `RingRenderer.drawRing` already took separate inner and outer colours
- [x] `ANIMATE_PIE_MENU` — 180 ms eased open animation. **Open only**; closing stays
      immediate because delaying teardown would hold input after the selection is
      already committed, and that is the most delicate path in the mod
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

Still exposed but unwired, with tooltips that say so:

- [ ] `USE_SOFTWARE_RENDERING` — no runtime effect since the 1.21.5 renderer rewrite
- [ ] `LAZY_CONFLICT_CHECK` — no effect; conflicts are always refreshed
- [ ] `USE_KEYBIND_FIX` — legacy 1.21.x fix, no longer referenced

All three are 1.21.x leftovers and the next thing to retire. `VERBOSE_DEBUG` is wired
but is currently just a higher-verbosity subset of `DEBUG`; give it a distinct meaning
or document the relationship.

### P0.2 Non-vanilla rebinding notification

- [ ] Conflict index refreshes on startup, world join, keybind-screen close, config save,
      and priority changes. A mod that changes a binding at runtime (via its own config
      screen) is not detected. Needs an explicit adapter notification rather than a
      polling hack.

### P0.3 Release readiness

- [ ] Audit `fabric.mod.json`: version, Java dependency, Fabric API range, links, and
      description all need to reflect 26.2 and the real loader support
- [ ] `README.md` still describes 1.21.x behaviour and mentions owo-lib, which the 26.2
      branch does not use
- [ ] `CHANGELOG_LATEST.md` predates the 26.2 port
- [ ] `gradle.properties` is tracked despite being listed in `.gitignore`; resolve
- [ ] `jars/` contains stale 1.21.1 Fabric and NeoForge artifacts with different
      licensing metadata. Not releases — delete or quarantine
- [ ] First tagged release, then Modrinth + CurseForge + GitHub releases in sync

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
- [ ] The three remaining unwired 1.21.x options (`USE_SOFTWARE_RENDERING`,
      `LAZY_CONFLICT_CHECK`, `USE_KEYBIND_FIX`) — retire them as part of release prep?
- [ ] `VERBOSE_DEBUG` currently only means "more `DEBUG` lines". Give it its own meaning,
      or document it as an alias?
- [ ] Profiles: per-world, per-server, or global?
- [ ] Is the `debug` category ever going to get a real English label in 26.2? If not, the
      legacy `FILTER_DEBUG_KEYS → [Debug]` migration should be retired
- [ ] Release tag format, and whether 26.2 ships as a new major version given the
      Minecraft-version jump
- [ ] Should the pie scale defaults stay at `0.8`/`0.2`? Those are the values the code
      always used; the `0.6`/`0.25` the config file once advertised were never in effect.
      Current behaviour was kept deliberately, but it was never a choice you made

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
