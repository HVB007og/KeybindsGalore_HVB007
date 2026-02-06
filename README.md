# KeybindsGalore

A Fabric mod that opens a popup menu when multiple actions are bound to the same key, allowing you to choose which action to perform.

---

## Features

*   **Conflict Resolution:** Opens a menu to resolve conflicting keybinds instead of executing all of them.
*   **Circular Pie Menu:** A modern, intuitive pie menu for selecting actions.
*   **Mouse Button Support:** Works with conflicting mouse button bindings.
*   **Dual Rendering Modes:** Choose between a high-performance hardware-accelerated renderer (via owo-lib) or a compatible software renderer.
*   **Extensive Configuration:** Almost every visual aspect, from colors to rendering modes, can be configured in the properties file.

---

## What's New in Version 1.6.X

This version introduces a major rewrite of the rendering system and adds new configuration options.

*   **Owo Lib Dependency**: The mod now uses **owo-lib** for its UI and rendering. This is a **required dependency**. `owo-sentinel` is included to help users download it automatically if it's missing.
*   **Dual Rendering Modes**: You can now choose between two rendering modes for the pie menu via the `USE_SOFTWARE_RENDERING` option in the properties file.
  - **Hardware Rendering (Owo Lib)**: The new default. Provides a perfectly smooth, circular pie menu with better performance.
  - **Software Rendering (Primitive)**: A fallback option that draws the pie menu using basic shapes. It retains the polygonal look and has been optimized to fix visual glitches.
*   **Mappings Updated**: Switched from Yarn to **Minecraft Official Mappings**. This aligns with Mojang's move towards deobfuscation in newer versions, ensuring better long-term maintenance.
* **New Configuration Options**:
  - `USE_SOFTWARE_RENDERING`: Switch between the two rendering modes.
  - All pie menu colors (sectors, cancel zone, hover effects) are now fully configurable in the properties file.
*   **Bug Fixes**:
  - The "diamond" shaped center hole in software rendering mode now correctly forms a polygon that matches the number of sectors.
  - Visual artifacts and seams with semi-transparent sectors in software rendering have been fixed.

---

## Requirements

![Fabric API](https://img.shields.io/badge/Fabric%20API-Required-blue)
![Owo Lib](https://img.shields.io/badge/Owo%20Lib-Required-red)

*   Minecraft 1.21.x
*   Fabric Loader
*   [Fabric API](https://modrinth.com/mod/fabric-api)
*   [owo-lib](https://modrinth.com/mod/owo-lib)

---

## Configuration

The mod can be configured by editing the `keybindsgalore.properties` file located in your `config` folder. You can customize all colors, radii, and rendering modes.

**Example Options:**
*   `USE_SOFTWARE_RENDERING=false` (Set to `true` to use the primitive renderer)
*   `PIE_MENU_SECTOR_COLOR_EVEN=0xC0606060`
*   `PIE_MENU_CANCEL_ZONE_HOVER_COLOR=0xC0B04232`

---
## History & Credits

This mod has a rich history of community contributions:

*   **Original Author:** The mod was originally created by **Cael**.
  *   [Cael's Original Project](https://github.com/CaelTheColher/KeybindsGalore)
*   **1.20.x Update:** It was first updated to 1.20.x by me, **HVB007**.
  *   [HVB007's GitHub](https://github.com/HVB007og/KeybindsGalore_HVB007_1.20.x)
*   **KeybindsGalore Plus:** The project was significantly enhanced and maintained as "KeybindsGalore Plus" by **AV306**, who added many features and bug fixes.
  *   [AV306's "KeybindsGalore Plus" Project](https://github.com/AV306/KeybindsGalore-Plus)

This current version for 1.21.x (1.4.1 to 1.6.0) builds upon all their hard work.
