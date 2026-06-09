# Keybinds Galore v1.6.1 (NeoForge 1.21.1)

This update brings a major overhaul to how the mod renders, finally bringing smooth, hardware-accelerated visuals to NeoForge without needing any extra library mods!

### ✨ What's New
* **Native Hardware-Accelerated Rendering:** The pie menu is now rendered using Minecraft's native `Tesselator`. This means you get the perfectly smooth, anti-aliased circles that previously required OwoLib on Fabric, but completely standalone!
* **Zero Dependencies:** OwoLib is no longer required. Just drop the mod in your folder and play.
* **New Customization Options:** Added new color properties to `keybindsgalore.properties` for granular control over the pie menu's appearance (Even/Odd sectors, Cancel Zone hover states, etc.).
* **Text Highlight:** Selected keybind text now features a clean, semi-transparent background highlight to make reading easier.

### 🐛 Bug Fixes
* **Fixed "Screen Blur" Glitch:** Resolved an issue where opening the circular pie menu would incorrectly apply Minecraft's default background blur to the entire screen. The menu now correctly darkens the background without blurring it.
* **Fixed Click Count Logic:** Fixed an internal bug where the click count was being set to the raw keycode value rather than `1` when an action was selected.
* **Math Precision Fix:** Rewrote the fallback software-rendering scanline interpolation to fix tiny visual gaps between triangles.

### ⚙️ Technical
* Ported successfully to NeoForge 1.21.1.