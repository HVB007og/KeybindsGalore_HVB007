Full Customization: Configure colors, transparency, and rendering modes in the config file.
⚠️ Requirements
Fabric API
History & Changelog
1.6.2 (Current):
Config: Replaced `FILTER_DEBUG_KEYS` with the more flexible `FILTERED_CATEGORY_KEYS` list, allowing any category to be excluded from conflicts.
Config: Fixed hex value parsing in the properties file and added support for generic string lists.
UX: Conflict warnings in chat now include instructions on how to disable them via config.
Internal: Refined code by removing legacy reflection logic for better stability.
1.6.1:
Rendering Overhaul: Removed the owo-lib dependency! Hardware-accelerated rendering is now handled natively via Minecraft's Tesselator for a perfectly smooth, standalone experience.
Bug Fixes: Fixed an issue where the background blurred incorrectly when the menu opened.
Config: Added new configurable colors for the menu sectors and hover states.
1.6.0:
Rendering Overhaul: Implemented owo-lib for hardware rendering.
Visual Fixes: Fixed the "diamond hole" issue in software rendering and removed transparency artifacts.
Config: Added options to toggle rendering modes and customize all UI colors.
1.5.2 (1.21.11):
Primitive working sectors enabled by default. Functional but basic.
1.21.5:
Big thanks to diblenderbenzene (AV306) for maintaining KeybindsGalore Plus and fixing many bugs.
Updated to support 1.21.x despite major Minecraft rendering changes.
1.21.6:
Temporary Box-type menu implemented while pie menu rendering was broken.
Updated to 1.20.x/1.21.x by HVB007 Original mod by Cael: KeybindsGalore

🤖 AI Declaration
Portions of this mod's code (specifically regarding the hardware-accelerated rendering rewrite and bug fixing) were written with the assistance of AI tools.