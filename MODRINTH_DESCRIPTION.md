KeybindsGalore
================
A Fabric mod that opens a popup menu when multiple actions are bound to the same key, allowing you to choose which action to perform.

✨ Features
----------
- **Conflict Resolution** — Opens a menu to resolve conflicting keybinds instead of executing all of them at once.
- **Circular Pie Menu** — A modern, intuitive pie menu for selecting actions.
- **Smooth GPU Rendering** — The pie menu is rendered via owo-lib's deferred GUI system, providing polished visuals with proper depth ordering and no z-fighting.
- **Mouse Button Support** — Works with conflicting mouse button bindings.
- **Extensive Configuration** — Almost every visual aspect, from colors to radii, can be configured in the properties file.

🚀 What's New in Version 1.7.2+1.21.9
--------------------------------------
This version upgrades the mod to Minecraft 1.21.9.

- **Minecraft 1.21.9 support** - Fabric API 0.134.1+1.21.9, Fabric Loader 0.19.3.
- **owo-lib** `0.12.24+1.21.9` — Pie menu uses `OwoUIDrawContext.drawRing()` / `drawCircle()` for reliable deferred-GUI rendering.
- **Cloth Config 19.0.147** - Full configuration GUI with all options and hover tooltips.
- **ModMenu 17.0.0** integration.

📦 Requirements
---------------
- Minecraft **1.21.9**
- Fabric Loader **>=0.19.3**
- Fabric API **>=0.134.1**

The mod bundles owo-lib internally — no separate download needed.

⚙️ Configuration
-----------------
The mod can be configured by editing the `keybindsgalore.properties` file in your config folder, or via the in-game ModMenu configuration screen. You can customize all colors, radii, and rendering behaviour.

📖 History & Credits
---------------------
- **Original Author**: Cael — [Original Project](https://github.com/CaelTheColher/KeybindsGalore)
- **1.20.x Update**: HVB007 — [GitHub](https://github.com/HVB007)
- **KeybindsGalore Plus**: AV306 — [Project](https://github.com/AV306/KeybindsGalore-Plus)
- **1.21.x Re-Rewrite**: HVB007 — added native hardware rendering without external UI libraries.
- **1.21.7+ owo-lib Migration**: HVB007 — adapted to Minecraft's deferred GUI system using owo-lib.

🤖 AI Declaration
------------------
Portions of this mod's code were written with the assistance of AI tools. All AI-generated code has been reviewed, tested, and verified for functionality by the developer.
