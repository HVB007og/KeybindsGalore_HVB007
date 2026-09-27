# Prompt: diagnose a bug from a log

Paste this, then paste the log or say where it is.

---

You are diagnosing a bug in KeybindsGalore, a client-side Fabric mod for Minecraft. I am not a
programmer. I will give you a symptom and a log. Find the cause. Do not guess.

## Read first

`AGENTS.md`, specifically the **Runtime invariants** section. It records how input actually
reaches this mod, and getting that wrong will send you chasing a bug that does not exist.

The single most important fact: **presses reach the mod through the `KeyMapping.set` mixin, but
releases for an open screen are delivered to `Screen.keyReleased` / `mouseReleased` instead.**
So a conflicting key normally logs `Key Input: <key> | Pressed: true` with **no matching
`Pressed: false`**. That is correct. Do not "fix" it, and do not treat a missing release log as
a symptom.

## Method

1. **Establish the facts before forming a theory.** What exactly did I do, what did I see, and
   what does the log actually contain? Quote the relevant lines.
2. **Filter the noise.** Ignore Mojang and Realms `401` and `SignedJWT` errors; they are normal
   offline. Focus on `keybindsgalore`, mixin errors, crashes, and stack traces.
3. **Find the code that produced the log line.** Use the logger call text to locate the exact
   line, then read the surrounding method. Name the file and line in your answer.
4. **Decide whether it is a bug at all.** This mod has a lot of settings that are persisted but
   never read. If I say a setting "does nothing", check whether anything reads it before
   assuming a rendering or logic fault. Several past reports turned out to be a setting that was
   never implemented, or one that belongs to the other menu.
5. **Check whether it is a version thing.** If the symptom is new, compare against the previous
   Minecraft branch. 26.3 replaced GLFW with SDL3 and moved the render pipeline, so
   input-related and rendering-related differences between branches are expected.

## Mistakes I have already made, so you do not repeat them

- **I reported a runaway loop that was me mashing the key.** Before calling anything a loop,
  ask me to reproduce it on a single deliberate 5 second hold. If a hold produces one press
  event and one selector session, there is no loop.
- **I shipped a speculative guard for a bug that did not exist.** A key-state latch written to
  "fix" a non-existent repeat problem would have swallowed the *next genuine press* and stopped
  the selector opening at all. Never add a guard for something not reproduced.
- **A batch edit across several config files removed an unrelated field** and broke the
  snapshot constructor, and a batch edit of the config screen deleted six unrelated widgets.
  After any bulk edit, build and run the config parity check.
- **A language file edit left a trailing comma** and the build still passed, because resources
  are copied without validation. Parse `en_us.json` explicitly after editing it.

## What to report back

- **Verdict**: real bug, not a bug, or cannot tell yet.
- **Evidence**: the exact log lines, quoted.
- **Cause**: file and line, with the reasoning that connects the log to the code.
- **Fix**: the smallest change that addresses it, and what could break as a result.
- **Confidence**: say plainly which parts you are sure of and which you inferred.

If the evidence does not support a conclusion, say that instead of manufacturing one. "The log
does not contain enough to tell" is a useful answer and far better than a confident wrong one.
