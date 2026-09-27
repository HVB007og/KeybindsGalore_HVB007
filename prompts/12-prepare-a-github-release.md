# Prompt: prepare a GitHub release

Paste this, then name the version, for example *"prepare the GitHub release for 1.9.0+26.3"*.

---

You are cutting a GitHub release for KeybindsGalore, a client-side Fabric mod for Minecraft.
I am not a programmer. I want the tag, the release notes, and both attached files correct, and
I want to know exactly what to click or run.

This covers the **GitHub** side only. Modrinth, CurseForge, the changelog, and the README are
handled by `03-release-and-modrinth.md`. The two overlap in one place: **Modrinth accepts one
file per version, so the sources jar is attached here and nowhere else.** That is the reason
this prompt exists separately.

## Read first

`AGENTS.md`, `ROADMAP.md`, `RELEASE_CHECKLIST.md`, and `bkpjar/README.md`.

## The three artifacts

| Artifact | Modrinth | GitHub release |
|---|---|---|
| Mod jar | yes | yes |
| Sources jar | **no**, one file per version | yes |
| SHA256SUMS | no | optional, useful |

Keep the sources jar. Other modders decompile against it, and it is the only published view of
the source for a given build.

## Steps

### 1. Confirm the build is the one being released

The release must be built from an **archived** jar in `bkpjar/<version>/`, not from a fresh
`build/`. Archiving happened before upload precisely so the backup is byte-identical to what
ships. If the archive is missing, stop and say so; do not rebuild and assume it matches.

Verify:

- the jar filename matches the version
- `fabric.mod.json` inside the jar reports the same version
- `depends.minecraft` and the Fabric API range are correct
- the LGPL licence file is embedded
- `bkpjar/SHA256SUMS.txt` contains an entry for both jars

Report the SHA256 of each file you attach.

### 2. Confirm the tag target

The tag must point at the commit that produced the build, on the branch for that Minecraft
version. Multiple branches exist here, for example `recovery/26.2` and `recovery/26.3-fabric`,
so this is easy to get wrong.

Ask me which branch the version belongs to if it is not obvious from the version string, and
show me the commit you are about to tag **before** creating anything.

### 3. Write the release notes

These are the GitHub release notes, so they should read differently from the Modrinth
description. Modrinth is marketing copy; GitHub is a change log.

- Lead with what a player would notice, not with a file list
- Group as **Added**, **Fixed**, **Changed**, **Removed**, **Requirements**, **Known
  limitations**
- For a Minecraft port, say plainly what platform changes were needed. The 26.3 port replaced
  GLFW with SDL3 and moved the GUI render pipeline, and that is exactly the kind of thing a
  player or a second modder needs to know
- List retired config options and what replaced each
- **Never imply something was verified that was not.** If a visual change has not been seen in
  game, do not describe how it looks
- Credit contributors and the original author. This mod's lineage matters and the platform
  expects it

### 4. Confirm before creating anything irreversible

Show me, and wait for approval:

- the exact tag name
- the commit it will point at
- the files you will attach, with sizes and checksums
- the release notes, in full
- whether it will be marked as a prerelease

Tags and releases are awkward to undo cleanly. Do not create them in the same turn you plan
them unless I have already told you to go ahead.

### 5. Create it

Only after I approve:

```powershell
git tag -a v<version> -m "KeybindsGalore <version> for Minecraft <mc>"
git push origin v<version>
gh release create v<version> `
  "bkpjar\<version>\keybindsgalore-<version>.jar" `
  "bkpjar\<version>\keybindsgalore-<version>-sources.jar" `
  --title "<version>" `
  --notes-file <notes-file>
```

Adjust the loader and Minecraft version in the tag message. Verify afterwards with
`gh release view` that both assets are attached, and confirm the working tree is still clean.

## Rules

- Do not rebuild during this task. If the archive is wrong, tell me and stop.
- Do not tag the wrong branch. Show me the commit first.
- Do not delete or move an existing tag. If one already exists for the version, stop and tell
  me; a moved tag silently breaks anyone's clone.
- Do not mark something a prerelease unless I ask.
- If the repository has more than one branch carrying a mod, say which branch each release
  belongs to in your summary.
