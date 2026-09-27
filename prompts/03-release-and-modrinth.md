# Prompt: prepare and publish a release (Modrinth + GitHub, via API)

Paste this, then name the version if you know it, for example *"publish 1.8.0 for NeoForge"*.

---

You are publishing a KeybindsGalore release. I am not a programmer. I want the whole thing
finished and correct, end to end, without me touching a single form. Work autonomously, commit
in small steps, push after each milestone, and ask me any questions **up front** — not one at a
time as you discover them.

## Non-negotiable rules

- **Never state something is verified unless you actually verified it yourself.** Distinguish
  *built*, *tests passed*, *client launched*, *mixins applied*, and *a human played it*. Only
  the last one means it works.
- **Never guess an identifier.** Modrinth project IDs, loader names, and version IDs must come
  from an API response you actually read. See the trap in step 5; it is a real one I hit.
- **Never publish on a guess I have not confirmed**, especially a version number.
- **Never commit or push** until I have seen the summary, unless I already asked you to.
- If a step fails, report the actual error text. Do not summarise it as "something went wrong".

## Before anything else

Read `AGENTS.md`, `ROADMAP.md`, `AUTONOMOUS_PLAN.md`, `RELEASE_CHECKLIST.md`, `bkpjar/README.md`,
plus `gradle.properties`, `build.gradle`, and the loader metadata for the branch
(`src/main/resources/fabric.mod.json` on Fabric, `src/main/resources/META-INF/neoforge.mods.toml`
on NeoForge).

Confirm with me, all in one message:

- the **version string**
- the **loader**: Fabric, NeoForge, or both as separate versions
- whether the build is **verified in game**, and by whom
- whether the **project description** on Modrinth is current

If it has not been tested, say so plainly and tell me exactly what is untested, rather than
writing release notes that imply otherwise.

## Tokens and credentials

**The Modrinth token is already configured.** It lives in the user environment variable
`MODRINTH_TOKEN`, set once with a 3-month expiry, so nothing needs doing per release:

```powershell
$token = $env:MODRINTH_TOKEN
```

**If `$env:MODRINTH_TOKEN` is empty, it is a stale-process problem, not a missing token.**
`setx` only affects *new* processes, so a shell that was already running will not see it. Read
the registry value instead, and tell me to restart anything that still cannot see it:

```powershell
if (-not $env:MODRINTH_TOKEN) {
    $token = (Get-ItemProperty -Path 'HKCU:\Environment').MODRINTH_TOKEN
}
```

Confirm the token works before relying on it. An empty `POST` to `/v2/version` returns `400` when
the token is valid and `401` when it is not, which distinguishes the two without publishing
anything:

```powershell
curl.exe -s -o nul -w "HTTP %{http_code} (400 = auth ok, 401 = bad token)`n" `
  -X POST -H "Authorization: $token" -H "User-Agent: HVB007/KeybindsGalore-release/1.0" `
  -H "Content-Type: application/json" -d '{}' 'https://api.modrinth.com/v2/version'
```

The token carries `VERSION_CREATE` and `VERSION_READ`. It **cannot** delete a version or touch
the account, which is a deliberately small blast radius for a long-lived credential.

**Never ask me to paste a token into the chat.** Tokens end up in conversation history and must
be treated as exposed. If a token is ever pasted, say so, ask for a fresh one, and tell me to
revoke the pasted one. Use the environment variable; if it is missing entirely, ask me to set it
with `setx` rather than accepting it in a message:

```powershell
setx MODRINTH_TOKEN "<token>"
```

GitHub uses the `gh` CLI, which is already authenticated. Do not handle a GitHub token by hand.

**Do not store the token in the repository.** Not even in a gitignored file. A secret in the
working tree is one `git add -f` away from a commit, and the environment variable needs no
cleanup and cannot be swept into a backup or an IDE sync.

## The order of work

### 1. Version

Modrinth **will not accept a duplicate version string on the same loader**. If the version
already exists for that loader, bump it honestly: **patch** for bug fixes, **minor** for
features added or configuration options removed.

**Follow the project's existing Modrinth convention, not the GitHub tag convention.** These
differ, and mixing them is a real mistake:

- Modrinth version numbers are plain semantic versions — `1.8.0`, `1.7.2+26.2`
- The Minecraft version is a **separate field** in the form, so it must not be encoded into the
  number
- The loader is a **separate field** too, so it must not be encoded either
- GitHub tags in this project *do* encode both: `keybindsgalore-1.8.0+26.2-neoforge`

So a NeoForge build of the same mod is often **the same version number** as the Fabric build,
differing only by loader. Check the existing version list before assuming a bump is needed:

