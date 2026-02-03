package io.wispforest.owo.ui.component;

import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.UIComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.class_1058;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1799;
import net.minecraft.class_2343;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_339;
import net.minecraft.class_4730;

// TODO paginated and tabbed containers

/**
 * Utility methods for creating UI components
 */
public final class UIComponents {

    private UIComponents() {}

    // -----------------------
    // Wrapped Vanilla Widgets
    // -----------------------

    public static ButtonComponent button(class_2561 message, Consumer<ButtonComponent> onPress) {
        return new ButtonComponent(message, onPress);
    }

    public static TextBoxComponent textBox(Sizing horizontalSizing) {
        return new TextBoxComponent(horizontalSizing);
    }

    public static TextBoxComponent textBox(Sizing horizontalSizing, String text) {
        var textBox = new TextBoxComponent(horizontalSizing);
        textBox.text(text);
        return textBox;
    }

    public static TextAreaComponent textArea(Sizing horizontalSizing, Sizing verticalSizing) {
        return new TextAreaComponent(horizontalSizing, verticalSizing);
    }

    public static TextAreaComponent textArea(Sizing horizontalSizing, Sizing verticalSizing, String text) {
        var textArea = new TextAreaComponent(horizontalSizing, verticalSizing);
        textArea.method_44400(text);
        return textArea;
    }

    // ------------------
    // Default Components
    // ------------------

    public static <E extends class_1297> EntityComponent<E> entity(Sizing sizing, class_1299<E> type, @Nullable class_2487 nbt) {
        return new EntityComponent<>(sizing, type, nbt);
    }

    public static <E extends class_1297> EntityComponent<E> entity(Sizing sizing, E entity) {
        return new EntityComponent<>(sizing, entity);
    }

    public static ItemComponent item(class_1799 item) {
        return new ItemComponent(item);
    }

    public static BlockComponent block(class_2680 state) {
        return new BlockComponent(state, null);
    }

    public static BlockComponent block(class_2680 state, class_2586 blockEntity) {
        return new BlockComponent(state, blockEntity);
    }

    public static BlockComponent block(class_2680 state, @Nullable class_2487 nbt) {
        final var client = class_310.method_1551();

        class_2586 blockEntity = null;

        if (state.method_26204() instanceof class_2343 provider) {
            blockEntity = provider.method_10123(client.field_1724.method_24515(), state);
            BlockComponent.prepareBlockEntity(state, blockEntity, nbt);
        }

        return new BlockComponent(state, blockEntity);
    }

    public static LabelComponent label(net.minecraft.class_2561 text) {
        return new LabelComponent(text);
    }

    public static CheckboxComponent checkbox(net.minecraft.class_2561 message) {
        return new CheckboxComponent(message);
    }

    public static SliderComponent slider(Sizing horizontalSizing) {
        return new SliderComponent(horizontalSizing);
    }

    public static DiscreteSliderComponent discreteSlider(Sizing horizontalSizing, double min, double max) {
        return new DiscreteSliderComponent(horizontalSizing, min, max);
    }

    public static SpriteComponent sprite(class_4730 spriteId) {
        return new SpriteComponent(class_310.method_1551().method_72703().method_73030(spriteId));
    }

    public static SpriteComponent sprite(class_1058 sprite) {
        return new SpriteComponent(sprite);
    }

    public static TextureComponent texture(class_2960 texture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        return new TextureComponent(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    public static TextureComponent texture(class_2960 texture, int u, int v, int regionWidth, int regionHeight) {
        return new TextureComponent(texture, u, v, regionWidth, regionHeight, 256, 256);
    }

    public static BoxComponent box(Sizing horizontalSizing, Sizing verticalSizing) {
        return new BoxComponent(horizontalSizing, verticalSizing);
    }

    public static DropdownComponent dropdown(Sizing horizontalSizing) {
        return new DropdownComponent(horizontalSizing);
    }

    public static SlimSliderComponent slimSlider(SlimSliderComponent.Axis axis) {
        return new SlimSliderComponent(axis);
    }

    public static SmallCheckboxComponent smallCheckbox(class_2561 label) {
        return new SmallCheckboxComponent(label);
    }

    public static SpacerComponent spacer(int percent) {
        return new SpacerComponent(percent);
    }

    public static SpacerComponent spacer() {
        return spacer(100);
    }

    // -------
    // Utility
    // -------

    public static <T, C extends UIComponent> FlowLayout list(List<T> data, Consumer<FlowLayout> layoutConfigurator, Function<T, C> componentMaker, boolean vertical) {
        var layout = vertical ? UIContainers.verticalFlow(Sizing.content(), Sizing.content()) : UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
        layoutConfigurator.accept(layout);

        for (var value : data) {
            layout.child(componentMaker.apply(value));
        }

        return layout;
    }

    public static VanillaWidgetComponent wrapVanillaWidget(class_339 widget) {
        return new VanillaWidgetComponent(widget);
    }

    public static <T extends UIComponent> T createWithSizing(Supplier<T> componentMaker, Sizing horizontalSizing, Sizing verticalSizing) {
        var component = componentMaker.get();
        component.sizing(horizontalSizing, verticalSizing);
        return component;
    }

}
