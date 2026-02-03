package io.wispforest.owo.ui.event;

import io.wispforest.owo.util.EventStream;
import net.minecraft.class_11905;

public interface CharTyped {
    boolean onCharTyped(class_11905 input);

    static EventStream<CharTyped> newStream() {
        return new EventStream<>(subscribers -> (input) -> {
            var anyTriggered = false;
            for (var subscriber : subscribers) {
                anyTriggered |= subscriber.onCharTyped(input);
            }
            return anyTriggered;
        });
    }
}
