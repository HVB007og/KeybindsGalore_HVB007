package io.wispforest.owo.braid.core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_327;

public class TextLayout {

    public static EditMetrics measure(class_327 font, String text, class_2583 baseStyle, int maxWidth) {
        var lines = new ArrayList<Line>();

        font.method_27527().method_27485(
            text,
            maxWidth,
            baseStyle,
            false,
            (style, start, end) -> lines.add(new Line(style, start, end))
        );

        if (text.endsWith("\n")) {
            lines.add(new Line(baseStyle, text.length(), text.length()));
        }

        if (lines.isEmpty()) {
            lines.add(new Line(baseStyle, 0, 0));
        }

        // ---

        var textWidth = 0;
        var textHeight = 0;
        var lineMetrics = new ArrayList<LineMetrics>();

        for (var line : lines) {
            var lineWidth = font.method_27525(line.substring(text));
            lineMetrics.add(new LineMetrics(line.beginIdx, line.endIdx, lineWidth));

            textWidth = Math.max(textWidth, lineWidth);
            textHeight += font.field_2000;
        }

        return new EditMetrics(textWidth, textHeight, lineMetrics);
    }

    public record LineMetrics(int beginIdx, int endIdx, double width) {
        public String substring(String fullContent) {
            return fullContent.substring(this.beginIdx, this.endIdx);
        }
    }

    public record EditMetrics(int width, int height, List<LineMetrics> lineMetrics) {}

    private record Line(class_2583 style, int beginIdx, int endIdx) {
        public class_2561 substring(String fullContent) {
            return class_2561.method_43470(fullContent.substring(this.beginIdx, this.endIdx)).method_10862(this.style);
        }
    }
}
