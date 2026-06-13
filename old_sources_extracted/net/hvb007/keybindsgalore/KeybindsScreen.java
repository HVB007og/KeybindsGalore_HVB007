/*
 * This class is modified from the PSI mod created by Vazkii
 * Psi Source Code: https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 *
 * HVB007: IDK What Part This credit refers to, if you want to know contact https://github.com/CaelTheColher as he is the maker of this mod
 * I am just updating it to 1.20.x
 */
package net.hvb007.keybindsgalore;

import com.mojang.blaze3d.systems.RenderSystem;
import net.hvb007.keybindsgalore.mixin.AccessorKeyBinding;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_293;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_333;
import net.minecraft.class_3532;
import net.minecraft.class_3675;
import net.minecraft.class_437;
import net.minecraft.class_757;
import net.minecraft.client.render.*;

public class KeybindsScreen extends class_437 {

    int timeIn = 0;
    int slotSelected = -1;

    private class_3675.class_306 conflictedKey = class_3675.field_16237;

    final class_310 mc;

    public KeybindsScreen() {
        super(class_333.field_18967);
        mc = class_310.method_1551();
    }

    @Override
    //Updated to use DrawContext instead of MatrixStack
    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        super.method_25394(context,mouseX,mouseY,delta);

        int x = field_22789 / 2;
        int y = field_22790 / 2;
        int maxRadius = 80;

        double angle = mouseAngle(x, y, mouseX, mouseY);

        //Determines how many segments to make for the circle selector thingy
        int segments = KeybindsManager.getConflicting(conflictedKey).size();
        float step = (float) Math.PI / 180;
        float degPer = (float) Math.PI * 2 / segments;

        slotSelected = -1;

        class_289 tess = class_289.method_1348();// IDK
        class_287 buf = tess.method_1349();

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.setShader(class_757::method_34540); //Updated to 1.20
        buf.method_1328(class_293.class_5596.field_27381, class_290.field_1576);

        //if cursor is in sector then it Highlights
        for (int seg = 0; seg < segments; seg++) {
            boolean mouseInSector = degPer * seg < angle && angle < degPer * (seg + 1);
            float radius = Math.max(0F, Math.min((timeIn + delta - seg * 6F / segments) * 40F, maxRadius));
            if (mouseInSector) {
                radius *= 1.025f;
            }

            int gs = 0x40;
            if (seg % 2 == 0) {
                gs += 0x19;
            }
            int r = gs;
            int g = gs;
            int b = gs;
            int a = 0x66;

            if (seg == 0) {
                buf.method_22912(x, y, 0).method_1336(r, g, b, a).method_1344();
            }

            if (mouseInSector) {
                slotSelected = seg;
                r = g = b = 0xFF;
            }

            for (float i = 0; i < degPer + step / 2; i += step) {
                float rad = i + seg * degPer;
                float xp = x + class_3532.method_15362(rad) * radius;
                float yp = y + class_3532.method_15374(rad) * radius;

                if (i == 0) {
                    buf.method_22912(xp, yp, 0).method_1336(r, g, b, a).method_1344();
                }
                buf.method_22912(xp, yp, 0).method_1336(r, g, b, a).method_1344();
            }
        }
        tess.method_1350();
        // IDK, This does something but im not sure
        for (int seg = 0; seg < segments; seg++) {
            boolean mouseInSector = degPer * seg < angle && angle < degPer * (seg + 1);
            float radius = Math.max(0F, Math.min((timeIn + delta - seg * 6F / segments) * 40F, maxRadius));
            if (mouseInSector) {
                radius *= 1.025f;
            }

            float rad = (seg + 0.5f) * degPer;
            float xp = x + class_3532.method_15362(rad) * radius;
            float yp = y + class_3532.method_15374(rad) * radius;
            String boundKey = class_2561.method_43471(KeybindsManager.getConflicting(conflictedKey).get(seg).method_1431()).getString();
            float xsp = xp - 4;
            float ysp = yp;
            String name = (mouseInSector ? class_124.field_1073 : class_124.field_1070) + boundKey;
            int width = field_22793.method_1727(name);
            if (xsp < x) {
                xsp -= width - 8;
            }
            if (ysp < y) {
                ysp -= 9;
            }

            // Updated To 1.20, uses DrawContext instead of textRenderer
            context.method_25303(field_22793,name, (int) xsp, (int) ysp, 0xFFFFFF);

        }
    }


    public void setConflictedKey(class_3675.class_306 key) {
        this.conflictedKey = key;
    }

    // Returns the angle of the mouse position relative to inputted x and y
    private static double mouseAngle(int x, int y, int mx, int my) {
        return (class_3532.method_15349(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }

    @Override
    //Checks for Conflicted keys every gametick and waits for input to press selected key once.
    public void method_25393() {
        super.method_25393();
        if (!class_3675.method_15987(class_310.method_1551().method_22683().method_4490(), conflictedKey.method_1444())) {
            mc.method_1507(null);
            if (slotSelected != -1) {
                class_304 bind = KeybindsManager.getConflicting(conflictedKey).get(slotSelected);
                ((AccessorKeyBinding) bind).setPressed(true);
                ((AccessorKeyBinding) bind).setTimesPressed(1);
            }
        }
        timeIn++;
    }

    @Override
    // IDK
    public boolean method_25421() {
        return false;
    }
}
