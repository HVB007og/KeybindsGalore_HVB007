# Prompt: add a new feature safely

Paste this, then describe the feature.

---

You are adding a feature to KeybindsGalore, a client-side Fabric mod for Minecraft that
resolves keybind conflicts. I am not a programmer. Explain what you are doing, ask me when a
genuine product decision is needed, and otherwise work autonomously.

## Read first

`AGENTS.md` for the runtime invariants and architecture, and `ROADMAP.md`, because the feature
you have been asked for may already be described there with notes from previous attempts.

## Before you write code, settle these

1. **Is it already planned?** `ROADMAP.md` may already describe it, including why an earlier
   approach was rejected. Read that first.
2. **Which layer does it belong in?** The architecture is deliberate:
   - `core` — platform-neutral rules. Must not import Minecraft, Fabric, GUI, config, or API.
   - `config` — persistence and settings. No Minecraft UI.
   - `input/minecraft` — input and mixin adapters feeding core ports.
   - `ui/model` — pure geometry and selection state.
   - `ui/minecraft` — screens and adapters. **No input policy decisions.**
   - `api` — the public contract, the only place Javadoc belongs.
3. **Does it need a new config option?** If yes, see the contract below. This is the part most
   often done wrong.
4. **Can the logic be made pure?** If it can, put it in `core` or `ui/model` and unit test it.
   That is why this project has 45 tests running in nine seconds.

## The input path is the fragile part

This mod's whole job sits on one delicate path: a key press arrives, we decide who owns it,
and we either open a selector, run a priority action, or let vanilla have it. Every past
regression lived here. Before changing anything in that path, read the **Runtime invariants**
section of `AGENTS.md` and re-read `MinecraftInputController` and both selector screens.

Non-negotiable behaviours:

- A single press, several rapid presses, and a long 5 second hold must each produce **exactly
  one** selector session.
- The pie menu is intentionally **mouse only** and does **not** pause the game. Do not change
  either without asking; if the feature needs keyboard support, that is a design decision.
- Releases for an open screen arrive via `Screen.keyReleased`, not through the mixin. Do not
  unify those two paths.
- Do not add a key-state poll, a latch, or a cooldown to guard against a problem you have not
  reproduced on a deliberate hold.

## If it needs a config option, all of these must change together

Missing any one produces a setting that is persisted but unreachable, or editable but
discarded:

1. `config/ConfigurationSnapshot.java` — the typed field
2. `ConfigurationSnapshot.defaults()` — **the single source of truth for defaults**
3. `config/ConfigurationSnapshotCodec.java` — read and write
4. `Configurations.java` — field, `snapshot()`, and `apply()`
5. `src/main/resources/keybindsgalore.properties` — the shipped file, with a comment
6. `configmanager/ConfigScreenBuilder.java` — the widget
7. `assets/keybindsgalore/lang/en_us.json` — label and tooltip
8. A test, and `AGENTS.md` if the option changes a documented rule

Rules that follow from this:

- **Bind each config field to exactly one widget.** Two toggles writing one setting revert each
  other on save.
- **Place it in the right tab.** Visual is anything that only changes how a menu looks or
  feels. Behaviour is conflict detection and priority resolution. General is diagnostics,
  input handling, and anything shared by both selectors.
- **If it is a colour**, use `startColorField` with `setAlphaMode(true)`. Note that
  `startIntSlider` takes min and max as **arguments**, while `startIntField` uses
  `setMin`/`setMax`.
- **The build does not validate `en_us.json`.** Parse it yourself after editing. A trailing
  comma on the last entry is invalid JSON and will ship.
- **Never make a setting that does nothing.** Either wire it or label it honestly as not
  implemented. A slider that appears to work but does not is the worst outcome available.

## Before you finish

- `.\gradlew.bat build` passes, all tests green.
- Config parity holds: GUI, properties file, codec, and language file agree.
- The new option's tooltip describes what the player will actually see.
- You have told me **what is verified and what still needs a human**, especially anything
  visual. This project has no way to verify appearance without me playing it.
