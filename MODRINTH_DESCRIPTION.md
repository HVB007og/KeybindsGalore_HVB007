# Keybinds Galore (NeoForge Port)

Ever bound multiple actions to the same key because you ran out of comfortable keys to press? **Keybinds Galore** solves this by opening a sleek, customizable pie menu (or a list) whenever you press a key with conflicting binds, letting you choose exactly which action you meant to trigger.

*(This is a direct port and continuation of the original Fabric mod, now featuring advanced hardware-accelerated rendering natively on NeoForge without requiring OwoLib!)*

## ✨ Features

- **Conflict Resolution on the Fly:** Press a conflicting key and instantly see a beautiful pie menu. Move your mouse to the action you want, and release.
- **Hardware-Accelerated Rendering:** The pie menu is rendered smoothly using Minecraft's native `Tesselator`, providing a polished and high-performance UI without massive library dependencies.
- **Highly Customizable:** Change the colors, opacity, size, and behavior of the menu to fit your exact preferences.
- **Priority System:** Set certain categories (like "Movement") or specific keybinds (like "Attack") to always execute immediately, bypassing the menu or resolving the conflict automatically.
- **Smart Whitelisting/Blacklisting:** Ignore specific keys (like Shift, Ctrl, WASD) so they function normally even if multiple mods bind to them.
- **Custom Data Support:** Rename clunky mod keybind descriptions or hide their categories entirely for a cleaner menu.
- **Zero GUI Library Dependencies:** Completely self-contained for maximum compatibility.

## ⚙️ Configuration

The mod generates a `keybindsgalore.properties` file in your `config` folder. You can configure:
- **Colors & Aesthetics:** Customize the colors of even/odd sectors, the selected sector, the cancel zone, and overall opacity (`PIE_MENU_SECTOR_COLOR_EVEN`, `PIE_MENU_ALPHA`, etc.).
- **Behavior:** Switch between the circular menu and a traditional list menu (`USE_CIRCULAR_MENU`).
- **Overrides:** Enable `SHOW_CONFLICT_WARNINGS` to get helpful chat messages when a key conflict is detected but ignored due to your settings.
- **Performance:** Hardware rendering is enabled by default. If you experience issues, you can fallback to classic software rendering by setting `USE_SOFTWARE_RENDERING=true`.

You can reload your configurations in-game without restarting by assigning a key to the "Reload config file" action in the Keybinds Galore category.

## 🛠️ Custom Names & Colors

You can override the display name, hide the category name, or even set a specific color for individual keybind sectors! 

Create a file named `keybindsgalore_customdata.data` in your `config` folder and use the following format:

```yaml
key.attack:
    display_name = "Destroy!"
    sector_color = "0xFF0000"
    hide_category = true

key.use:
    display_name = "Place Block"
```

## 📜 Credits

- Originally created by **Cael** (Fabric).
- Updated and expanded by **HVB007**.
- Ported to NeoForge 1.21.1 with native Tesselator hardware rendering.

## 📖 History / Evolution

This mod has been through quite a journey across many Minecraft versions! Here is a brief look at its evolution:

- **1.6.x (Current):** Finally achieved smooth, hardware-accelerated pie menu rendering directly via Minecraft's native `Tesselator`. Removed the need for external GUI libraries while fixing all previous aliasing and rendering glitches!
- **1.5.2+1.21.11:** Primitive software-rendered sectors were enabled by default due to Minecraft rendering changes. It was functional but aliased.
- **1.5.1+1.21.11:** Updated to work with 1.21.11 after under-the-hood Minecraft changes.
- **1.4.1+1.21.6:** Temporarily changed from a radial pie menu to a box list menu due to severe rendering problems in newer versions. Big thanks to **diblenderbenzene** for handling edge cases!
- **1.4.1+1.21.5:** HVB007 took over from diblenderbenzene (who was on hiatus until 2027) to fix major rendering breakages caused by Minecraft 1.21.2+ updates. The menu worked but had visual glitches.
- **0.3-1.20.x:** Added the central "Cancel Keypress" zone, highlighting in red to safely abort a selection.
- **0.2-1.20:** Added an ignore list for common movement/modifier keys (Tab, Shift, Ctrl, Space, WASD) to prevent the menu from popping up when other mods bind to them.
- **0.1-1.20:** Initial port to 1.20.x by HVB007.

## 🤖 AI Declaration

Portions of this mod's code (specifically regarding the hardware-accelerated rendering rewrite for NeoForge and bug fixing) were written with the assistance of AI tools. All AI-generated code has been reviewed, tested, and verified for functionality by the developer.