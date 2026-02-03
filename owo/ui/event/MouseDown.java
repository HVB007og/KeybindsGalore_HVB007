package io.wispforest.owo.ui.event;

import io.wispforest.owo.util.EventStream;
import net.minecraft.class_11909;

public interface MouseDown {
    boolean onMouseDown(class_11909 click, boolean doubled);

    static EventStream<MouseDown> newStream() {
        return new EventStream<>(subscribers -> (click, doubled) -> {
            var anyTriggered = false;
            for (var subscriber : subscribers) {
                anyTriggered |= subscriber.onMouseDown(click, doubled);
            }
            return anyTriggered;
        });
    }
}
