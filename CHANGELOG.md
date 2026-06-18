# Changelog

## 1.7.2+1.21.11 (Fabric 1.21.11)
- Upgraded to Minecraft 1.21.11
- Fabric API 0.141.4+1.21.11, Fabric Loader 0.19.3
- owo-lib 0.13.0+1.21.11, Cloth Config 19.0.147, ModMenu 17.0.0
- owo-lib-based pie menu rendering (unchanged from 1.21.7)
- All prior features: pie menu, K-key capture flow, priority system, conflict detection

## 1.7.2+1.21.10 (Fabric 1.21.10)
- Upgraded to Minecraft 1.21.10
- Fabric API 0.138.4+1.21.10, Fabric Loader 0.19.3
- owo-lib 0.12.24+1.21.9, Cloth Config 19.0.147, ModMenu 16.0.1
- owo-lib-based pie menu rendering (unchanged from 1.21.7)
- All prior features: pie menu, K-key capture flow, priority system, conflict detection

## 1.7.2+1.21.9 (Fabric 1.21.9)
- Upgraded to Minecraft 1.21.9
- Fabric API 0.134.1+1.21.9, Fabric Loader 0.19.3
- owo-lib 0.12.24+1.21.9, Cloth Config 19.0.147, ModMenu 16.0.1
- owo-lib-based pie menu rendering (unchanged from 1.21.7)
- All prior features: pie menu, K-key capture flow, priority system, conflict detection

## 1.7.2+1.21.8 (Fabric 1.21.8)

## 1.7.2+1.21.7 (Fabric 1.21.7)

## 1.7.2 (Fabric 1.21.5)
- Upgraded to Minecraft 1.21.5 (Spring to Life)
- Fabric API 0.128.2+1.21.5, Fabric Loader 0.19.3
- Cloth Config 18.0.145, ModMenu 14.0.0
- **Fixed pie menu not rendering**: two root causes —
    1. HW-accelerated path never flushed geometry (`endBatch` was never called)
    2. Sector rendering happened before `super.render()`, so the background gradient overwrote it
- **Rendering now uses GPU triangle-strip batching via custom `RenderPipeline` + `RenderType` layer** — sectors drawn as interleaved triangle strips using `BufferSource.getBuffer()` on GuiGraphics' shared buffer
- **Custom pipeline registered**: `GUI_TRIANGLE_STRIP` derived from `RenderPipelines.GUI_SNIPPET` with `POSITION_COLOR` format and `TRIANGLE_STRIP` mode
- **Render layer created via Mojang mappings**: `RenderType.create(String, int, RenderPipeline, CompositeState)` with `CompositeState.builder().createCompositeState(false)`
- **Mixin accessor added**: `GuiGraphicsAccessor` exposes `bufferSource` field on `GuiGraphics` to share its render buffer
- **Removed dead code**: old HW path (`Tesselator`/`BufferUploader`, both removed in 1.21.5), scanline `fillTriangle`/`fillQuad`, `GuiElementRenderState` attempt (system doesn't exist in 1.21.5 — only 1.21.6+)
- Removed `USE_SOFTWARE_RENDERING` config flag; drawing is always batched now
- Default config has `USE_SOFTWARE_RENDERING=true`
- All prior features: pie menu, K-key capture flow, priority system

## 1.7.2 (Fabric 1.21.1)
- Full Cloth Config GUI with **all** config options displayed and categorized
- Hover tooltips on every config entry explaining what it does
- Fixed PIE_MENU_ALPHA value inflation on save-read cycle (short hex parsing bug)
- Config screen categories: General Settings, Behaviour Settings, Visual Settings
- Range constraints on numeric fields (min/max validation)

## 1.7.0
- Initial Fabric 1.21.1 port with all core features
