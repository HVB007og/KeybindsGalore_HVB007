# Release checklist — 1.8.0+26.2

Everything that has to be filled in on Modrinth, plus the values to paste. Generated
from the state of the `recovery/26.2` branch.

---

## 1. The files to publish

**Modrinth** accepts exactly one file per version, so it gets the mod jar only:

| | |
|---|---|
| **File** | `jars/keybindsgalore-1.8.0+26.2.jar` (139 KB) |
| **Version ID / name** | `1.8.0+26.2` |
| **Version number** | `1.8.0` |
| **Game version** | `26.2` |
| **Loader** | `Fabric` |
| **Side** | `Client-only` |

**GitHub Release** accepts multiple assets, so both jars go on the release:

| | |
|---|---|
| Mod jar | `keybindsgalore-1.8.0+26.2.jar` |
| Sources jar | `keybindsgalore-1.8.0+26.2-sources.jar` (80 KB, 50 `.java` files) |

Verified inside the built jar: id `keybindsgalore`, version `1.8.0+26.2`, 60 classes,
`fabric.mod.json`, mixins config, default properties, icon, `en_us.json`, and the LGPL
licence file. No stale classes or assets from earlier Minecraft versions.

---

## 2. Dependencies — both required

| Dependency | Version range |
|---|---|
| Minecraft | `26.2` |
| Fabric Loader | `>=0.19.3` |
| Fabric API | `>=0.152.1+26.2` |
| Cloth Config | `>=26.2.155` |
| ModMenu | `>=20.0.2` (optional) |

Java 25 or newer is required. This cannot be expressed in Modrinth's dependency list, so it
belongs in the description's Requirements section.

**Cloth Config is required, not optional.** The config screen is built from Cloth Config
classes, so a user with ModMenu but no Cloth Config crashes opening it. This is declared as
a hard dependency in `fabric.mod.json` and should match on Modrinth.

---

## 3. Description body

Paste the full contents of [`MODRINTH_DESCRIPTION.md`](MODRINTH_DESCRIPTION.md).

The headline change to lead with: **the config screen now works on 26.2.** The published
1.7.2+26.2 listed it as a known issue and told users to edit the properties file by hand.
That is fixed.

---

## 4. Changelog entry

Paste the contents of [`CHANGELOG_LATEST.md`](CHANGELOG_LATEST.md), minus the top-level
`# Changelog` heading, as the version's changelog body.

---

## 5. Fields that likely need no change

These already match the repository:

- **Licence:** `LGPL-3.0-only`
- **Categories / tags:** `utility`, `management` (currently shows Utility, Management,
  Technology, Game Mechanics, Optimization — the first two are the honest ones)
- **Platforms:** `fabric` only. The NeoForge entry on the page refers to a 1.21.1 build that
  is not maintained here.
- **Client-side only:** correct, this is a client mod.

---

## 6. Gallery — replace all three existing images

The current gallery is three screenshots from 2023–2024, all captioned `<1.21.1`, showing a
selector UI that no longer exists. None of them represent the current mod.

Before capturing: set `SHOW_CONFLICT_WARNINGS=false` in the config so chat spam does not
appear in the shots, and confirm `DEBUG=false`. Use **1920×1080**.

Priority order:

1. **Pie menu, mid-conflict, one wedge hovered** — the hero image. Use a key with 5–6
   conflicts so the gradient, the hover highlight, and the sector expansion are all
   visible at once.
2. **Config screen → Visual (Pie Menu)** — shows the sliders and colour fields. Valuable
   specifically because the previous release's config screen was broken, so this is the
   proof it works.
3. **Config screen → Behaviour** — shows the Priority Actions list, which people cannot
   picture from the name alone.
4. **K-key capture flow** — the "Press any key…" screen and then the action list. Signature
   feature, poorly documented anywhere else.
5. **List menu** — an entire mode most players will never discover.
6. **Red conflict warning in chat** — easy to capture, shows the priority system announcing
   itself.

Avoid reusing the old "Conflicted Keys" vanilla-settings shot as a feature image; it shows
Minecraft's own control panel, not the mod.

---

## 7. Archive the build, then publish

Do this **before** uploading, so the archive is guaranteed to match what ships.

```powershell
attrib.exe -R "C:\Users\HVB\Desktop\Projects\MC Mod\KeybindsGalore_HVB007\build\*" /S /D
.\gradlew.bat build
$version = "1.8.0+26.2"
New-Item -ItemType Directory -Force -Path ".\bkpjar\$version"
Copy-Item ".\build\libs\keybindsgalore-$version.jar" ".\bkpjar\$version\"
Copy-Item ".\build\libs\keybindsgalore-$version-sources.jar" ".\bkpjar\$version\"
Copy-Item ".\build\libs\keybindsgalore-$version.jar" ".\jars\"
```

Then regenerate the checksums:

```powershell
Get-ChildItem ".\bkpjar" -Recurse -Filter "*.jar" | Sort-Object FullName | ForEach-Object {
  $rel = $_.FullName.Replace("$PWD\bkpjar\","").Replace("\","/")
  "{0}  {1}" -f (Get-FileHash $_.FullName -Algorithm SHA256).Hash.ToLower(), $rel
} | Set-Content ".\bkpjar\SHA256SUMS.txt"
```

`bkpjar/` is the durable local record of every published build. It is git-ignored, because
build outputs do not belong in history and the authoritative copy is the Modrinth version
plus the GitHub release assets. See `bkpjar/README.md`.

## 8. Publish

1. **Modrinth** — create version `1.8.0+26.2`, upload the mod jar, paste the description
   from `MODRINTH_DESCRIPTION.md` and the changelog from `CHANGELOG_LATEST.md`.
2. **GitHub** — tag and create a release with both jars attached:

   ```powershell
   git tag -a v1.8.0+26.2 -m "KeybindsGalore 1.8.0 for Minecraft 26.2"
   git push origin v1.8.0+26.2
   ```

   Then attach `keybindsgalore-1.8.0+26.2.jar` and
   `keybindsgalore-1.8.0+26.2-sources.jar` to the release on GitHub.
3. **CurseForge** — mirror the Modrinth version if you still publish there.
4. Leave the previous `1.7.2+26.2` published. Players move to 1.8.0, and the changelog
   tells anyone who was told to hand-edit the properties file that they no longer need to.

---

## Notes for whoever writes the announcement

The user-visible story is: the mod now detects conflicts more reliably, the pie menu looks
considerably better and is properly configurable, and several settings that appeared in the
config screen but did nothing have been fixed or removed. Anyone who was told to hand-edit
`keybindsgalore.properties` on 26.2 should switch to the in-game screen.