```powershell
curl.exe -s -H "User-Agent: HVB007/KeybindsGalore-release/1.0" `
  "https://api.modrinth.com/v2/project/l6y7RMn7/version"
```

Update `mod_version` in `gradle.properties` so the jar name matches.

### 2. Build and verify the jar

```powershell
attrib.exe -R "C:\Users\HVB\Desktop\Projects\MC Mod\KeybindsGalore_HVB007\build\*" /S /D
$env:JAVA_HOME="C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.0.1\jbr"
.\gradlew.bat build
.\gradlew.bat test
```

**Do not trust a green build.** Open the jar and confirm:

- the loader metadata file (`fabric.mod.json` or `META-INF/neoforge.mods.toml`) is present and
  its version matches `gradle.properties`
- the mixin config, `keybindsgalore.properties`, the icon, and `en_us.json` are all present
- the **LGPL licence file is embedded** (`jar { from("LICENSE") }` in `build.gradle`)
- **no stale assets or classes** from earlier Minecraft versions. This project once shipped a
  whole language file belonging to a different historical mod, and two different icons.
- no leftover files from the *other* loader. A NeoForge jar must not contain `fabric.mod.json`,
  and vice versa
- report the class count and the jar size

**If a NeoForge version string is hardcoded in `neoforge.mods.toml`, check it.** NeoForge reads
the version from that file, and a dev run loads the exploded classes folder, which has no
manifest to fall back on — so a stale or templated value shows up in-game as `0.0NONE`. The
`verifyModVersion` task wired into `check` catches a mismatch with `gradle.properties`; run it.

### 3. Archive before publishing, not after

Copy **both** jars into `bkpjar/<version>/`, then regenerate `bkpjar/SHA256SUMS.txt`, then
**verify every archived jar against its recorded hash**. Archiving first guarantees the backup is
byte-identical to what ships. `RELEASE_CHECKLIST.md` and `bkpjar/README.md` have the commands.
The jars are git-ignored; only `README.md` and `SHA256SUMS.txt` are tracked. Do not commit jars.

### 4. Documentation

Bring all of these in line with the current build. The recurring failure is documentation
describing a previous Minecraft version, so check every version number and every claim:

- `README.md` — user facing. Must not contain mojibake; check for the replacement character.
- `CHANGELOG_LATEST.md` — must describe **this** release, not the last one.
- `MODRINTH_DESCRIPTION.md` — the Modrinth page body.
- `ROADMAP.md` and `AUTONOMOUS_PLAN.md` — tick what shipped, add what is new.
- `RELEASE_CHECKLIST.md` — refresh the version-specific values.

**Claim audit.** The old README was wrong in four ways: it described a rendering library the
branch does not use, promised an RGB colour picker that does not exist, named a category that
does not exist in this version, and claimed "zero dependencies". Check every technical claim
against the current code. Where the mod has a known limitation, say so in a **Known
limitations** section rather than hiding it.

**Loader neutrality.** If a project serves more than one loader, the **description must cover
every loader it lists**. A description that says "A Fabric mod", requires Fabric Loader and
Fabric API, and tells the reader to use ModMenu actively misleads NeoForge users, because
NeoForge needs none of those. Check the live page, not just the repo file, since the two drift
apart. When publishing a new loader, treat a stale description as part of the job, not a
follow-up.

**Known cosmetic warnings.** If a dependency emits a deprecation or version warning, find out
whether it is *ours* or *upstream* before writing anything about it, and say which. Do not
patch a third-party jar to silence a warning, and do not ship one claiming a warning is absent
when it is only absent from your own code.

### 5. Modrinth upload, via the API

Project id for this project is **`l6y7RMn7`** (slug `keybindsgalore+(hvb007)`). Read the
version list and project object first so you are working from real data.

**The endpoint is `POST /v2/version` and it is `multipart/form-data`, not JSON.** Sending a JSON
body fails with `ContentTypeIncompatible`, which reads like a content-type problem but is
actually the wrong shape entirely.

Two parts are required:

- a part named `data` holding the version metadata as JSON
- at least one file part, whose name is listed in `file_parts`

The `data` part must come **first** in the multipart body, or the upload fails with
`` `data` field must come before file fields ``. `project_id` is required in that payload
despite being absent from the documented request body.

```powershell
$token  = $env:MODRINTH_TOKEN
if (-not $token) { $token = (Get-ItemProperty -Path 'HKCU:\Environment').MODRINTH_TOKEN }
$changelog = [string]::Join("`n", [string[]](Get-Content ".\mr-changelog.txt"))

