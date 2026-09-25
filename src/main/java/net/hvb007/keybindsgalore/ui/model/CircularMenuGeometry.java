package net.hvb007.keybindsgalore.ui.model;

public final class CircularMenuGeometry {
    private static final double TWO_PI = Math.PI * 2.0;

    private CircularMenuGeometry() {
    }

    public static Selection select(double deltaX, double deltaY, double cancelRadius, int sectorCount) {
        if (sectorCount <= 0) {
            return new Selection(-1, true);
        }

        double distance = Math.hypot(deltaX, deltaY);
        if (distance <= cancelRadius) {
            return new Selection(-1, true);
        }

        double angle = (Math.atan2(deltaY, deltaX) + TWO_PI) % TWO_PI;
        int sector = (int) (angle / (TWO_PI / sectorCount));
        return new Selection(Math.min(sector, sectorCount - 1), false);
    }

    public record Selection(int sectorIndex, boolean cancelZone) {
    }
}
