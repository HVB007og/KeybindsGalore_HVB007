## KeybindsGalore Plus - v1.7.1 (The Rendering Fix)

This patch fixes a critical rendering issue where the circular conflict resolution menu would not display its pie sectors, making it unusable.

### 🛠️ Fixes:
- **Fixed pie menu not rendering** — The circular menu's sectors now properly draw using the native `TRIANGLE_FAN` rendering approach from v0.2, correctly ported to Minecraft 1.20.1's updated `BufferBuilder` API.
- **Fixed first sector highlight bleeding** — Hovering the first sector no longer tints the entire pie. The center vertex now always uses the default sector color, keeping highlight gradients properly contained within the selected sector.
- **Cleaned up console logging** — Removed per-frame debug spam (`[KBG DEBUG]`). The `DEBUG` and `VERBOSE_DEBUG` config options now default to `false`.
