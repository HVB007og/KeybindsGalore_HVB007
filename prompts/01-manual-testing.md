# Prompt: run a manual test pass on the mod

Paste this to an assistant, then optionally name the Minecraft version or the build to test.

---

You are helping me test KeybindsGalore, a client-side Fabric mod for Minecraft that resolves
keybind conflicts. I am not a programmer. You are not driving the game — **I** am. Your job
is to give me a clear, ordered test script, then read the log I send back and tell me what it
means.

## Before anything else

Read `AGENTS.md` for the runtime invariants. In particular:

- Presses reach the mod through the `KeyMapping.set` mixin, but **releases for an open screen
  go to `Screen.keyReleased` / `mouseReleased`**, not through the mixin. So a conflicting key
  normally logs `Pressed: true` with **no** matching `Pressed: false`. That is correct
  behaviour, not a bug. Do not "fix" it.
- `KeybindsGalore.verboseLog` traces selector hover and finalise/cancel outcomes. `DEBUG`
  logs every key event and is extremely noisy. Keep `DEBUG=true` and `VERBOSE_DEBUG=true`
  while testing, but read the log with filters.

## Step 1 — give me the script

Write a numbered test script. For each step give: what to do, and what to look for. Cover at
minimum:

1. Startup and initial conflict scan.
2. Keyboard conflict — open the pie, hover, release, confirm the choice is committed.
3. Mouse conflict.
4. Held action (walk forward) versus one-shot action (hotbar slot).
5. **Three press patterns**, each must produce exactly one selector session:
   - a single press
   - several rapid deliberate presses
   - one long hold of 5+ seconds
6. List menu, via `USE_CIRCULAR_MENU=false` in the config screen.
7. The K capture flow: K, the target key, choose an action, confirm it prioritises, then
   remove the priority.
8. Config save through ModMenu, and confirm values survive closing and reopening the screen.
9. Escape closing the selector without choosing.
10. The priority-warning message in chat.

Set up a conflict first if needed: two actions on one key in vanilla Controls. The easiest
pair is two hotbar slots on the same digit.

## Step 2 — I send you the log

I will paste `run/logs/latest.log`, or tell you to read it from disk.

When analysing it:

- Filter for `keybindsgalore`. Ignore Mojang/Realms `401` and `SignedJWT` errors, they are
  normal when running offline.
- **Fail loudly on any mod error, crash, or mixin failure.** Those are real.
- A missing log line is only a bug if you can say which code should have produced it. Do not
  speculate.
- If a step passes, say so briefly. Do not manufacture concerns.

## Step 3 — report

For each step: **pass**, **fail**, or **not tested**. For failures, quote the exact log lines
and name the file and line you believe is responsible. If you are not confident, say which
part you are unsure about rather than guessing.

## Traps I have already hit, so you do not repeat them

- I once mashed a key dozens of times and reported it as a runaway loop bug. It was me
  mashing. **Before calling something a loop, ask whether it reproduces on a single hold.**
- Do not suggest a guard, latch, or key-state poll to "fix" something you have not reproduced
  on a deliberate 5 second hold. A latch written for a non-existent bug silently swallows the
  next real keypress.
- A setting appearing in the config screen does not mean it is wired to anything. If I say a
  setting "does nothing", check whether the code reads it at all before assuming a rendering
  bug. Roughly half of what I have reported in the past turned out to be a setting that was
  never implemented, or a setting that belongs to the *other* menu.
- The pie menu is **mouse only**. There is no keyboard navigation. Do not report that as a
  regression; it is a known gap.
- The pie menu intentionally does **not** pause the game.

## Rules

- Never claim a test passed if I did not report it.
- If a step cannot be tested as written, say why and give me an alternative.
- Prefer asking me one clear question over guessing.
