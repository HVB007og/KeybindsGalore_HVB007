package net.hvb007.keybindsgalore;

import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//logger
//import net.minecraft.client.MinecraftClient;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


public class KeybindsManager {
//    private static final Logger LOGGER = LogManager.getLogger();


    // Creates an Hashmap? IDK what a hashmap is.
    private static final Map<InputUtil.Key, List<KeyBinding>> conflictingKeys = Maps.newHashMap();

    //When conflicting keys are pressed this creates a Array List of all the bindings bound to same key
    public static boolean handleConflict(InputUtil.Key key) {
        List<KeyBinding> matches = new ArrayList<>();
        KeyBinding[] keysAll = MinecraftClient.getInstance().options.allKeys;
        for (KeyBinding bind: keysAll) {
            if (bind.matchesKey(key.getCode(), -1)) {
                matches.add(bind);
            }
        }

        if (matches.size() > 1) {
            KeybindsManager.conflictingKeys.put(key, matches);
//            LOGGER.info("Conflicting key: " + key);

            // Define the array of keys to check against
            InputUtil.Key[] keysToCheck = {
                    InputUtil.fromTranslationKey("key.keyboard.tab"),
                    InputUtil.fromTranslationKey("key.keyboard.caps.lock"),
                    InputUtil.fromTranslationKey("key.keyboard.left.shift"),
                    InputUtil.fromTranslationKey("key.keyboard.left.control"),
                    InputUtil.fromTranslationKey("key.keyboard.space"),
                    InputUtil.fromTranslationKey("key.keyboard.left.alt"),
                    InputUtil.fromTranslationKey("key.keyboard.w"),
                    InputUtil.fromTranslationKey("key.keyboard.a"),
                    InputUtil.fromTranslationKey("key.keyboard.s"),
                    InputUtil.fromTranslationKey("key.keyboard.d")
            };

            // Check if the key is in the array
            boolean keyInArray = false;
            for (InputUtil.Key arrayKey : keysToCheck) {
                if (arrayKey.equals(key)) {
                    keyInArray = true;
                    KeybindsManager.conflictingKeys.remove(key);
                    break;
                }
            }

            return !keyInArray;

        } else {
            KeybindsManager.conflictingKeys.remove(key, matches);
            return false;
        }
    }

    //boolean returning if there are multiple bindings bound or not to the same key
    public static boolean isConflicting(InputUtil.Key key) {
        return conflictingKeys.containsKey(key);
    }

    //Initializes and opens the Circle selector thingy
    public static void openConflictMenu(InputUtil.Key key) {
        KeybindsScreen screen = new KeybindsScreen();
        screen.setConflictedKey(key);
        MinecraftClient.getInstance().setScreen(screen);
    }


    // A shortcut method to return the list of maps that the key is bound to.
    public static List<KeyBinding> getConflicting(InputUtil.Key key) {
        return conflictingKeys.get(key);
    }

}
