# KeybindsGalore Plus

A Fabric client-side mod that completely overhauls how Minecraft handles keybind conflicts. Instead of actions randomly overriding each other when bound to the same key, Keybinds Galore intercepts the conflict and opens an intuitive selection menu (either a list or a customizable Pie Menu), allowing you to choose exactly which action to perform on the fly!

Originally created by Cael, this version has been extensively upgraded by HVB007 for modern Minecraft versions with a massive feature set.

---

## ✨ Features

*   **Conflict Resolution Menu:** Instantly opens a menu to resolve conflicts when multiple actions are bound to the same physical key.
*   **Pie Menu Support:** Optional circular Pie Menu for faster, gesture-based selection.
*   **Hardware-Accelerated Rendering:** The Pie Menu utilizes native OpenGL/Tesselator rendering for maximum performance and a smooth visual aesthetic.
*   **Smart Priority System:** Do you want `Space` to ALWAYS jump, even if something else is bound to it? Use the new Priority System to enforce strict exclusivity for specific actions on specific keys.
*   **In-Game Config GUI:** Fully integrated with ModMenu and Cloth Config for seamless in-game customization. Change colors, transparency, and behaviors without ever editing a text file.
*   **Filtered Categories:** Bulk-ignore specific mod categories (like "Debug" or "Minimap") from ever triggering the conflict menu.
*   **Zero Dependencies:** Runs natively on Fabric API. No heavy UI libraries required (Cloth Config is optional but highly recommended for the GUI).

## 🚀 How to Use the Priority System

If you have two actions on the same key (e.g., `Jump` and `Mod Action` both on `Space`), the Pie Menu will appear. If you want one of them to ALWAYS happen without seeing the menu:

1. Press the **Capture Hotkey** (Default: `K`) while in-game.
2. The game will prompt you to "Press any key...". Press the conflicting key (e.g., `Space`).
3. A menu will appear showing all actions bound to that key.
4. Click the action you want to prioritize (e.g., `Jump`).
5. From now on, pressing `Space` will instantly execute `Jump` and ignore the other actions.

You can easily remove priorities using the "Remove Current Priority" button in the same capture menu, or manage your full list via ModMenu!

## ⚙️ Configuration
Access the settings via **ModMenu** (requires Cloth Config API).

*   **General:** Enable debug logging.
*   **Behavior:** Toggle between the List Menu and Pie Menu, manage your Filtered Categories, and view your Priority Actions.
*   **Visual:** Completely customize the Pie Menu's colors, transparency, hover effects, and scale using a live RGBA color picker.

## ⚠️ Requirements
*   [Fabric API](https://modrinth.com/mod/fabric-api)
*   **Recommended:** [ModMenu](https://modrinth.com/mod/modmenu) and [Cloth Config API](https://modrinth.com/mod/cloth-config) (for the in-game settings GUI).

## 🤖 AI Declaration
Portions of this mod's code (specifically regarding the hardware-accelerated rendering rewrite, config GUI implementation, and advanced input prioritization logic) were written with the assistance of AI tools. All AI-generated code has been rigorously reviewed, tested, and verified for functionality by the developer.