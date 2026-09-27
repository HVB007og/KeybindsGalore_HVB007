# Prompt: port the mod to NeoForge

Paste this, then name the target Minecraft version, for example *"port to NeoForge 26.2"*.

---

You are porting KeybindsGalore from Fabric to NeoForge. I am not a programmer. Work
autonomously, commit in small steps, push after each working milestone, and ask me all your
questions now rather than one at a time later.

## Understand the shape of this job

This is **not** a version bump, and it is not always a big job either. Measure it before
assuming: count the loader-specific imports in the tree.

For the 26.2 port that count was **7 imports across 50 files**, and the finished port touched
**3 source files**. The conflict engine, config model, and both selector screens were untouched,
because nearly all of the mod is plain Minecraft or plain Java. Time went into build-system
traps, not code:

- ModDevGradle needs `addModdingDependenciesTo sourceSets.test`, or tests that construct
  `KeyMapping` fail to compile
- `foojay-resolver-convention` must be **1.0.0** in `settings.gradle`; 0.9.0 references
  `JvmVendorSpec.IBM_SEMERU`, removed in Gradle 9, and the run tasks then fail hunting for a
  Java 21 toolchain
- the `plugins {}` block in `settings.gradle` must come *after* `pluginManagement {}`

A port to a *newer Minecraft version* on NeoForge costs more than this, because the game
itself changed. Going 26.2 to 26.3 means the SDL3 key handling and the `renderpearl` pipeline
on top of the loader work.

## The gate: get an empty mod launching first

Before porting any feature, get a bare `@Mod(dist = Dist.CLIENT)` class compiling and
launching with the real metadata file. A skeleton that reaches the main menu proves the
toolchain, the version numbers, and the dependency coordinates. Only then bring the real code
across. It also makes any later failure obviously about the port rather than the build.

## Before anything else

Read `AGENTS.md`, `ROADMAP.md`, and `AUTONOMOUS_PLAN.md`, then `build.gradle`,
`gradle.properties`, and the **target branch's** loader metadata
(`src/main/resources/fabric.mod.json` or `src/main/resources/META-INF/neoforge.mods.toml`).

**Check what NeoForge actually publishes before promising a target.** Versions may exist only
as `-beta`, and a version line may stop well short of the newest Minecraft release. Report
what you found rather than assuming parity with the Fabric target.

`run/` and `build/` are shared across branches. If a run launches the wrong loader, check the
log for `net.neoforged.fml` versus `FabricLoader` before suspecting the code, and clear
`build/` after switching branches.

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
