# Prompt: audit and tidy the codebase

Paste this, then optionally name an area, for example *"audit the config layer"*.

---

You are reviewing KeybindsGalore, a client-side Fabric mod for Minecraft, and improving its
structure, consistency, and documentation. I am not a programmer. I care about the code being
correct and understandable, and about not breaking a working mod in the name of tidying.

**Default to small, safe improvements.** Do not restructure a working project for tidiness.

## Read first

`AGENTS.md` and `ROADMAP.md`. The **Ecosystem conventions** section in `AGENTS.md` contains
measured data from eleven published Fabric mods, and it records decisions already made. Do not
reopen a settled question without new evidence.

## The bar you are held to

This mod's real quality problem is **not** file count or line length. It is **settings that lie
to the player**: options that appear in the config screen, are saved, and are then never read.
Nine were retired during one cleanup pass. A player who changes "pie menu scale" and sees
nothing conclude the mod is broken, which is worse than the option not existing.

So: when auditing, always sweep for persisted-but-unread options and report them. That is
usually the highest-value finding available.

## The ecosystem rules to hold to

- A mid-size client Fabric mod is 45 to 150 files, median class around 50 lines. This mod sits
  at 50 files, which is normal. **Do not merge or split files to hit a number.**
- Javadoc belongs on the `api/` package only, where it is a real contract. Implementation
  classes get no Javadoc; use `//` at column 0 only for non-obvious *why*. Continuity has one
  Javadoc block across 102 files.
- No Checkstyle, Spotless, or google-java-format. `.editorconfig` is the formatting source of
  truth. Adding a linter is not an improvement here.
- Delete dead code rather than deprecating it. Config options are the exception, because their
  values must keep round-tripping.
- Keep branches within three levels of indentation. Extract a method rather than nesting deeper.
- No per-file licence headers. Six of eleven popular mods have none.

## Checks to run

1. **Dead code** — classes, methods, and fields with no references. Exclude public API and
   public static state, which are compatibility surfaces.
2. **Unread config options** — every option in the snapshot codec, checked against actual reads
   in the screens and core. Report which are unwired and whether each should be implemented or
   retired.
3. **Config contract integrity** — the GUI, `keybindsgalore.properties`, the snapshot codec,
   and `en_us.json` must agree on the same set of options. Report any mismatch in either
   direction. Watch for two widgets bound to one field, which silently revert each other.
4. **Dead documentation** — comments and Javadoc that describe behaviour the code does not
   have. This project shipped a README claiming a rendering library the branch did not use, a
   colour picker that did not exist, and a category that did not exist in that version.
5. **Nesting depth and long methods** — extract rather than condense.
6. **Naming and package fit** — singular package names, `Mixin*` prefix, `*Accessor` for
   accessors, mixins package-private and accessors public.
7. **Test coverage gaps** — which pure logic is untested, and which behaviour can only be
   verified by hand.

## Rules

- **Never delete a config key outright.** `ConfigManager` logs an error for any key the
  snapshot does not recognise, so a plain deletion punishes every existing user. Retirement is
  a three-step operation, documented in `AGENTS.md`.
- **Never change a default without telling me**, and say what the previous value was and what
  the user will see differently.
- **Never hide a GUI option to make a problem disappear** unless the tooltip says plainly that
  it has no effect.
- After any change, build and run the tests. If you change config, run the parity check.
- Separate mechanical fixes from judgement calls in your report, so I can approve the second
  kind.

## What to report

Findings grouped as: **safe mechanical fixes you made**, **things needing my decision**, and
**things you deliberately left alone with the reason**. For the second group, give me the
options and a recommendation. Do not make a product decision on my behalf.
