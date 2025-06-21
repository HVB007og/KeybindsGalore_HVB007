# KeybindsGalore Plus

> [!WARNING]<br>
> Version 1.4.0 has been removed from Modrinth as it contains too many bugs.
> Version 1.4.1 will be published soon.

> [!NOTE]<br>
> This project is a fork of KeybindsGalore, originally by Cael and updated to 1.20 by HVB007.
> <br>[HVB007's project is here](https://github.com/HVB007og/KeybindsGalore_HVB007_1.20.x), and Cael's [original project is here](https://github.com/CaelTheColher/KeybindsGalore).

<br>

A keybind conflict management and general quality-of-life mod!

- Provides a **pie menu** for conflicting keybinds
- **Corrects vanilla conflict handling**, similar to [Keybinds Fix](https://www.curseforge.com/minecraft/mc-mods/keybind-fix)
- Supports **mouse buttons**
- **Fixes many bugs** and adds many improvements to the original mod
- Supports **1.20.x** and **1.21.x**
- Supports **Fabric** and **Forge/NeoForge (via Sinytra Connector)**

<div style="display: flex; justify-content: center; align-items: center;">
  <img src="https://github.com/AV306/KeybindsGalore-Plus/blob/14b7001f913c9bf089ef4fc41934c60dcf0db275/images/kbg_plus_demo.gif?raw=true" max-height=400 />
</div>

<br>

## How to Use

KeybindsGalore+ will automatically detect conflicting keybinds!

- **Pressing** a conflicted key *down* will **open the pie menu**
- **Releasing** a conflicted key (when the pie menu is displayed) will **activate the highlighted action** and **close** the pie menu
- **Clicking** on a section of the pie menu (without releasing the key) will **activate the highlighted action** and **deactivate** it when the key is **released**

NB: When the pie menu *opens*, all actions currently activated (e.g. holding a key down) will be deactivated.
<br>

## Modifications to Original

- Optimised conflict searching
- Keybind labels now show their category along with their name
- Customisable keybind labels (see [this issue](https://github.com/AV306/KeybindsGalore-Plus/issues/3))
- Label texts no longer run off the screen
- Fully customisable pie menu
- Allows compatibility with non-vanilla keybinds (insert your mod's keybinds into the [conflict table](https://github.com/AV306/KeybindsGalore-Plus/blob/1.21/src/main/java/me/av306/keybindsgaloreplus/KeybindManager.java) whenever convenient!)
- **And more...**

<br>

## Bug-busters :heart:

Bug reports and feature requests VERY welcome!

- lightmcxx
- mo9713
- Poopooracoocoo
- GabanKillasta
- Tgaisen
- GhostIsBeHere
- Alwis2000
- StarsShine11904
- ClutchMasterYT
- GuardedHoney53
- BumbleTree
- Mideks
- UNI717
- IG114514
- WxAaRoNxW

(let me know if I missed you!)

<br>

## Roadmap

### 1.3.5

- Bugfixes

### 1.4.0

- Blacklist/whitelist toggle for ignored key list
- Per-keybind overrides in custom data for:
  - Label text (category + name / name only / custom text)
  - sector colour
  - sector opacity
- Removal of non-lazy conflict check

(need more features? make a [feature request](https://github.com/AV306/KeybindsGalore-Plus/issues)!)

<br>

## [[ Old README below ]]

# KeybindsGalore_HVB007_1.20.x
Updated to 1.20 by HVB007.

>Github : https://github.com/HVB007og/KeybindsGalore_HVB007_1.20.x 
>Fabric mod Which opens an popup when there are multiple actions bound to the same key in the Minecraft>controls>Keybinds settings. then choose one of the options to use.

>Changelog keybindsgalore-0.2-1.20:

Works with 1.20.2

Added Feature: Will not open the menu when pressing certain keys (Due to keys compatibility with other mods) as follows: 
1.tab 
2.caps lock 
3.left shift 
4.left control 
5.space 
6.left alt 
7.w 
8.a 
9.s 
10.d

Future Feature: Add mod setting to configure the keys to disable.

>Does not support conflicting Keybinds not using the Minecraft Keybinds settings.

Updated to 1.20.x by HVB007

Updated version of keybindsgalore by Cael : https://github.com/CaelTheColher/KeybindsGalore
