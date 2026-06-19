## 1.7.2+26.2 — Fabric 26.2 Upgrade

- **Minecraft 26.2** — Fabric Loader 0.19.3, Loom 1.17, Gradle 9.5.1
- **Fabric API 0.152.1+26.2** — Vulkan backend support, renderer API ported
- **ModMenu 20.0.0-beta.2**
- **Removed owo-lib dependency** — replaced with custom `RingRenderer` using `GuiElementRenderState` and `RenderPipelines.GUI`
- **Fixed RenderPipelines.GUI topology** — emits `QUADS` matching GUI_SNIPPET (fixes swiss-cheese rendering)
- **Fixed coordinate signs** — uses `cx + cos * r` to match atan2 mouse angle convention (fixes 180° flipped sector highlight)
- **Known issue:** Config screen unavailable from ModMenu — Cloth Config references `net/minecraft/util/Tuple` which was removed in 26.2. Edit `keybindsgalore.properties` manually in the config folder.
