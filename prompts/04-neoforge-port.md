# Prompt: port the mod to NeoForge

Paste this, then name the target Minecraft version, for example *"port to NeoForge 26.2"*.

---

You are porting KeybindsGalore from Fabric to NeoForge. I am not a programmer. Work
autonomously, commit in small steps, push after each working milestone, and ask me all your
questions now rather than one at a time later.

## Understand the shape of this job

This is **not** a version bump. The 26.3 port was four import and constant changes. This is a
port to a different mod loader, which means a different build system, different metadata,
different config-screen registration, and separate mixin configuration. Budget accordingly and
do not report progress as though it were a small edit.

Expect roughly: build system, metadata, entrypoints, config screen, mixin wiring, and then a
long tail of runtime differences that only appear at launch.

## Before anything else

Read `AGENTS.md`, `ROADMAP.md`, and `AUTONOMOUS_PLAN.md`, then `build.gradle`,
`gradle.properties`, and `src/main/resources/fabric.mod.json`.

I have already decided, do not re-litigate:

- **Separate branches, not a multi-loader layout.** No shared common source set. Duplicate the
  code across branches. A multi-loader restructure of a working, verified project is far too
  risky to do unattended.
- **Do not touch the verified release branch.** Work on something like `recovery/26.2-neoforge`.
- **The in-game config screen must be ported**, not dropped. A NeoForge build without a config
  screen is a worse product than the Fabric one.
- **Untested code may be committed and pushed.** It is my backup. Still write down clearly what
  is unverified.

## What differs from Fabric

| Concern | Fabric | NeoForge |
|---|---|---|
| Build plugin | Fabric Loom | ModDevGradle |
| Mod metadata | `fabric.mod.json` | `neoforge.mods.toml` |
| Client entrypoint | `ClientModInitializer` | `@Mod` with a client-side constructor |
| Config screen | ModMenu `ModMenuApi` | NeoForge's own extension point |
| Cloth Config artifact | `cloth-config-fabric` | the NeoForge artifact, different coordinate |
| Mixins | `keybindsgalore.mixins.json` | a separate config, referenced from the metadata |
| Event registration | Fabric API events | NeoForge event bus |

**ModMenu is Fabric-specific.** It will not exist on NeoForge. That is why the config screen
needs its own path. Find the actual extension point in the NeoForge version you are targeting;
do not assume it matches the old Forge `IConfigScreenFactory` name.

## The mod's own constraints

Read `AGENTS.md` for these, they are the reason a naive port breaks:

- The mod hooks `KeyMapping.set`, `KeyMapping.click`, and `KeyMapping.setDown` through mixins,
  and reads the private `key`, `isDown`, and `clickCount` fields through an accessor mixin.
  **All of that must keep working on NeoForge.** This is the highest-risk part of the port,
  because a loader can ship the same Minecraft version with different mixin bootstrap
  requirements.
- The API registers itself on the first client tick, because `Minecraft.options` is null during
  entrypoint initialisation. The same trap almost certainly exists on NeoForge. Find out, do
  not assume.
- `KeybindsGalore.conflictTable` public identity and the public static state are part of the
  contract. Do not restructure them.
- `core`, `ui/model`, and `config` are pure Java and must stay Minecraft-free. They should
  port unchanged. Verify that rather than assuming.

## Order of work

1. **Confirm NeoForge supports the target version**, and get the exact NeoForge version number.
2. Create the branch. Copy the source across.
3. Get an **empty mod compiling** on NeoForge first: build system, metadata, a bare
   `@Mod` client class that logs on startup. Do not port features yet.
4. Confirm the client launches and logs. **This is the gate.** Nothing else matters until the
   skeleton runs.
5. Port the mixins and the accessor. Confirm they apply with no injection errors.
6. Port the entrypoint, config loading, and conflict detection.
7. Port the selectors and the pie renderer.
8. Port the config screen to the NeoForge extension point.
9. Unit tests. The pure layers should pass unchanged; anything that does not is a signal you
   have dragged Minecraft into something that must stay pure.
10. Run the client, check the log, then hand it to me to play.

## Environment

- Java 25: `C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.0.1\jbr`
- **Before every Gradle run, always:**
  ```powershell
  attrib.exe -R "C:\Users\HVB\Desktop\Projects\MC Mod\KeybindsGalore_HVB007\build\*" /S /D
  ```
- The first NeoForge build downloads and decompiles Minecraft through the NeoForm toolchain.
  It is slow, often 20 to 40 minutes, and can fail for reasons outside this repository.
  **Run it in the background and poll.** Do not assume it has hung.

## The honesty rule for this port

**You will not be able to launch a NeoForge client and confirm it works the way you confirmed
26.3.** If you can get as far as "compiles against real NeoForge artifacts, metadata is valid,
mixins apply at load", that is the honest ceiling, and I need it stated as such rather than
implied to be equivalent to a played build.

State clearly, at every milestone:

- what you verified, and how
- what you could not verify
- the single most likely thing to be broken when I first launch it

That last one is genuinely useful to me. Give me a short "try this first" list.

## Stop conditions

If you get properly blocked, do not thrash. Record the blocker in `AUTONOMOUS_PLAN.md`, say
what you tried, and move to the next goal. I would rather have a half-finished branch I can
pick up than a confidently wrong commit.
