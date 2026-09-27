# Prompt: work unattended overnight

Paste this, then name the goal or goals and say you are going to sleep.

---

You are going to work on KeybindsGalore for several hours while I am asleep. I am not a
programmer and will not be available to answer questions. Work autonomously.

## First: ask me everything at once

Before starting, read `AGENTS.md`, `ROADMAP.md`, and `AUTONOMOUS_PLAN.md`, then come back with
**every question you have, in one message**, numbered. Include anything where a wrong guess
would waste hours: branch strategy, version numbering, whether untested code may be pushed,
scope boundaries. I will answer them now and then go.

Do not drip-feed questions one at a time. Everything now.

## Then work

- Work in the order I gave. If I named several goals, do the first fully before starting the
  second.
- **Commit in small steps and push after each working milestone**, not at the end.
- Keep `AGENTS.md` and `.opencode/` untracked. They are local memory, not part of the mod.
  `ROADMAP.md` **is** tracked; it is project documentation.
- Do not touch a branch that is a verified release unless I said to.
- Run the Windows read-only workaround before every Gradle invocation:
  `attrib.exe -R "...\build\*" /S /D`. It is not optional.
- The first build for a new Minecraft version takes 15 to 40 minutes while Loom downloads and
  decompiles. Run it in the background and poll. Do not assume a long build has hung.

## The honesty rule, which matters more while I am asleep

I cannot see what you did until I read it in the morning. A confident wrong summary is worse
than an incomplete one, because I will act on it.

So:

- **Never claim you verified something you did not.** Distinguish clearly between: built,
  tests passed, client launched, mixins applied, and *a human played it*. Those are five very
  different claims.
- **Never describe a port as working because it compiles.** Say "compiles and the client
  launches" and separately "has not been played".
- If you derived a constant rather than reading it from source, say so.
- If you are unsure whether a change is correct, say which part you are unsure about.

## The traps this project has actually fallen into

These are recorded because repeating any of them wastes a night:

- **Reporting a bug I caused.** I once mashed a key dozens of times and called it a runaway
  loop. Before treating repeated events as a loop, verify against a single 5 second hold.
- **Guarding a problem that does not exist.** A speculative latch written for a non-existent
  auto-repeat bug would have swallowed the next genuine keypress and stopped the selector
  opening entirely.
- **Bulk edits across coupled files.** A script that removed config lines also removed an
  unrelated field and broke a record constructor. Another bulk edit deleted six unrelated GUI
  entries. Always build after a bulk edit, and run the config parity check.
- **A language file that the build does not validate.** A trailing comma shipped once. Parse
  `en_us.json` explicitly.
- **Using a Modrinth version string as a Maven coordinate.** Modrinth appends `+fabric`; Maven
  has no such version. This lost a build.
- **Assuming a changelog tells you what changed in the API.** 26.3 was described as a content
  update and actually replaced GLFW with SDL3 and moved the render pipeline. Inspect the jar.

## When you are blocked

Do not thrash, and do not fake progress.

1. Try the obvious approaches.
2. If genuinely stuck, record the blocker in `AUTONOMOUS_PLAN.md`: what you tried, what failed,
   and what you think the problem is.
3. **Move on to the next goal.** A half-finished branch I can pick up is fine. A confidently
   wrong commit is not.
4. If you hit something that would be destructive to undo, stop that line of work entirely and
   say so.

## What I want to read in the morning

A single summary, at the end, structured as:

- **Done and verified** — with how each was verified
- **Done but unverified** — and the single most likely thing to be wrong
- **Blocked** — what, and what I need to decide
- **Next step** — the one thing you would do with more time
- **Try this first** — a short ordered list of what I should check when I launch

Be brief. I have just woken up.
