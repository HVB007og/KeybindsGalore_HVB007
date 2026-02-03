package io.wispforest.owo.ui.util;

import io.wispforest.owo.ui.core.CursorStyle;
import org.lwjgl.glfw.GLFW;

import java.util.EnumMap;
import net.minecraft.class_1041;
import net.minecraft.class_310;

public class CursorAdapter {

    protected static final CursorStyle[] ACTIVE_STYLES = {CursorStyle.POINTER, CursorStyle.TEXT, CursorStyle.HAND, CursorStyle.CROSSHAIR, CursorStyle.MOVE, CursorStyle.HORIZONTAL_RESIZE, CursorStyle.VERTICAL_RESIZE, CursorStyle.NWSE_RESIZE, CursorStyle.NESW_RESIZE, CursorStyle.NOT_ALLOWED};

    protected final EnumMap<CursorStyle, Long> cursors = new EnumMap<>(CursorStyle.class);
    protected final long windowHandle;

    protected CursorStyle lastCursorStyle = CursorStyle.POINTER;
    protected boolean disposed = false;

    protected CursorAdapter(long windowHandle) {
        this.windowHandle = windowHandle;
        for (var style : ACTIVE_STYLES) {
            var pointer = GLFW.glfwCreateStandardCursor(style.glfw);
            if (pointer == 0) continue;

            this.cursors.put(style, pointer);
        }
    }

    public static CursorAdapter ofClientWindow() {
        return new CursorAdapter(class_310.method_1551().method_22683().method_4490());
    }

    public static CursorAdapter ofWindow(class_1041 window) {
        return new CursorAdapter(window.method_4490());
    }

    public static CursorAdapter ofWindow(long windowHandle) {
        return new CursorAdapter(windowHandle);
    }

    public void applyStyle(CursorStyle style) {
        if (this.disposed || this.lastCursorStyle == style) return;

        if (style == CursorStyle.NONE) {
            GLFW.glfwSetCursor(this.windowHandle, 0);
        } else {
            GLFW.glfwSetCursor(this.windowHandle, this.cursors.getOrDefault(style, 0L));
        }
        this.lastCursorStyle = style;
    }

    public void dispose() {
        if (this.disposed) return;

        this.cursors.values().forEach(GLFW::glfwDestroyCursor);
        this.disposed = true;
    }

}
