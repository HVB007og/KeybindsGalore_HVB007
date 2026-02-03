package io.wispforest.owo.ui.component;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.base.BaseUIComponent;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.renderstate.OwoItemElementRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.class_10442;
import net.minecraft.class_10444;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_2291;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_5684;
import net.minecraft.class_7225;
import net.minecraft.class_7923;
import net.minecraft.class_8030;
import net.minecraft.class_811;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ItemComponent extends BaseUIComponent {

    protected final class_10442 itemModelManager;
    protected class_1799 stack;
    protected boolean showOverlay = false;
    protected boolean setTooltipFromStack = false;

    protected ItemComponent(class_1799 stack) {
        this.itemModelManager = class_310.method_1551().method_65386();
        this.stack = stack;
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return 16;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return 16;
    }

    @Override
    public void draw(OwoUIGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        var matrices = graphics.method_51448();
        matrices.pushMatrix();

        // Translate to the root of the component
        matrices.translate(this.x, this.y);

        // Scale according to component size and translate to the center
        matrices.scale(this.width / 16f, this.height / 16f);

        var client = class_310.method_1551();

        if (this.width <= 16 && this.height <= 16) {
            graphics.method_51427(this.stack, 0, 0);
        } else {
            var state = new class_10444();
            this.itemModelManager.method_65596(state, this.stack, class_811.field_4317, class_310.method_1551().field_1687, class_310.method_1551().field_1724, 0);

            graphics.field_59826.method_70922(new OwoItemElementRenderState(
                state,
                new class_8030(this.x, this.y, this.width, this.height),
                graphics.field_44659.method_70863()
            ));
        }

        // Clean up
        matrices.popMatrix();

        if (this.showOverlay) {
            graphics.method_51431(client.field_1772, this.stack, this.x, this.y);
        }
    }

    protected void updateTooltipForStack() {
        if (!this.setTooltipFromStack) return;

        if (!this.stack.method_7960()) {
            class_310 client = class_310.method_1551();
            this.tooltip(tooltipFromItem(this.stack, class_1792.class_9635.method_59528(client.field_1687), client.field_1724, null));
        } else {
            this.tooltip((List<class_5684>) null);
        }
    }

    public ItemComponent setTooltipFromStack(boolean setTooltipFromStack) {
        this.setTooltipFromStack = setTooltipFromStack;
        this.updateTooltipForStack();

        return this;
    }

    public boolean setTooltipFromStack() {
        return setTooltipFromStack;
    }

    public ItemComponent stack(class_1799 stack) {
        this.stack = stack;
        this.updateTooltipForStack();

        return this;
    }

    public class_1799 stack() {
        return this.stack;
    }

    public ItemComponent showOverlay(boolean drawOverlay) {
        this.showOverlay = drawOverlay;
        return this;
    }

    public boolean showOverlay() {
        return this.showOverlay;
    }

    /**
     * Obtain the full item stack tooltip, including custom components
     * provided via {@link net.minecraft.class_1792#method_32346(class_1799)}
     *
     * @param stack   The item stack from which to obtain the tooltip
     * @param context the tooltip context
     * @param player  The player to use for context, may be {@code null}
     * @param type    The tooltip type - {@code null} to fall back to the default provided by
     *                {@link net.minecraft.class_315#field_1827}
     */
    public static List<class_5684> tooltipFromItem(class_1799 stack, class_1792.class_9635 context, @Nullable class_1657 player, @Nullable class_1836 type) {
        if (type == null) {
            type = class_310.method_1551().field_1690.field_1827 ? class_1836.field_41071 : class_1836.field_41070;
        }

        var tooltip = new ArrayList<class_5684>();
        stack.method_7950(context, player, type)
            .stream()
            .map(class_2561::method_30937)
            .map(class_5684::method_32662)
            .forEach(tooltip::add);

        stack.method_32347().ifPresent(data -> {
            tooltip.add(1, Objects.requireNonNullElseGet(
                TooltipComponentCallback.EVENT.invoker().getComponent(data),
                () -> class_5684.method_32663(data)
            ));
        });

        return tooltip;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "show-overlay", UIParsing::parseBool, this::showOverlay);
        UIParsing.apply(children, "set-tooltip-from-stack", UIParsing::parseBool, this::setTooltipFromStack);

        UIParsing.apply(children, "item", UIParsing::parseIdentifier, itemId -> {
            Owo.debugWarn(Owo.LOGGER, "Deprecated <item> property populated on item component - migrate to <stack> instead");

            var item = class_7923.field_41178.method_17966(itemId).orElseThrow(() -> new UIModelParsingException("Unknown item " + itemId));
            this.stack(item.method_7854());
        });

        UIParsing.apply(children, "stack", $ -> $.getTextContent().strip(), stackString -> {
            try {
                var result = new class_2291(class_7225.class_7874.method_46761(Stream.of(class_7923.field_41178)))
                    .method_9789(new StringReader(stackString));

                var stack = new class_1799(result.comp_628());
                stack.method_59692(result.comp_2439());

                this.stack(stack);
            } catch (CommandSyntaxException cse) {
                throw new UIModelParsingException("Invalid item stack", cse);
            }
        });
    }
}
