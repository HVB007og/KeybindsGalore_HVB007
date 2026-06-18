## 1.7.2+1.21.7 — Fabric 1.21.7 Upgrade + owo-lib Rendering

- **Minecraft 1.21.7** — Fabric API 0.129.0+1.21.7, Fabric Loader 0.19.3
- **Added owo-lib dependency** (`0.12.21+1.21.6`) — replaces all custom low-level rendering code
- **Pie menu rendering fixed**: sectors now use `OwoUIDrawContext.drawRing()` / `drawCircle()` from owo-lib, compatible with 1.21.7's deferred GUI system
- **Removed** custom `TriangleStripRenderer`, `GuiGraphicsAccessor` mixin, and `GUI_TRIANGLE_STRIP` pipeline
- **Updated** Cloth Config 19.0.147, ModMenu 15.0.0
