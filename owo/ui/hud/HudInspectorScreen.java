package io.wispforest.owo.ui.hud;

import io.wispforest.owo.ui.util.CommandOpenedScreen;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;

public class HudInspectorScreen extends class_437 implements CommandOpenedScreen {

    public HudInspectorScreen() {
        super(class_2561.method_43473());
        if (Hud.adapter != null) {
            Hud.suppress = true;
            Hud.adapter.enableInspector = true;
        }
    }

    @Override
    public void method_25394(class_332 graphics, int mouseX, int mouseY, float delta) {
        super.method_25394(graphics, mouseX, mouseY, delta);

        if (Hud.adapter == null) return;
        Hud.adapter.method_25394(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void method_25432() {
        if (Hud.adapter != null) {
            Hud.suppress = false;
            Hud.adapter.enableInspector = false;
        }
    }
}
