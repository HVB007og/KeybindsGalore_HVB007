# KeybindsGalore

A Fabric mod that opens a popup menu when multiple actions are bound to the same key, allowing you to choose which action to perform.

---

## ✨ Features

*   **Conflict Resolution:** Opens a menu to resolve conflicting keybinds instead of executing all of them.
*   **Circular Pie Menu:** A modern, intuitive pie menu for selecting actions.
*   **Hardware-Accelerated Rendering:** The pie menu is rendered smoothly using Minecraft's native `Tesselator`, providing a polished and high-performance UI without massive library dependencies.
*   **Mouse Button Support:** Works with conflicting mouse button bindings.
*   **Extensive Configuration:** Almost every visual aspect, from colors to rendering modes, can be configured in the properties file.

---

## 🚀 What's New in Version 1.6.2

This version introduces improved configuration flexibility and UX refinements.

*   **Flexible Conflict Filtering**: Replaced `FILTER_DEBUG_KEYS` with `FILTERED_CATEGORY_KEYS`. You can now exclude any category (e.g., `[Debug]`, `[Multiplayer]`) from conflict detection.
*   **Enhanced UX**: Chat warnings for conflicting keybinds now include helpful instructions on how to disable them in the config file.
*   **Bug Fixes**:
  - Fixed hex value parsing in the properties file for more reliable color configuration.
  - Improved `ConfigManager` to support generic string lists.
*   **Internal Improvements**: Removed legacy reflection-based initialization for better stability and cleaner code.

---

## 🚀 What's New in Version 1.6.1

This version introduced a major rewrite of the rendering system.

*   **Zero Dependencies**: The mod has been completely overhauled to remove the dependency on `owo-lib`. The mod is now fully self-contained!
*   **Native Hardware Rendering**: Achieved smooth, hardware-accelerated pie menu rendering directly via Minecraft's native `Tesselator`. This fixes all previous aliasing and rendering glitches.
*   **Mappings Updated**: Switched from Yarn to **Minecraft Official Mappings**. This aligns with Mojang's move towards deobfuscation in newer versions, ensuring better long-term maintenance.
* **New Configuration Options**:
  - `USE_SOFTWARE_RENDERING`: A fallback option that draws the pie menu using basic shapes if you experience issues with the new hardware renderer.
  - All pie menu colors (sectors, cancel zone, hover effects) are now fully configurable in the properties file (e.g., `PIE_MENU_SECTOR_COLOR_EVEN`).
*   **Bug Fixes**:
  - Fixed a glitch where opening the circular pie menu would incorrectly apply Minecraft's default background blur to the entire screen.
  - Fixed an internal bug where the click count was being set to the raw keycode value rather than 1 when an action was selected.

---

## 📦 Requirements

![Fabric API](https://img.shields.io/badge/Fabric%20API-Required-blue)

*   Minecraft 1.21.1
*   Fabric Loader
*   [Fabric API](https://modrinth.com/mod/fabric-api)

---

## ⚙️ Configuration

The mod can be configured by editing the `keybindsgalore.properties` file located in your `config` folder. You can customize all colors, radii, and rendering modes.

**Example Options:**
*   `USE_SOFTWARE_RENDERING=false` (Set to `true` to use the primitive software renderer)
*   `PIE_MENU_SECTOR_COLOR_EVEN=0xC0606060`
*   `PIE_MENU_CANCEL_ZONE_HOVER_COLOR=0xC0B04232`

---

## 📖 History & Credits

This mod has a rich history of community contributions:

*   **Original Author:** The mod was originally created by **Cael**.
  *   [Cael's Original Project](https://github.com/CaelTheColher/KeybindsGalore)
*   **1.20.x Update:** It was first updated to 1.20.x by me, **HVB007**.
  *   [HVB007's GitHub](https://github.com/HVB007og/KeybindsGalore_HVB007_1.20.x)
*   **KeybindsGalore Plus:** The project was significantly enhanced and maintained as "KeybindsGalore Plus" by **AV306**, who added many features and bug fixes.
  *   [AV306's "KeybindsGalore Plus" Project](https://github.com/AV306/KeybindsGalore-Plus)
*   **1.21.x Re-Rewrite:** Ported to 1.21.1 and rewritten to use native hardware rendering without external UI libraries by **HVB007**.

## 🤖 AI Declaration

Portions of this mod's code (specifically regarding the hardware-accelerated rendering rewrite and bug fixing) were written with the assistance of AI tools. All AI-generated code has been reviewed, tested, and verified for functionality by the developer.