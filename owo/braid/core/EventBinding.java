package io.wispforest.owo.braid.core;

import io.wispforest.owo.braid.core.events.UserEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_310;
import net.minecraft.class_3675;

public abstract class EventBinding {

    private final List<EventSlot> bufferedEvents = new ArrayList<>();

    public EventSlot add(UserEvent event) {
        var slot = new EventSlot(event);
        this.bufferedEvents.add(slot);

        return slot;
    }

    List<EventSlot> poll() {
        var events = new ArrayList<>(this.bufferedEvents);
        this.bufferedEvents.clear();

        return events;
    }

    public abstract boolean isKeyPressed(int keyCode);

    public KeyModifiers activeModifiers() {
        return new KeyModifiers(
            (this.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) || this.isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT) ? GLFW.GLFW_MOD_SHIFT : 0)
            | (this.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || this.isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL) ? GLFW.GLFW_MOD_CONTROL : 0)
            | (this.isKeyPressed(GLFW.GLFW_KEY_LEFT_ALT) || this.isKeyPressed(GLFW.GLFW_KEY_RIGHT_ALT) ? GLFW.GLFW_MOD_ALT : 0)
            | (this.isKeyPressed(GLFW.GLFW_KEY_RIGHT_SUPER) || this.isKeyPressed(GLFW.GLFW_KEY_RIGHT_SUPER) ? GLFW.GLFW_MOD_SUPER : 0)
            | (this.isKeyPressed(GLFW.GLFW_KEY_NUM_LOCK) ? GLFW.GLFW_MOD_NUM_LOCK : 0)
            | (this.isKeyPressed(GLFW.GLFW_KEY_CAPS_LOCK) ? GLFW.GLFW_MOD_CAPS_LOCK : 0)
        );
    }

    public static class EventSlot {
        final UserEvent event;
        private boolean handled = false;

        public EventSlot(UserEvent event) {
            this.event = event;
        }

        public boolean handled() {
            return this.handled;
        }

        void markHandled() {
            this.handled = true;
        }
    }

    // ---

    public static class Headless extends EventBinding {
        @Override
        public boolean isKeyPressed(int keyCode) {
            return false;
        }
    }

    public static class Default extends EventBinding {
        @Override
        public boolean isKeyPressed(int keyCode) {
            return class_3675.method_15987(class_310.method_1551().method_22683(), keyCode);
        }
    }
}
