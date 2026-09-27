# KeybindsGalore — LLM prompt library

Paste-ready prompts so a fresh chat or a different LLM does not need the whole story
repeated. Each file is written **to the assistant**, not to you.

## How to use

1. Open a new chat.
2. Paste one prompt file in full.
3. Say something like: *"Follow `prompts/02-minecraft-version-upgrade.md` to port to 26.4."*
4. Answer any questions it asks, then let it work.

Prompts are additive. If you need two at once, say so explicitly, e.g. *"Follow
`05-bug-investigation-from-log.md`, then `03-release-and-modrinth.md` when it's fixed."*

## Before anything else

Every prompt assumes the assistant has read these three files. If the assistant has not,
the prompt tells it to read them first:

| File | What it holds |
|---|---|
| `AGENTS.md` | Durable technical facts, runtime invariants, architecture, hard-won traps |
| `ROADMAP.md` | What is planned and not yet done |
| `AUTONOMOUS_PLAN.md` | The 26.3 and NeoForge work, with honest status |

Those are the source of truth. The prompts here are *how to do a job*, not *what is true*.

## The prompts

| File | Use it for |
|---|---|
| `01-manual-testing.md` | Driving a full manual test pass in game, then reporting results |
| `02-minecraft-version-upgrade.md` | Porting to a new Minecraft version on the current loader |
| `03-release-and-modrinth.md` | Getting a release ready: docs, metadata, Modrinth, tags, archive |
| `04-neoforge-port.md` | Porting the mod to NeoForge |
| `05-bug-investigation-from-log.md` | Diagnosing a bug from a log file, without guessing |
| `06-codebase-audit.md` | Reviewing and tidying the codebase against ecosystem conventions |
| `07-new-feature.md` | Adding a feature without breaking the input path or the config contract |
| `08-unattended-overnight-run.md` | Letting an assistant work for hours with you asleep |
| `12-prepare-a-github-release.md` | Tagging a release and attaching both jars

## Recommended additions, not yet written

These are worth creating when the need is real rather than speculative. Each would be a
small file in this folder.

| Candidate | Why it earns its place |
|---|---|
| `09-revert-a-bad-change.md` | Undoing a change that turned out wrong, without losing unrelated work. There have been several this project, including a bulk config edit that silently removed `useKeybindFix` and a bulk GUI edit that deleted six unrelated widgets. |
| `10-translate-the-mod.md` | Adding or correcting a language file. Must include the rule that `en_us.json` is not validated by the build, and that a trailing comma on the last entry is invalid. |
| `11-dependency-upgrade.md` | Bumping Fabric API, Cloth Config, or ModMenu without assuming the Modrinth version string is the Maven coordinate. This project lost a build to exactly that. |
| ~~`12-prepare-a-github-release.md`~~ | **Written.** Tagging a release and attaching both jars. Separate from `03` because Modrinth takes one file per version, so the sources jar belongs on GitHub. |
| `13-audit-config-options.md` | Finding options that are persisted but never read. A repeatable version of the sweep that retired nine options. |
| `14-multi-loader-maintenance.md` | Keeping Fabric and NeoForge branches in step once both exist. This will matter the moment there are two branches to drift apart. |

## One rule that outranks all of them

Do not claim verification that was not performed. Say what was checked, what was not, and
what a human still needs to do. Overclaiming here caused a false bug report earlier in
this project, and the correction cost more time than the original mistake.
