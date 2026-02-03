package io.wispforest.owo.ui.util;

import io.wispforest.owo.Owo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;

public final class UISounds {

    public static final class_3414 UI_INTERACTION = class_3414.method_47908(Owo.id("ui.owo.interaction"));

    private UISounds() {}

    @Environment(EnvType.CLIENT)
    public static void play(class_3414 event) {
        class_310.method_1551().method_1483().method_4873(class_1109.method_4758(event, 1));
    }

    @Environment(EnvType.CLIENT)
    public static void playButtonSound() {
        play(class_3417.field_15015.comp_349());
    }

    @Environment(EnvType.CLIENT)
    public static void playInteractionSound() {
        play(UI_INTERACTION);
    }
}
