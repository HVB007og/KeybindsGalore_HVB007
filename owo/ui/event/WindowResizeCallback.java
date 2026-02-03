package io.wispforest.owo.ui.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.class_1041;
import net.minecraft.class_310;

public interface WindowResizeCallback {

    Event<WindowResizeCallback> EVENT = EventFactory.createArrayBacked(WindowResizeCallback.class, callbacks -> (client, window) -> {
        for (var callback : callbacks) {
            callback.onResized(client, window);
        }
    });

    /**
     * Called after the client's window has been resized
     *
     * @param client The currently active client
     * @param window The window which was resized
     */
    void onResized(class_310 client, class_1041 window);

}