$data = @{
  project_id     = "l6y7RMn7"          # REQUIRED, despite not being in the docs body
  name           = "NeoForge port for Minecraft 26.2"
  version_number = "1.8.0"
  changelog      = [string]$changelog
  dependencies   = @( @{ project_id = "9s6osm5g"; dependency_type = "required" } )
  game_versions  = @("26.2")
  version_type   = "release"
  loaders        = @("neoforge")
  featured       = $false
  status         = "listed"
  environment    = "client_only"        # a STRING, not an array
  file_parts     = @("file","sources")
  primary_file   = "file"                 # the mod jar, by multipart field name
  file_types     = @{ sources = "sources-jar" }
} | ConvertTo-Json -Depth 6 -Compress

[System.IO.File]::WriteAllText("$PWD\mr-data.json", $data, (New-Object System.Text.UTF8Encoding($false)))

# The data part MUST come before the file parts, or the API rejects the upload.
curl.exe -s -w "`n__HTTP__%{http_code}" -X POST `
  -H "Authorization: $token" -H "User-Agent: HVB007/KeybindsGalore-release/1.0" `
  -F "data=<$PWD\mr-data.json;type=application/json" `
  -F "file=@$jar;type=application/java-archive;filename=$filename" `
  -F "sources=@$sourcesJar;type=application/java-archive;filename=$sourcesFilename" `
  "https://api.modrinth.com/v2/version"
```

**PowerShell traps that will cost you an hour otherwise:**

- `Get-Content -Raw` returns a `PSObject`. `ConvertTo-Json` then wraps it as
  `{"value": "..."}`, and the API rejects it with a confusing parse error. Cast with
  `[string]`, or use `[string]::Join("`n", [string[]](Get-Content ...))`.
- `Get-Content -Raw` also reads as the **ANSI codepage**, not UTF-8, so em-dashes and emoji
  arrive double-encoded and land on the live page as `Aâ€"`. Read the body with
  `[System.IO.File]::ReadAllText` (UTF-8 by default), or make the text pure ASCII. The
  project description was corrupted twice this way before it was made pure ASCII.
- **Never fix text with a PowerShell `-replace` chain and trust it.** One such chain silently
  deleted every lowercase `i` and briefly turned "KeybindsGalore" into "KeybndsGalore" on the
  live page. Write the file, then verify by comparing the fetched result to the intended text
  as whole strings, not by eye.
- `environment` is a **single string**, not an array. Sending `["client_only"]` fails with
  `invalid type: sequence, expected a string`.
- Write the JSON with `UTF8Encoding($false)`. The default encoder emits a BOM.
- The API reports missing fields one at a time, as `400` with `missing field 'x'`. Expect to
  add `file_parts`, `dependencies`, and `featured` on successive attempts.

**Set the primary file at creation. There is no way to set it afterwards.** This is the single
most important rule in this section, and getting it wrong is not cleanly reversible.

- `primary_file` is a **string** at creation: the multipart *field name* of the primary file.
- On the **edit** endpoint the field is an array `[algorithm, hash]`, and as of the current
  Labrinth v3 code the edit request struct has **no primary field at all**. `PATCH
  /v2/version/{id}` returns `204` and silently does nothing, so it looks like it worked.
- There is no `PATCH /v2/version_file/{id}`. That route does not exist on v2 or v3, and a
  `primary` hint passed to the add-file endpoint is ignored.
- Files with no `primary` flag are not re-ordered. The API will happily return the sources jar
  first, and `DELETE`ing the other file does not promote anything.

**So: never create a version, then rename it and swap its files.** Decide the version number and
the exact file set *before* the first upload. If a version number turns out to be wrong, the
recovery is `DELETE /v2/version/{id}` and a fresh create, which needs the **`VERSION_DELETE`**
scope. Ask for that scope up front alongside create/read/write so renames stay possible.

**Version files are addressed by hash, not by id.** `DELETE /v2/version_file/{sha1}` works;
`DELETE /v2/version_file/{base62-id}` returns `404`. The base62 `id` on each file is for
nothing you need here.

**Tag the sources jar as a sources jar.** On creation, pass a `file_types` map from multipart
field name to type, and it can be corrected later via `PATCH /v2/version/{id}` with a
`file_types` array of `{algorithm, hash, file_type}` — the one file field that *is* still
editable:

```json
"file_types": { "sources": "sources-jar" }
```

Without the tag, Modrinth treats the sources jar as a generic supplement. With it, the version
page and the API expose `file_type: "sources-jar"` and the download button correctly resolves
to the mod jar. Verify this on the real version page after publishing, not just in the API
response, because a version whose files are all unflagged and sources-first is ambiguous.


**Upload both files in the first create, and get the version number right first.** Modrinth
accepts a sources jar as a supplementary file on the same version — the primary is whichever
part `primary_file` names. Both belong on Modrinth; the sources jar is not a GitHub-only
artefact.

