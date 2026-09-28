# Release backups

Permanent, local archive of every published build. One directory per version, named with
the exact Modrinth version string.

## Contents

```text
bkpjar/
  SHA256SUMS.txt                                  checksums for every archived jar
  1.8.0+26.2/                                     Fabric, Minecraft 26.2
    keybindsgalore-1.8.0+26.2.jar
    keybindsgalore-1.8.0+26.2-sources.jar
  1.8.0+26.2-neoforge/                            NeoForge, Minecraft 26.2
    keybindsgalore-1.8.0+26.2-neoforge.jar
    keybindsgalore-1.8.0+26.2-neoforge-sources.jar
  1.8.0+26.3/                                     Fabric, Minecraft 26.3
    keybindsgalore-1.8.0+26.3.jar
    keybindsgalore-1.8.0+26.3-sources.jar
  1.8.0+26.3-neoforge/                            NeoForge, Minecraft 26.3
    keybindsgalore-1.8.0+26.3-neoforge.jar
    keybindsgalore-1.8.0+26.3-neoforge-sources.jar
```

The directory name is the **jar version string**, which is not the Modrinth version number. All
four published builds are Modrinth `1.8.0`; the jar name additionally carries the Minecraft
version and, for NeoForge, the loader, so one project can list the same number four times
without ambiguity.

## Why this exists

Modrinth only keeps the most recent files attached to a version, and a version's file can be
replaced or a version removed. `build/` is wiped by `clean` and is git-ignored, so neither
is a reliable archive. This directory is the durable local record of exactly what was
published.

## Rules

- Never edit a directory here. Retired builds stay, so an old version someone still runs can
  always be matched to its binary. The one exception is a build that was published briefly and
  then superseded within the same session, where a corrected rebuild would otherwise leave two
  conflicting binaries of the same release: `1.9.0+26.3` was removed when the 26.3 version
  number was corrected to `1.8.0`.
- Both jars are kept, not just the mod jar, and **both are published to Modrinth and GitHub**.
  The sources jar is a release artefact in its own right, since it is what other modders
  decompile against.
- `SHA256SUMS.txt` is regenerated on every release. Verify with
  `Get-FileHash -Algorithm SHA256` if a download is ever suspect.

## Adding a release

```powershell
attrib.exe -R "C:\Users\HVB\Desktop\Projects\MC Mod\KeybindsGalore_HVB007\build\*" /S /D
.\gradlew.bat build
$version = "<version from gradle.properties>"
New-Item -ItemType Directory -Force -Path ".\bkpjar\$version"
Copy-Item ".\build\libs\keybindsgalore-$version.jar" ".\bkpjar\$version\"
Copy-Item ".\build\libs\keybindsgalore-$version-sources.jar" ".\bkpjar\$version\"
```

Then regenerate `SHA256SUMS.txt` as described in `RELEASE_CHECKLIST.md`.

## Not in version control

`bkpjar/` is git-ignored. Build outputs do not belong in history, and the authoritative
copy of any published build is the Modrinth version plus the GitHub release assets, both of
which are externally backed up. This directory is the local safety net for the case where
both of those are edited or removed.

If this directory is ever lost, every published build can be reconstructed from its git tag
using `git checkout v<version>` followed by a build.
