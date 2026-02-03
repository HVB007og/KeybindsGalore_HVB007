package io.wispforest.owo.ui.hud;

import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.UIComponent;
import io.wispforest.owo.ui.event.ClientRenderCallback;
import io.wispforest.owo.ui.event.WindowResizeCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A utility for displaying owo-ui components on the
 * in-game HUD - rendered during {@link HudRenderCallback}
 */
public class Hud {

    static @Nullable OwoUIAdapter<FlowLayout> adapter = null;
    static boolean suppress = false;

    private static final Map<class_2960, UIComponent> activeComponents = new HashMap<>();
    private static final List<Consumer<FlowLayout>> pendingActions = new ArrayList<>();

    /**
     * Add a new component to be rendered on the in-game HUD.
     * The root container used by the HUD does not support layout
     * positioning - the component supplied by {@code component}
     * must be explicitly positioned via either {@link io.wispforest.owo.ui.core.Positioning#absolute(int, int)}
     * or {@link io.wispforest.owo.ui.core.Positioning#relative(int, int)}
     *
     * @param id        An ID uniquely describing this HUD component
     * @param component A function creating the component
     *                  when the HUD is first rendered
     */
    public static void add(class_2960 id, Supplier<UIComponent> component) {
        pendingActions.add(flowLayout -> {
            var instance = component.get();

            flowLayout.child(instance);
            activeComponents.put(id, instance);
        });
    }

    /**
     * Remove the HUD component described by the given ID
     *
     * @param id The ID of the HUD component to remove
     */
    public static void remove(class_2960 id) {
        pendingActions.add(flowLayout -> {
            var component = activeComponents.get(id);
            if (component == null) return;

            flowLayout.removeChild(component);
            activeComponents.remove(id);
        });
    }

    /**
     * Get the HUD component described by the given ID
     *
     * @param id The ID of the HUD component to query
     * @return The relevant HUD component, or {@code null} if there is none
     */
    public static @Nullable UIComponent getComponent(class_2960 id) {
        return activeComponents.get(id);
    }

    /**
     * @return {@code true} if there is an active HUD component described by {@code id}
     */
    public static boolean hasComponent(class_2960 id) {
        return activeComponents.containsKey(id);
    }

    private static void initializeAdapter() {
        var window = class_310.method_1551().method_22683();
        adapter = OwoUIAdapter.createWithoutScreen(
            0, 0, window.method_4486(), window.method_4502(), HudContainer::new
        );

        adapter.inflateAndMount();
    }

    static {
        WindowResizeCallback.EVENT.register((client, window) -> {
            if (adapter == null) return;
            adapter.moveAndResize(0, 0, window.method_4486(), window.method_4502());
        });

        ClientRenderCallback.BEFORE.register(client -> {
            if (client.field_1687 == null) return;
            if (!pendingActions.isEmpty()) {
                if (adapter == null) initializeAdapter();

                pendingActions.forEach(action -> action.accept(adapter.rootComponent));
                pendingActions.clear();
            }
        });

        HudElementRegistry.addLast(class_2960.method_60655("owo", "owo_ui_hud"), (context, tickCounter) -> {
            if (adapter == null || suppress || class_310.method_1551().field_1690.field_1842) return;
            adapter.method_25394(context, -69, -69, tickCounter.method_60637(false));
        });
    }
}