- primary: `keybindsgalore-<version>.jar`
- supplementary: `keybindsgalore-<version>-sources.jar`, tagged `sources-jar`


**Dependencies — the trap I actually fell into.** Do not copy a dependency `project_id` from an
existing version of another loader. I reused `P7dR8mSH` from the Fabric release believing it was
NeoForge, and it is in fact **Fabric API**. That single copy-paste would have instructed every
NeoForge user to install Fabric API.

**Look every dependency id up by slug and confirm what it actually is** before using it:

```powershell
curl.exe -s -H "User-Agent: HVB007/KeybindsGalore-release/1.0" `
  "https://api.modrinth.com/v2/project/<slug>"
```

- `9s6osm5g` is Cloth Config — a real required dependency
- **NeoForge has no Modrinth project page.** It is a loader, expressed through the `loaders`
  tag on the version, so it must **not** appear as a dependency. Search confirms no such project
  exists. If you cannot find it, that is expected, not an error to work around.

### 6. GitHub release

Do this as a full step, not as an afterthought after Modrinth. **A GitHub release is part of
publishing**, because the sources jar is a release artefact in its own right and GitHub is where
people decompile against it.

Both jars attach to the release:

```powershell
gh release create "keybindsgalore-$version" `
  ".\bkpjar\$version\keybindsgalore-$version.jar" `
  ".\bkpjar\$version\keybindsgalore-$version-sources.jar" `
  --title "$version" --notes-file ".\release-notes.md" --target "<branch>"
```

If the release is being created in a turn where I have **not** yet seen a summary, prepare the
commands and the notes and show them to me first. When I have already said to go ahead, or when
this is a follow-up turn for a version whose release already exists, create it without asking
again. `prompts/12-prepare-a-github-release.md` covers the GitHub side in more depth, including
confirming the tag target before creating anything.

Check the tag is free first, and check what titles already exist, so you neither clobber an old
release nor create a confusing near-duplicate:

```powershell
gh release list --limit 10
git ls-remote --tags origin
```

**Tag and title conventions differ from Modrinth, on purpose.**

- the GitHub **tag** carries the Minecraft version and loader:
  `keybindsgalore-1.8.0+26.2-neoforge`
- the GitHub **title** is the plain version number: `1.8.0`
- the Modrinth **version_number** is also the plain number: `1.8.0`
- the **jar filename** carries the Minecraft version: `keybindsgalore-1.8.0+26.2-neoforge.jar`

So a NeoForge build and a Fabric build of the same mod share the number `1.8.0` and differ by
tag and filename. That is intentional, not an inconsistency to tidy up. If you find an existing
release whose title still embeds the Minecraft version, retitle it with
`gh release edit <tag> --title <plain-number>` rather than creating a second release.

### 7. Verify both uploads, do not assume either

**Modrinth.** Download each file back from the CDN URL in the API response and compare hashes
against the archived jar:

```powershell
curl.exe -s -o verify.jar "<the file url from the response>"
Get-FileHash verify.jar -Algorithm SHA1     # compare to the archive
Get-FileHash verify.jar -Algorithm SHA512   # compare to the archive
```

Byte-identical or it did not ship correctly. Then fetch the version by id and confirm its
`loaders`, `game_versions`, `status`, `environment`, `dependencies`, and file list — including
that the sources jar is present and `primary` is false. Report the version id and public URL.

**GitHub.** `gh release view <tag> --json assets` returns a `sha256` per asset. Compare each
against the local archive:

```powershell
gh release view "keybindsgalore-$version" --json assets
```

A mismatch means the wrong file was uploaded. Check `bkpjar/SHA256SUMS.txt` to see which build
the archive actually holds.

**Watch for archive drift.** A sources jar rebuilt from a newer commit differs by a few bytes
from one archived earlier, because prompts and docs are packaged into it. When a release already
exists on one platform, align the archive to what that platform serves rather than silently
shipping different bytes to different users, unless I say otherwise.

### 8. Close out

- confirm no stray token file was left behind, and that nothing token-shaped is staged or
  committed. The token lives in the environment variable and needs no cleanup
- note the token's expiry so we renew it before a release is blocked by it
- `git log --oneline` and a clean `git status`, so I can see exactly what changed
- a plain summary of what is now public, with links
- anything you deliberately did **not** do, and why

## Screenshots

Check `MODRINTH_DESCRIPTION.md` and the existing gallery. If the gallery images predate the
current UI, say so plainly and give me a numbered shot list with a one-line caption and alt
text for each. Always lead with the strongest single image: it becomes the project's preview
thumbnail in search results.
