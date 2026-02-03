package io.wispforest.owo.braid.display;

import net.minecraft.class_243;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import org.joml.Vector2dc;

public final class DisplayQuad {
    public final class_243 pos;
    public final class_243 top;
    public final class_243 left;
    public final class_243 normal;

    public DisplayQuad(class_243 pos, class_243 top, class_243 left) {
        this.pos = pos;
        this.top = top;
        this.left = left;
        this.normal = this.left.method_1036(this.top);
    }

    public class_243 unproject(Vector2dc point) {
        return this.pos.method_1019(this.top.method_1021(point.x())).method_1019(this.left.method_1021(point.y()));
    }

    public @Nullable HitTestResult hitTest(class_243 origin, class_243 direction) {
        var t = this.pos.method_1020(origin).method_1026(this.normal) / direction.method_1026(this.normal);
        if (t < 0) return null;

        var candidatePoint = origin.method_1019(direction.method_1021(t)).method_1020(this.pos);

        var widthSquared = this.top.method_1027();
        var heightSquared = this.left.method_1027();

        var point = new Vector2d(
            candidatePoint.method_1026(this.top) / widthSquared,
            candidatePoint.method_1026(this.left) / heightSquared
        );

        return point.x > 0 && point.x < 1 && point.y > 0 && point.y < 1
            ? new HitTestResult(point, t)
            : null;
    }

    public record HitTestResult(Vector2dc point, double t) {}
}
