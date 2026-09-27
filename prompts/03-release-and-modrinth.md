# Prompt: prepare a release and get Modrinth ready

Paste this, then name the version if you know it, for example *"prepare 1.9.0+26.3"*.

---

You are preparing a KeybindsGalore release. I am not a programmer. I want everything finished
and correct so that uploading takes me minutes, not an evening. Work autonomously, commit in
small steps, push after each milestone, and ask me any questions up front.

## Before anything else

Read `AGENTS.md`, `ROADMAP.md`, and `RELEASE_CHECKLIST.md`, plus `gradle.properties` and
`src/main/resources/fabric.mod.json`.

Confirm with me: the version string, and whether the build is already verified in game. If it
has not been tested, say so plainly and tell me what is untested, rather than writing release
notes that imply otherwise.

## The order of work

### 1. Version

Modrinth **will not accept a duplicate version string**. If the version already exists on
Modrinth, it must be bumped. Choose the bump honestly:

- **patch** — bug fixes only
- **minor** — features added, or configuration options removed

Update `mod_version` in `gradle.properties`. That feeds the jar name, the mod metadata, and
the Modrinth version id.

### 2. Build and verify the jar

```powershell
attrib.exe -R "C:\Users\HVB\Desktop\Projects\MC Mod\KeybindsGalore_HVB007\build\*" /S /D
$env:JAVA_HOME="C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.0.1\jbr"
.\gradlew.bat clean build
```

**Do not trust a green build.** Open the produced jar and confirm:

- `fabric.mod.json` version matches, and `depends.minecraft` is correct
- the mixin config, `keybindsgalore.properties`, the icon, and `en_us.json` are all present
- the **LGPL licence file is embedded** (`jar { from("LICENSE") }` in `build.gradle`)
- **no stale assets or classes** from earlier Minecraft versions. This project once shipped a
  whole language file belonging to a different historical mod, and two different icons.
- report the class count and the jar size

### 3. Archive before publishing, not after

Copy **both** jars into `bkpjar/<version>/`, then regenerate `bkpjar/SHA256SUMS.txt`.
Archiving first guarantees the backup is byte-identical to what ships.
`RELEASE_CHECKLIST.md` step 7 has the commands. The jars are git-ignored; do not commit them.

### 4. Documentation

Bring all of these in line with the current build. The recurring failure is documentation
describing a previous Minecraft version, so check every version number and every claim:

- `README.md` — user facing. Must not contain mojibake; check for the replacement character.
- `CHANGELOG_LATEST.md` — must describe **this** release, not the last one.
- `MODRINTH_DESCRIPTION.md` — the Modrinth page body.
- `ROADMAP.md` — tick what shipped, add what is new.
- `RELEASE_CHECKLIST.md` — refresh the version-specific values.

**Claim audit.** The old README was wrong in four ways: it described a rendering library the
branch does not use, promised an RGB colour picker that does not exist, named a category that
does not exist in this version, and claimed "zero dependencies". Check every technical claim
against the current code. Where the mod has a known limitation, say so in a **Known
limitations** section rather than hiding it.

### 5. `fabric.mod.json` review

- `depends.minecraft` must use `~` so a later patch cannot silently install an untested build
- Java and Fabric API ranges match `gradle.properties`
- Any library the code genuinely cannot run without must be in `depends`, not `recommends`.
  This project had Cloth Config in `recommends` while the config screen was built from Cloth
  Config classes, so a user with ModMenu but no Cloth Config would have **crashed**
- a `contact` block with project links
- a description that states what the mod does, not a changelog

### 6. Repository hygiene

Check and report, fixing what is safe:

- **any jar committed to the repository** — build outputs do not belong in history
- **`gradle.properties` both tracked and ignored** — it pins the version baseline and must be
  tracked, so the ignore rule is what is wrong
- generated output or stale directories tracked
- `AGENTS.md` and `.opencode/` must stay **untracked**; they are local agent memory.
  `ROADMAP.md` is tracked, it is project documentation.

### 7. Modrinth hand-off

Produce a final hand-off containing:

- **File**: the mod jar. Modrinth takes **one file per version**, so the sources jar cannot go
  there. It goes on the GitHub release instead.
- **Version name** and **version number**, split sensibly. `1.9.0+26.3` is name `1.9.0+26.3`,
  number `1.9.0`.
- **Game version**, **loader**, **side**
- the **dependency table**, and note anything that cannot be expressed there, such as a Java
  version requirement
- the description body and the changelog body, ready to paste
- which existing page fields need **no** change
- the **screenshot plan**, if the gallery is stale

### 8. GitHub release

Give me the exact commands for the tag, and state that **both** jars attach to the release.
Do not push the tag until I confirm the Modrinth upload succeeded.

## Screenshots

Check `MODRINTH_DESCRIPTION.md` and the existing gallery. If the gallery images predate the
current UI, say so plainly and give me a numbered shot list with a one-line caption and alt
text for each. Always lead with the strongest single image: it becomes the project's preview
thumbnail in search results.

## Rules

- Never state that something is verified unless you actually verified it.
- Never leave a doc describing an older Minecraft version.
- Never silently change a version number. Tell me what you changed and why.
- Do not commit or push until I have seen the summary, unless I have already asked you to.
