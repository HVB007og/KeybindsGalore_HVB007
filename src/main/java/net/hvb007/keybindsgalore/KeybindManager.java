package net.hvb007.keybindsgalore;

import net.hvb007.keybindsgalore.mixin.KeyMappingAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeybindManager {
    public static final Map<InputConstants.Key, List<KeyMapping>> conflictTable = new HashMap<>();
    public static final HashMap<Integer, KeyMapping> clickHoldKeys = new HashMap<>();
    public static final HashSet<InputConstants.Key> shownConflictWarnings = new HashSet<>();

    public static String safeGetTranslationKey(KeyMapping binding) {
        return binding.getName();
    }

    public static String safeGetCategory(KeyMapping binding) {
        return binding.getCategory();
    }

    public static void findAllConflicts() {
        KeybindsGalore.LOGGER.info("Scanning for conflicting keybinds...");
        Minecraft client = Minecraft.getInstance();

        validateAndMigratePriorityKeybinds(client);

        conflictTable.clear();
        shownConflictWarnings.clear();

        for (KeyMapping keybinding : client.options.keyMappings) {
            if (Configurations.FILTERED_CATEGORY_KEYS.stream().anyMatch(s -> s.equalsIgnoreCase(safeGetCategory(keybinding)))) {
                continue;
            }

            InputConstants.Key physicalKey = ((KeyMappingAccessor) keybinding).getKey();
            if (physicalKey.getValue() == GLFW.GLFW_KEY_UNKNOWN) {
                continue;
            }

            conflictTable.computeIfAbsent(physicalKey, k -> new ArrayList<>()).add(keybinding);
        }

        conflictTable.keySet().removeIf(key -> conflictTable.get(key).size() < 2);
    }

    private static void validateAndMigratePriorityKeybinds(Minecraft client) {
        boolean changed = false;
        List<String> newPriorities = new ArrayList<>();
        HashSet<String> uniqueKeys = new HashSet<>();

        for (String entry : Configurations.PRIORITY_KEYBINDS) {
            if (!entry.contains(":")) {
                for (KeyMapping kb : client.options.keyMappings) {
                    if (kb.getName().equals(entry)) {
                        String keyName = ((KeyMappingAccessor) kb).getKey().getName();
                        String newEntry = entry + ":" + keyName;
                        if (!uniqueKeys.contains(keyName)) {
                            newPriorities.add(newEntry);
                            uniqueKeys.add(keyName);
                            changed = true;
                        }
                        break;
                    }
                }
            } else {
                String keyName = entry.split(":", 2)[1];
                if (!uniqueKeys.contains(keyName)) {
                    newPriorities.add(entry);
                    uniqueKeys.add(keyName);
                } else {
                    changed = true;
                }
            }
        }

        if (changed) {
            Configurations.PRIORITY_KEYBINDS.clear();
            Configurations.PRIORITY_KEYBINDS.addAll(newPriorities);
            KeybindsGalore.configManager.saveConfigFile();
        }
    }

    public static boolean isIgnoredKey(InputConstants.Key key) {
        return Configurations.IGNORED_KEYS.contains(key.getValue()) ^ Configurations.INVERT_IGNORED_KEYS_LIST;
    }

    public static boolean isClickHoldKey(InputConstants.Key key) {
        return clickHoldKeys.containsKey(key.getValue());
    }

    public static boolean hasConflicts(InputConstants.Key key) {
        return conflictTable.containsKey(key);
    }

    public static void openConflictMenu(InputConstants.Key key) {
        Screen screen;
        if (Configurations.USE_CIRCULAR_MENU) {
            screen = new KeybindCircularScreen(key);
        } else {
            screen = new KeybindSelectorScreen(key);
        }
        Minecraft.getInstance().setScreen(screen);
    }

    public static List<KeyMapping> getConflicts(InputConstants.Key key) {
        return conflictTable.get(key);
    }

    public static KeyMapping getPriorityKey(InputConstants.Key key) {
        if (!hasConflicts(key)) return null;

        List<KeyMapping> conflicts = getConflicts(key);
        if (conflicts == null) return null;

        KeyMapping categoryPriority = null;

        for (KeyMapping kb : conflicts) {
            String priorityPair = safeGetTranslationKey(kb) + ":" + key.getName();
            if (Configurations.PRIORITY_KEYBINDS.stream().anyMatch(s -> s.equalsIgnoreCase(priorityPair))) {
                return kb;
            }

            if (categoryPriority == null) {
                if (Configurations.PRIORITY_CATEGORIES.stream().anyMatch(s -> s.equalsIgnoreCase(safeGetCategory(kb)))) {
                    categoryPriority = kb;
                }
            }
        }

        return categoryPriority;
    }

    public static void handleOnKeyPressed(InputConstants.Key key, CallbackInfo ci) {
        if (hasConflicts(key) && !isClickHoldKey(key)) {
            ci.cancel();
        }
    }

    public static void handleKeyPress(InputConstants.Key key, boolean pressed, CallbackInfo ci) {
        if (Configurations.DEBUG) {
            KeybindsGalore.LOGGER.info("[KBG DEBUG] Key Input: {} | Pressed: {}", key.getName(), pressed);
        }

        boolean wasSelectorScreenOpen = Minecraft.getInstance().screen instanceof KeybindSelectorScreen || Minecraft.getInstance().screen instanceof KeybindCircularScreen;

        if (hasConflicts(key)) {
            if (Configurations.DEBUG) {
                KeybindsGalore.LOGGER.info("[KBG DEBUG] Conflict detected for key: {}", key.getName());
            }

            if (!isClickHoldKey(key)) {
                KeyMapping priorityKey = getPriorityKey(key);

                if (priorityKey != null) {
                    if (Configurations.DEBUG) {
                        KeybindsGalore.LOGGER.info("[KBG DEBUG] Executing priority action: {} | State: {}", priorityKey.getName(), pressed ? "PRESSED" : "RELEASED");
                    }

                    ((KeyMappingAccessor) priorityKey).setIsDown(pressed);

                    if (pressed) {
                        int currentClicks = ((KeyMappingAccessor) priorityKey).getClickCount();
                        ((KeyMappingAccessor) priorityKey).setClickCount(currentClicks + 1);

                        KeybindsGalore.activePulseTarget = priorityKey;
                    } else {
                        if (KeybindsGalore.activePulseTarget == priorityKey) {
                            KeybindsGalore.activePulseTarget = null;
                        }
                    }

                    ci.cancel();

                    if (pressed && !shownConflictWarnings.contains(key)) {
                        if (Configurations.SHOW_CONFLICT_WARNINGS) {
                            Minecraft client = Minecraft.getInstance();
                            if (client.player != null) {
                                MutableComponent warningHeader = Component.literal("KeybindsGalore Warning: Key '")
                                    .append(Component.literal(key.getDisplayName().getString()).withStyle(ChatFormatting.GOLD))
                                    .append(Component.literal("' has conflicts. Prioritizing '"))
                                    .append(Component.translatable(priorityKey.getName()).withStyle(ChatFormatting.AQUA))
                                    .append(Component.literal("'."))
                                    .withStyle(ChatFormatting.RED);
                                client.player.displayClientMessage(warningHeader, false);

                                MutableComponent otherKeys = Component.literal("");
                                boolean first = true;
                                for (KeyMapping otherKb : getConflicts(key)) {
                                    if (otherKb == priorityKey) continue;
                                    if (!first) {
                                        otherKeys.append(Component.literal(", ").withStyle(ChatFormatting.GRAY));
                                    }
                                    otherKeys.append(Component.translatable(otherKb.getName()).withStyle(ChatFormatting.YELLOW));
                                    first = false;
                                }

                                if (!otherKeys.getString().isEmpty()) {
                                     client.player.displayClientMessage(
                                        Component.literal("Other conflicting keybinds: ").withStyle(ChatFormatting.GRAY)
                                        .append(otherKeys)
                                        .append(Component.literal(". Please rebind them in your controls! If you do not want to see these error Messages in Chat, Set SHOW_CONFLICT_WARNINGS=false in keybindsgalore.properties file in your config folder.").withStyle(ChatFormatting.GRAY)),
                                        false
                                    );
                                }
                            }
                        }
                        shownConflictWarnings.add(key);
                    }
                    return;
                }

                if (isIgnoredKey(key)) {
                    if (pressed && !shownConflictWarnings.contains(key)) {
                        if (Configurations.SHOW_CONFLICT_WARNINGS) {
                            Minecraft client = Minecraft.getInstance();
                            if (client.player != null) {
                                MutableComponent warningHeader = Component.literal("KeybindsGalore Warning: Ignored key '")
                                    .append(Component.literal(key.getDisplayName().getString()).withStyle(ChatFormatting.GOLD))
                                    .append(Component.literal("' has conflicts. Letting Minecraft handle it."))
                                    .withStyle(ChatFormatting.RED);
                                client.player.displayClientMessage(warningHeader, false);

                                MutableComponent otherKeys = Component.literal("");
                                boolean first = true;
                                for (KeyMapping otherKb : getConflicts(key)) {
                                    if (!first) {
                                        otherKeys.append(Component.literal(", ").withStyle(ChatFormatting.GRAY));
                                    }
                                    otherKeys.append(Component.translatable(otherKb.getName()).withStyle(ChatFormatting.YELLOW));
                                    first = false;
                                }

                                if (!otherKeys.getString().isEmpty()) {
                                     client.player.displayClientMessage(
                                        Component.literal("Conflicting keybinds: ").withStyle(ChatFormatting.GRAY)
                                        .append(otherKeys)
                                        .append(Component.literal(". Please rebind them in your controls!If you do not want to see these error Messages in Chat, Set SHOW_CONFLICT_WARNINGS=false in keybindsgalore.properties file in your config folder.").withStyle(ChatFormatting.GRAY)),
                                        false
                                    );
                                }
                            }
                        } else {
                            String conflictingKeybinds = getConflicts(key).stream()
                                .map(KeyMapping::getName)
                                .map(Component::translatable)
                                .map(Component::getString)
                                .collect(Collectors.joining(", "));
                            KeybindsGalore.LOGGER.info("KeybindsGalore: Ignored key '{}' has conflicts. Letting Minecraft handle it. Conflicts: {}", key.getDisplayName().getString(), conflictingKeybinds);
                        }
                        shownConflictWarnings.add(key);
                    }
                    return;
                }

                if (pressed) {
                    if (Configurations.DEBUG) {
                        KeybindsGalore.LOGGER.info("[KBG DEBUG] No priority found, opening conflict menu for key: {}", key.getName());
                    }
                    ci.cancel();
                    openConflictMenu(key);
                } else {
                    if (wasSelectorScreenOpen) {
                        Screen currentScreen = Minecraft.getInstance().screen;
                        if (currentScreen instanceof KeybindSelectorScreen) {
                            ((KeybindSelectorScreen) currentScreen).onKeyRelease();
                        } else if (currentScreen instanceof KeybindCircularScreen) {
                            ((KeybindCircularScreen) currentScreen).onKeyRelease();
                        }
                    }

                    List<KeyMapping> conflicts = getConflicts(key);
                    if (conflicts != null) {
                        for (KeyMapping kb : conflicts) {
                            ((KeyMappingAccessor) kb).setIsDown(false);
                        }
                    }
                    ci.cancel();
                }
                return;
            }
        } else if (Configurations.DEBUG) {
            KeybindsGalore.LOGGER.info("[KBG DEBUG] No conflicts for key: {}", key.getName());
        }

        if (!pressed) {
            if (KeybindsGalore.pulseTimer > 0 && KeybindsGalore.activePulseTarget != null) {
                InputConstants.Key targetKey = ((KeyMappingAccessor) KeybindsGalore.activePulseTarget).getKey();
                if (key.equals(targetKey)) {
                    ((KeyMappingAccessor) KeybindsGalore.activePulseTarget).setIsDown(true);
                    ci.cancel();
                }
            }
        }
    }

    public static void prioritizeAction(KeyMapping selected, InputConstants.Key key) {
        String newPair = selected.getName() + ":" + key.getName();

        Configurations.PRIORITY_KEYBINDS.removeIf(entry -> {
            if (entry.contains(":")) {
                String existingKeyName = entry.split(":", 2)[1];
                return existingKeyName.equalsIgnoreCase(key.getName());
            }
            return false;
        });

        Configurations.PRIORITY_KEYBINDS.add(newPair);
        KeybindsGalore.configManager.saveConfigFile();
        findAllConflicts();

        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(
                Component.translatable("text.keybindsgalore.action_prioritized",
                Component.translatable(selected.getName()),
                Component.translatable(key.getName())), false
            );
        }
    }

    public static void removePriority(InputConstants.Key key) {
        boolean removed = Configurations.PRIORITY_KEYBINDS.removeIf(entry -> {
            if (entry.contains(":")) {
                String existingKeyName = entry.split(":", 2)[1];
                return existingKeyName.equalsIgnoreCase(key.getName());
            }
            return false;
        });

        if (removed) {
            KeybindsGalore.configManager.saveConfigFile();
            findAllConflicts();

            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable("text.keybindsgalore.priority_removed",
                    Component.translatable(key.getName())), false
                );
            }
        }
    }
}
