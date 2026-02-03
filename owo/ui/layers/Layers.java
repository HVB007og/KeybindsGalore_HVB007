package io.wispforest.owo.ui.layers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.core.ParentUIComponent;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.util.pond.OwoScreenExtension;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.class_2960;
import net.minecraft.class_437;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * A system for adding owo-ui components onto existing screens.
 * <p>
 * You can create a new layer by calling {@link #add(BiFunction, Consumer, Class[])}. The
 * second argument to this function is the instance initializer, which is where you configure
 * instances of your layer added onto screens when they get initialized. This is the place to
 * configure the UI adapter of your layer as well as building your UI tree onto the root
 * component of said adapter
 * <p>
 * Just like proper owo-ui screens, layers preserve state when the client's window
 * is resized - they are only initialized once, when the screen is first opened
 */
public final class Layers {

    /**
     * The event phase during which owo-ui layer instances are created and
     * initialized. This runs after the default phase
     */
    public static final class_2960 INIT_PHASE = Owo.id("init-layers");

    private static final Multimap<Class<? extends class_437>, Layer<?, ?>> LAYERS = HashMultimap.create();

    /**
     * Add a new layer to the given screens
     *
     * @param rootComponentMaker  A function which will create the root component of this layer
     * @param instanceInitializer A function which will initialize any instances of this layer which get created.
     *                            This is where you add components or configure the UI adapter of the generated layer instance
     * @param screenClasses       The screens onto which to add the new layer
     */
    @SafeVarargs
    public static <S extends class_437, R extends ParentUIComponent> Layer<S, R> add(BiFunction<Sizing, Sizing, R> rootComponentMaker, Consumer<Layer<S, R>.Instance> instanceInitializer, Class<? extends S>... screenClasses) {
        final var layer = new Layer<>(rootComponentMaker, instanceInitializer);
        for (var screenClass : screenClasses) {
            LAYERS.put(screenClass, layer);
        }
        return layer;
    }

    /**
     * Get all layers associated with a given screen
     */
    @SuppressWarnings("unchecked")
    public static <S extends class_437> Collection<Layer<S, ?>> getLayers(Class<S> screenClass) {
        return (Collection<Layer<S, ?>>) (Object) LAYERS.get(screenClass);
    }

    /**
     * Get all layer instances currently present on the given screen
     */
    @SuppressWarnings("unchecked")
    public static <S extends class_437> List<Layer<S, ?>.Instance> getInstances(S screen) {
        return (List<Layer<S, ?>.Instance>) (Object) ((OwoScreenExtension) screen).owo$getInstancesView();
    }

    static {
        ScreenEvents.AFTER_INIT.addPhaseOrdering(Event.DEFAULT_PHASE, INIT_PHASE);
        ScreenEvents.AFTER_INIT.register(INIT_PHASE, (client, screeen, scaledWidth, scaledHeight) -> {
            ((OwoScreenExtension) screeen).owo$updateLayers();

            ScreenEvents.remove(screeen).register(screen -> {
                for (var instance : getInstances(screen)) {
                    instance.adapter.dispose();
                }
            });

            ScreenEvents.beforeRender(screeen).register((screen, context, mouseX, mouseY, tickDelta) -> {
                for (var instance : getInstances(screen)) {
                    if (instance.aggressivePositioning) instance.dispatchLayoutUpdates();
                }
            });

            ScreenEvents.afterRender(screeen).register((screen, context, mouseX, mouseY, tickDelta) -> {
//                context.draw();
                for (var instance : getInstances(screen)) {
                    instance.adapter.method_25394(context, mouseX, mouseY, tickDelta);
                }

                for (var instance : getInstances(screen)) {
                    instance.adapter.drawTooltip(context, mouseX, mouseY, tickDelta);
                }
            });

            ScreenMouseEvents.allowMouseClick(screeen).register((screen, click) -> {
                boolean handled;
                for (var instance : getInstances(screen)) {
                    handled = instance.adapter.method_25402(click, false);
                    if (handled) return false;
                }

                return true;
            });

            ScreenMouseEvents.allowMouseRelease(screeen).register((screen, click) -> {
                boolean handled;
                for (var instance : getInstances(screen)) {
                    handled = instance.adapter.method_25406(click);
                    if (handled) return false;
                }

                return true;
            });

            ScreenMouseEvents.allowMouseScroll(screeen).register((screen, mouseX, mouseY, horizontalAmount, verticalAmount) -> {
                boolean handled;
                for (var instance : getInstances(screen)) {
                    handled = instance.adapter.method_25401(mouseX, mouseY, horizontalAmount, verticalAmount);
                    if (handled) return false;
                }

                return true;
            });

            ScreenKeyboardEvents.allowKeyPress(screeen).register((screen, keyInput) -> {
                boolean handled;
                for (var instance : getInstances(screen)) {
                    handled = instance.adapter.method_25404(keyInput);
                    if (handled) return false;
                }

                return true;
            });

            ScreenKeyboardEvents.allowKeyRelease(screeen).register((screen, keyInput) -> {
                boolean handled;
                for (var instance : getInstances(screen)) {
                    handled = instance.adapter.method_16803(keyInput);
                    if (handled) return false;
                }

                return true;
            });
        });
    }

}
