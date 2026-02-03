package io.wispforest.owo.ui.component;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.Owo;
import io.wispforest.owo.mixin.ui.access.BlockEntityAccessor;
import io.wispforest.owo.ui.base.BaseUIComponent;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.renderstate.BlockElementRenderState;
import net.minecraft.class_11352;
import net.minecraft.class_11954;
import net.minecraft.class_2259;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_7923;
import net.minecraft.class_8030;
import net.minecraft.class_8942;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

public class BlockComponent extends BaseUIComponent {

    private final class_2680 state;
    private final @Nullable class_2586 entity;

    protected BlockComponent(class_2680 state, @Nullable class_2586 entity) {
        this.state = state;
        this.entity = entity;
    }

    @Override
    public void draw(OwoUIGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        class_11954 entity = null;
        if (this.entity != null) {
            var renderer = class_310.method_1551().method_31975().method_3550(this.entity);
            if (renderer != null) {
                entity = renderer.method_74335();

                renderer.method_74331(
                    this.entity, entity, partialTicks, class_243.field_1353, null
                );
            }
        }

        graphics.field_59826.method_70922(new BlockElementRenderState(
            this.state,
            entity,
            new class_8030(this.x, this.y, this.width, this.height),
            graphics.field_44659.method_70863()
        ));
    }

    protected static void prepareBlockEntity(class_2680 state, class_2586 blockEntity, @Nullable class_2487 nbt) {
        if (blockEntity == null) return;

        var world = class_310.method_1551().field_1687;

        ((BlockEntityAccessor) blockEntity).owo$setBlockState(state);
        blockEntity.method_31662(world);

        if (nbt == null) return;

        final var nbtCopy = nbt.method_10553();

        nbtCopy.method_10569("x", 0);
        nbtCopy.method_10569("y", 0);
        nbtCopy.method_10569("z", 0);

        blockEntity.method_58690(class_11352.method_71417(new class_8942.class_11340(Owo.LOGGER), world.method_30349(), nbtCopy));
    }

    public static BlockComponent parse(Element element) {
        UIParsing.expectAttributes(element, "state");

        try {
            var result = class_2259.method_41957(class_7923.field_41175, element.getAttribute("state"), true);
            return UIComponents.block(result.comp_622(), result.comp_624());
        } catch (CommandSyntaxException cse) {
            throw new UIModelParsingException("Invalid block state", cse);
        }
    }
}
