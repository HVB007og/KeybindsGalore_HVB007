# Autonomous work plan — 26.3 port and NeoForge

Requested 2026-09-26. Three goals, in priority order:

1. Port to **Minecraft 26.3** on Fabric
2. Build an **automated test harness** that drives key input, produces logs, and loops
3. Port to **NeoForge** for both 26.2 and 26.3

## Verified feasibility, checked 2026-09-26

| Question | Answer | Source |
|---|---|---|
| Does 26.3 exist? | **Yes**, released 15 Sep 2026, "Wilderness Bound" | minecraft.net |
| Does NeoForge support 26.2? | **Yes**, `26.2.0.75` stable | projects.neoforged.net |
| Does NeoForge support 26.3? | Listed on neoforged.net, likely beta | neoforged.net |
| Is 26.2 → 26.3 a small change? | **No.** Content update *plus* two platform breaks: GLFW replaced by SDL3, and the GUI render pipeline moved to `renderpearl`. Found by inspecting the 26.3 jar, not the changelog. | 26.3 jar inspection |

## Goal 1 outcome — verified working

**Closed.** `recovery/26.3-fabric`, version `1.9.0+26.3`.

- Compiles; 45 unit tests pass
- Client launches as `1.9.0+26.3`; all mixins apply with no injection errors
- Startup conflict scan runs on 26.3
- The user confirmed in game that it works

Two platform changes were needed, both found by inspecting the 26.3 jar:

- **GLFW replaced with SDL3.** `org.lwjgl.glfw` is gone, `InputConstants.Type.KEYSYM`
  became `Type.KEYBOARD`, and key values are now SDL scancodes. Confirmed by
  `InputConstants` calling `SDL_GetKeyFromScancode` and `SDL_GetKeyName`.
- **The render pipeline moved** to `com.mojang.renderpearl.api.pipeline`. `RingRenderer`
  needed only its import changed; `RenderPipelines.GUI` still exists.

Verified as unchanged, so the mixins were left alone: `KeyMapping.set/click/setDown`, the
accessor fields, `Category.id()/label()`, `KeyEvent`, `MouseButtonEvent`.

Still worth one check, because the user called their own testing limited: the **K capture
hotkey**. The SDL scancode for K was derived from `SDL_SCANCODE_A = 4` rather than read
from vanilla source, since letter keys have no named constant on `InputConstants`. If K
does not open the capture screen, the fix is to rebind it in the vanilla Controls screen.
Pie visual fidelity and the full 26.2 manual matrix have not been re-run on 26.3.

## Honest assessment of each goal

### Goal 1 — 26.3 Fabric port: done

Expected work: bump `minecraft_version` and the Fabric API version, build, fix whatever
the compiler reports, then look for silent breakages in the mixin targets and re-run the
unit tests.

**What I can verify:** compilation, 45 unit tests, mixin application at launch, and log
inspection. **What I cannot verify:** that the pie menu renders correctly, that hover
geometry is right, that config saves work. Those have always needed a human.

Risk points, in order of likelihood:
- `KeyMapping.set` / `click` / `setDown` injection points changing
- `KeyMapping.Category` ids or labels changing
- `Screen.keyReleased` / `KeyEvent` / `MouseButtonEvent` signatures
- `Minecraft.player`, `LocalPlayer`, `Window` accessors
- `GuiGraphicsExtractor` and `RenderPipelines.GUI` for the pie renderer

### Goal 2 — automated key-input harness: partially achievable, with a hard limit

I can build a harness that launches the client, drives input, captures the log, and loops
"build → run → read log → fix". That has real value: it catches mixin failures, crashes,
and any error the mod logs.

**The limit, stated plainly:** it cannot verify anything visual. Every genuine bug found
so far in this project came from the user clicking around, not from the tests:

- label inset applied in screen space instead of radially
- `DARKENED_BACKGROUND` bound to two widgets, reverting itself
- three list-menu options sitting in the pie-menu tab
- label text shadow hardcoded on, ignoring its own setting
- vertex budget making the cancel circle faceted before the pie

The 45 unit tests caught exactly one of those, a config default. So a loop can find
crashes and log errors, and cannot find "the pie looks wrong". A harness that appears to
pass is not evidence the UI works.

### Goal 3 — NeoForge ports: real work, and I cannot verify them

This is a substantially larger project than the 26.3 bump, and it is the one most likely
to end in something that compiles but does not run:

- Different build system. Fabric Loom is replaced by ModDevGradle.
- Different mixin bootstrap and configuration, and separate mixin configs per loader.
- `neoforge.mods.toml` instead of `fabric.mod.json` metadata.
- Cloth Config publishes a separate NeoForge artifact with a different coordinate.
- ModMenu is Fabric/Modrinth-specific. NeoForge registers its config screen through a
  different extension point, so the in-game config screen needs a NeoForge-specific path.
- The first build downloads and decompiles Minecraft through the NeoForm toolchain, which
  is slow and can fail for reasons outside this repository.

Doing it twice, for 26.2 and 26.3, doubles it.

**The real risk is publishing a broken port.** Everything in this repository so far was
manually verified before being committed. A NeoForge jar I cannot launch is a guess.

## Decisions needed from the user

Recorded here so the answers survive even if the conversation does not.

- [ ] **NeoForge 26.3 stability.** Target 26.2 NeoForge first, and only attempt 26.3 once
      26.2 works? Default if unanswered: yes, 26.2 first.
- [ ] **Repository layout.** One branch per target
      (`recovery/26.2-fabric`, `recovery/26.3-fabric`, `recovery/26.2-neoforge`,
      `recovery/26.3-neoforge`), or a multi-loader Gradle layout with a shared common
      source set? Default if unanswered: separate branches, because a multi-loader layout
      is a restructure of a working, verified project and is far riskier unattended.
- [ ] **May untested code be pushed to the public repository?** Default if unanswered:
      **no.** Work lands on local branches and is only pushed after it compiles, tests
      pass, and the mixins are confirmed to apply at launch. Anything less would put an
      unverified jar on a repository with 21.8k downloads.
- [ ] **ModMenu on NeoForge.** Port the in-game config screen through NeoForge's own
      extension point, or ship NeoForge builds with file-only configuration? Default if
      unanswered: port the screen, since a NeoForge build without a config screen is a
      worse product.

## Definition of done per goal

Stated up front so "finished" is not a matter of opinion later.

- **26.3 Fabric:** compiles, all unit tests pass, client launches without mixin failure,
  conflict detection logs correct refreshes. Visual correctness explicitly *not* claimed.
- **Harness:** launches the client unattended, drives a scripted key sequence, writes a
  log, and exits with a non-zero code on any error-level log line or crash. Documented as
  covering crashes and log errors only, not visual correctness.
- **NeoForge:** compiles against real NeoForge artifacts, metadata is valid, mixins
  apply. Runtime behaviour unverified until someone launches it.

## Rule for unattended work

If a goal cannot be completed without a human, stop and record the blocker rather than
guessing. A half-finished port on a branch is recoverable. A confidently wrong commit
pushed to a public repository is not.
