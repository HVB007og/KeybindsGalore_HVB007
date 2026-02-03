package io.wispforest.owo.braid.display;

import io.wispforest.owo.braid.core.events.MouseMoveEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2dc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_11659;
import net.minecraft.class_12075;
import net.minecraft.class_243;
import net.minecraft.class_4587;
import net.minecraft.class_765;

public class BraidDisplayBinding {

    private static final List<BraidDisplay> ACTIVE_DISPLAYS = new ArrayList<>();

    // ---

    public static void activate(BraidDisplay display) {
        ACTIVE_DISPLAYS.add(display);
    }

    public static void deactivate(BraidDisplay display) {
        ACTIVE_DISPLAYS.remove(display);
    }

    // ---

    public static @Nullable DisplayHitResult targetDisplay;

    @ApiStatus.Internal
    public static @Nullable DisplayHitResult queryTargetDisplay(class_243 rayOrigin, class_243 rayDirection) {
        DisplayHitResult closestResult = null;
        double closestRayOffset = Double.POSITIVE_INFINITY;

        for (var display : ACTIVE_DISPLAYS) {
            var result = display.quad.hitTest(rayOrigin, rayDirection);
            if (result == null || result.t() >= closestRayOffset) continue;

            closestResult = new DisplayHitResult(display, result.point());
            closestRayOffset = result.t();
        }

        return closestResult;
    }

    @ApiStatus.Internal
    public static void onDisplayHit(DisplayHitResult targetDisplay) {
        var app = targetDisplay.display.app;

        var cursorX = targetDisplay.point.x() * app.surface.width();
        var cursorY = targetDisplay.point.y() * app.surface.height();

        app.eventBinding.add(new MouseMoveEvent(cursorX, cursorY));
    }

    @ApiStatus.Internal
    public static void updateAndDrawDisplays() {
        for (var display : ACTIVE_DISPLAYS) {
            display.updateAndDrawApp();
        }
    }

    @ApiStatus.Internal
    public static void renderAutomaticDisplays(class_4587 matrices, class_12075 camera, class_11659 nodeCollector) {
        for (var display : ACTIVE_DISPLAYS) {
            if (!display.renderAutomatically) continue;

            matrices.method_22903();
            matrices.method_61958(display.quad.pos.method_1020(camera.field_63078));

            display.render(matrices, nodeCollector, class_765.field_32767);

            matrices.method_22909();
        }
    }

    // ---

    public record DisplayHitResult(BraidDisplay display, Vector2dc point) {}
}
