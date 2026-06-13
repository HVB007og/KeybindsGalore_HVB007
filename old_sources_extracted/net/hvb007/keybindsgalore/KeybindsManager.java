package net.hvb007.keybindsgalore;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;

//logger
//import net.minecraft.client.MinecraftClient;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


public class KeybindsManager {
//    private static final Logger LOGGER = LogManager.getLogger();


    // Creates an Hashmap? IDK what a hashmap is.
    private static final Map<class_3675.class_306, List<class_304>> conflictingKeys = Maps.newHashMap();

    //When conflicting keys are pressed this creates a Array List of all the bindings bound to same key
    public static boolean handleConflict(class_3675.class_306 key) {
        List<class_304> matches = new ArrayList<>();
        class_304[] keysAll = class_310.method_1551().field_1690.field_1839;
        for (class_304 bind: keysAll) {
            if (bind.method_1417(key.method_1444(), -1)) {
                matches.add(bind);
            }
        }

        if (matches.size() > 1) {
            KeybindsManager.conflictingKeys.put(key, matches);
//            LOGGER.info("Conflicting key: " + key);

            // Define the array of keys to check against
            class_3675.class_306[] keysToCheck = {
                    class_3675.method_15981("key.keyboard.tab"),
                    class_3675.method_15981("key.keyboard.caps.lock"),
                    class_3675.method_15981("key.keyboard.left.shift"),
                    class_3675.method_15981("key.keyboard.left.control"),
                    class_3675.method_15981("key.keyboard.space"),
                    class_3675.method_15981("key.keyboard.left.alt"),
                    class_3675.method_15981("key.keyboard.w"),
                    class_3675.method_15981("key.keyboard.a"),
                    class_3675.method_15981("key.keyboard.s"),
                    class_3675.method_15981("key.keyboard.d")
            };

            // Check if the key is in the array
            boolean keyInArray = false;
            for (class_3675.class_306 arrayKey : keysToCheck) {
                if (arrayKey.equals(key)) {
                    keyInArray = true;
                    KeybindsManager.conflictingKeys.remove(key);
                    break;
                }
            }

            return !keyInArray;

        } else {
            KeybindsManager.conflictingKeys.remove(key);
            return false;
        }
    }

    //boolean returning if there are multiple bindings bound or not to the same key
    public static boolean isConflicting(class_3675.class_306 key) {
        return conflictingKeys.containsKey(key);
    }

    //Initializes and opens the Circle selector thingy
    public static void openConflictMenu(class_3675.class_306 key) {
//        if () {
//            break;
//        } else {
        KeybindsScreen screen = new KeybindsScreen();
        screen.setConflictedKey(key);
        class_310.method_1551().method_1507(screen);
    }


    // IDK, maby a shortcut method
    public static List<class_304> getConflicting(class_3675.class_306 key) {
        return conflictingKeys.get(key);
    }

}
