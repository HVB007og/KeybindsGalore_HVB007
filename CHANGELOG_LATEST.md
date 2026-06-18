## 1.7.2+1.21.6 — owo-lib Migration

- **Added owo-lib dependency** (`0.12.21+1.21.6`) — replaces all custom low-level rendering code
- **Pie menu rendering fixed**: sectors now use `OwoUIDrawContext.drawRing()` / `drawCircle()` from owo-lib, compatible with Minecraft 1.21.6's deferred `GuiElementRenderState` system
- **Removed** custom `SectorElementRenderState`, `TriangleStripRenderer`, `DrawContextAccessor` mixin, and `GUI_TRIANGLE_STRIP` pipeline
