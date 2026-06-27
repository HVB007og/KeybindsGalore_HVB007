## KeybindsGalore Plus - v1.7.0 (The UI & Priority Update)

This is a massive update that completely overhauls how you manage keybinds and settings, making the mod more intuitive and powerful than ever before!

### ✨ What's New:
*   **In-Game Config GUI:** Fully integrated with ModMenu and Cloth Config API! You can now customize pie menu colors, transparency, scale, and mod behaviors entirely in-game—no more editing `.properties` files manually. 
*   **The "Priority System" Overhaul:** The old, confusing "Ignored Keys" list has been entirely replaced by a new, smart Priority System. You can now enforce strict, exclusive priority for specific actions on specific keys (e.g., forcing `Space` to ALWAYS jump, even if something else is bound to it).
*   **New "Capture" Hotkey (Default: K):** We've added an incredibly easy way to manage conflicts on the fly. Press `K` while playing, press the key that has a conflict, and select the action you want to prioritize from a simple list. Done!
*   **Smart State Handling:** Completely rewrote the vanilla input interception mixins. The mod now flawlessly handles both continuous "hold" actions (like walking) and single-click "toggle" actions (like cinematic camera or hotbar slots) when they are prioritized.

### 🛠️ Changes & Fixes:
*   **Removed:** The old `IGNORED_KEYS` configuration has been removed to streamline the priority system.
*   **Migrated:** Old config files will automatically migrate their old priorities to the new `Action:Key` exclusivity format upon loading the game.
*   **Fixed:** The "double action" bug where non-prioritized actions could still fire under certain conditions has been completely eradicated by implementing strict, string-based vanilla input isolation.
*   **Added:** Extensive debug logging options (toggleable in the new GUI) for users experiencing complex conflicts.

*Note: While the mod still runs entirely standalone, installing the `Cloth Config API` mod is highly recommended to access the new GUI.*

---

## Orange Cube Communication — Patch Addendum

### Issue #14 — "Completely broken" (Debug key conflicts)
- **Root Cause:** `KeybindManager.safeGetCategory()` used `getCategory().id().toString()` which returns `"minecraft:debug"`, but the default config filter was `"Debug"`. The `equalsIgnoreCase` never matched, so debug-category keybinds were never filtered out of the conflict table.
- **Fix:** Changed to `getPath()` — now returns `"debug"`, which correctly matches config values via `equalsIgnoreCase`.
- **Also:** The `IGNORED_KEYS` config field existed but was never actually checked in `findAllConflicts()`. Added the missing check (blacklist/whitelist via `INVERT_IGNORED_KEYS_LIST`).

### Issue #13 — Incompatibility with Amecs
- **Root Cause:** Amecs intentionally allows multiple keybindings per key, but KeybindsGalore's conflict detection treated every shared-key as a conflict and blocked non-priority keybindings via the `setDown` mixin.
- **Fix:** Added `isAmecsLoaded()` detection via FabricLoader (checks for `amecs`, `amecsapi`, `amecs-fork`). When detected, all conflict resolution logic is skipped — `findAllConflicts()`, `handleKeyPress()`, `handleOnKeyPressed()`, and the `setDown` mixin all yield control to Amecs.

### Files Modified
- `src/main/java/net/hvb007/keybindsgalore/KeybindManager.java` — `safeGetCategory()` path fix, IGNORED_KEYS check, Amecs detection and bail-outs
- `src/main/java/net/hvb007/keybindsgalore/mixin/KeyMappingMixin.java` — Amecs bail-out in `setPressed()`
- `src/main/java/net/hvb007/keybindsgalore/Configurations.java` — `FILTERED_CATEGORY_KEYS` defaults include `"Debug"` and `"debug"`
- `src/main/java/net/hvb007/keybindsgalore/KeybindsGalore.java` — skip `findAllConflicts()` on JOIN when Amecs loaded

### Push
All 19 local branches pushed to `origin` (GitHub). `master` skipped (local behind remote).
