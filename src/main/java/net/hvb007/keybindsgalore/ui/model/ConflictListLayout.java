package net.hvb007.keybindsgalore.ui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;

public final class ConflictListLayout {
    public record Box(int x, int y, int width, int height) {
    }

    public record Result(
            int centerX,
            int centerY,
            int halfCount,
            int topStartY,
            int bottomStartY,
            List<Box> boxes) {
        public Result {
            boxes = List.copyOf(boxes);
        }
    }

    public Result calculate(
            List<String> labels,
            int centerX,
            int centerY,
            int screenWidth,
            int lineHeight,
            int horizontalPadding,
            int verticalPadding,
            int spacing,
            int screenMargin,
            ToIntFunction<String> textWidth) {
        Objects.requireNonNull(labels, "labels");
        Objects.requireNonNull(textWidth, "textWidth");

        int count = labels.size();
        int halfCount = count / 2;
        int boxHeight = lineHeight + 2 * verticalPadding;
        int maxWidth = 0;
        for (String label : labels) {
            maxWidth = Math.max(maxWidth, textWidth.applyAsInt(label) + 2 * horizontalPadding);
        }
        maxWidth = Math.max(0, Math.min(maxWidth, screenWidth - 2 * screenMargin));

        int topHeight = halfCount == 0 ? 0 : halfCount * boxHeight + (halfCount - 1) * spacing;
        int topStartY = centerY - spacing / 2 - topHeight;
        int bottomStartY = centerY + spacing / 2;
        int x = centerX - maxWidth / 2;

        List<Box> boxes = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            int y = index < halfCount
                    ? topStartY + index * (boxHeight + spacing)
                    : bottomStartY + (index - halfCount) * (boxHeight + spacing);
            boxes.add(new Box(x, y, maxWidth, boxHeight));
        }

        return new Result(centerX, centerY, halfCount, topStartY, bottomStartY, boxes);
    }
}
