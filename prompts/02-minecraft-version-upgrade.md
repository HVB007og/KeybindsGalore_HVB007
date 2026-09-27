# Prompt: port the mod to a new Minecraft version

Paste this, then name the target version, for example *"port to 26.4"*.

---

You are porting KeybindsGalore, a client-side mod that ships on both Fabric and NeoForge, to a
new Minecraft version. I am not a programmer. Work autonomously, commit in small steps, and push
after each working milestone. Ask me questions now, before you start, not one at a time later.

**Confirm which loader and branch you are porting.** A new Minecraft version has to be ported
**per loader branch**, because each branch carries its own build system, metadata file, and
entrypoint. Porting Fabric 26.3 does not give you NeoForge 26.3. Check whether the target
NeoForge version exists at all, and whether it is still only published as a beta.

**A Minecraft port that changes no mod behaviour keeps the same version number.** Do not bump
it. The 26.3 Fabric build shipped as `1.8.0`, the same number as 26.2, because it only adapted
to engine changes.

## Before touching anything

Read, in this order:

1. `AGENTS.md` — runtime invariants, build commands, style rules
2. `ROADMAP.md` — current state
3. `AUTONOMOUS_PLAN.md` — how the 26.3 port went, including the mistakes
4. `gradle.properties`, `build.gradle`, `settings.gradle`
5. The **target branch's** loader metadata: `src/main/resources/fabric.mod.json` on a Fabric
   branch, `src/main/resources/META-INF/neoforge.mods.toml` on a NeoForge branch, plus
   `keybindsgalore.mixins.json`

Then confirm with me: current version, target version, loader, and which branch to work on.
Never work on a branch that is a verified release. Create or use a branch like
`recovery/<version>-fabric` or `recovery/<version>-neoforge`.

## Do not trust the changelog

The 26.3 changelog described a content update. The actual jar contained two platform breaks
that were invisible until the code was compiled against it:

- **GLFW was replaced with SDL3.** `org.lwjgl.glfw` disappeared, `InputConstants.Type.KEYSYM`
  became `Type.KEYBOARD`, and key values changed from GLFW keycodes to SDL scancodes.
- **The GUI render pipeline moved** to `com.mojang.renderpearl.api.pipeline`.

So: bump the versions, compile, and let the compiler find the breaks. Then **inspect the
target jar directly** for anything the compiler cannot catch. Do not assume a silent
breakage is absent because it compiled.

## Finding the dependency versions

The single most common way to lose time here is using a **Modrinth version string as a Maven
coordinate**. Modrinth appends a loader suffix such as `+fabric` that does not exist in Maven.

- Cloth Config: Modrinth showed `26.3.159+fabric`. The Maven coordinate is `26.3.159`.
- Fetch the real coordinate from the project's own Maven metadata, for example
  `https://maven.shedaniel.me/me/shedaniel/cloth/cloth-config-fabric/maven-metadata.xml`.
- Verify every version is a **stable release**, not a beta, unless there is no alternative.

## Environment

- Java 25: `C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.0.1\jbr`
- Set `JAVA_HOME` to that in every build command.
- **Always run this before Gradle, it is not optional:**
  ```powershell
  attrib.exe -R "C:\Users\HVB\Desktop\Projects\MC Mod\KeybindsGalore_HVB007\build\*" /S /D
  ```
  Loom writes read-only files into `build/`, and a later Gradle run fails to clean them.
- The first build for a new Minecraft version takes 15 to 30 minutes while Loom downloads and
  decompiles. **Run it in the background and poll.** Do not sit blocking on it and do not
  assume it has hung.

## What to verify, in this order

1. `.\gradlew.bat build` — compiles, and all 45+ unit tests pass.
2. Inspect the new jar for the mixin targets the mod depends on, and confirm each still
   exists with the same shape. These are the things a compiler will **not** catch:
   - `KeyMapping.set`, `KeyMapping.click`, `KeyMapping.setDown`
   - the `key`, `isDown`, `clickCount` fields that `KeyMappingAccessor` reads
   - `KeyMapping.Category.id()` and `.label()`
   - `Screen.keyReleased` / `mouseReleased`, `KeyEvent`, `MouseButtonEvent`
   - `RenderPipelines.GUI` and whatever type `GuiElementRenderState.pipeline()` now returns
   - `Minecraft.player` and the `Window` handle if used
3. Run the client: `.\gradlew.bat runClient`. It will open a window and keep running. That is
   expected. Wait for the log, then confirm and stop the process.
4. Confirm from `run/logs/latest.log`:
   - the mod loads with the new version
   - **zero mixin errors** — search for `mixin` together with `error`, `fail`, or `invalid`
   - `Scanning for conflicting keybinds (STARTUP)` appears
5. Update `fabric.mod.json`: `depends.minecraft` to the new version, the Fabric API range, the
   Cloth Config and ModMenu ranges, and the description text. Keep `~` on the Minecraft version
   so a later patch cannot silently install an untested build.
6. Bump `mod_version` in `gradle.properties`. Modrinth will not accept a duplicate version
   string, so it must be new.

## Things that are easy to get wrong

- **The build does not validate `en_us.json`.** Resources are copied verbatim, so a malformed
  language file compiles cleanly and fails at runtime with missing translation keys. After
  editing it, parse it explicitly. A trailing comma on the final entry is the easy mistake.
- **Never bind one config field to two widgets.** Two toggles writing the same setting revert
  each other on save, because both initialise from the same stale value. Shared settings get
  exactly one control, in the tab that matches what they affect.
- If you edit config across many files with a script, **verify afterwards**. A bulk removal
  once deleted an unrelated field and broke the snapshot constructor, and another bulk edit
  deleted six unrelated GUI entries. Build and run the parity check below.
- Config parity check after any config change: the GUI, `keybindsgalore.properties`, the
  snapshot codec, and `en_us.json` must all agree on the same set of options.
- Retiring a config option is a **three-step operation**, not a deletion. See `AGENTS.md`.
  Deleting the key outright makes `ConfigManager` log an error for every existing user.

## Reporting

After each milestone, commit and push. Then tell me:

- what you changed and why
- what you verified, and how
- **what remains unverified and needs a human** — especially anything visual, and any
  constant you derived rather than read from vanilla source

Do not describe a port as finished because it compiles. A port that compiles but has never
been launched is not a port, and neither is one that launches but has never been played.
