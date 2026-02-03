package io.wispforest.owo.braid.widgets.object;

import io.wispforest.owo.Owo;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.mixin.ui.access.BlockEntityAccessor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.class_11352;
import net.minecraft.class_2343;
import net.minecraft.class_2487;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_8942;

public class BlockWidget extends StatefulWidget {

    public final class_2680 blockState;
    public final @Nullable class_2586 blockEntity;
    public final @Nullable class_2487 blockEntityNbt;
    public final @Nullable Consumer<Matrix4f> transform;

    private BlockWidget(class_2680 blockState, @Nullable class_2586 blockEntity, @Nullable class_2487 blockEntityNbt, @Nullable Consumer<Matrix4f> transform) {
        this.blockState = blockState;
        this.blockEntity = blockEntity;
        this.blockEntityNbt = blockEntityNbt;
        this.transform = transform;
    }

    public BlockWidget(class_2680 blockState, @Nullable class_2586 blockEntity) {
        this(blockState, blockEntity, null, null);
    }

    public BlockWidget(class_2680 blockState, @Nullable class_2586 blockEntity, Consumer<Matrix4f> transform) {
        this(blockState, blockEntity, null, transform);
    }

    public BlockWidget(class_2680 blockState, @Nullable class_2487 blockEntityNbt) {
        this(blockState, null, blockEntityNbt, null);
    }

    public BlockWidget(class_2680 blockState, @Nullable class_2487 blockEntityNbt, Consumer<Matrix4f> transform) {
        this(blockState, null, blockEntityNbt, transform);
    }

    public BlockWidget(class_2680 blockState, Consumer<Matrix4f> transform) {
        this(blockState, null, null, transform);
    }

    public BlockWidget(class_2680 blockState) {
        this(blockState, null, null, null);
    }

    @Override
    public WidgetState<BlockWidget> createState() {
        return new State();
    }

    public static class State extends WidgetState<BlockWidget> {

        private @Nullable class_2586 internalBlockEntity;

        @Override
        public void init() {
            this.resetBlockEntity();
        }

        @Override
        public void didUpdateWidget(BlockWidget oldWidget) {
            if (this.widget().blockState == oldWidget.blockState
                && this.widget().blockEntity == oldWidget.blockEntity
                && Objects.equals(this.widget().blockEntityNbt, oldWidget.blockEntityNbt)) {
                return;
            }

            this.resetBlockEntity();
        }

        private void resetBlockEntity() {
            this.internalBlockEntity = this.widget().blockEntity == null
                ? prepareBlockEntity(this.widget().blockState, this.widget().blockEntityNbt)
                : null;
        }

        @Override
        public Widget build(BuildContext context) {
            return new RawBlockWidget(
                this.widget().blockState,
                this.internalBlockEntity != null ? this.internalBlockEntity : this.widget().blockEntity,
                this.widget().transform
            );
        }

        // ---

        private static @Nullable class_2586 prepareBlockEntity(class_2680 state, @Nullable class_2487 nbt) {
            var client = class_310.method_1551();
            if (!state.method_31709()) {
                return null;
            }

            var blockEntity = ((class_2343) state.method_26204()).method_10123(client.field_1724.method_24515(), state);
            if (blockEntity == null) {
                return null;
            }

            ((BlockEntityAccessor) blockEntity).owo$setBlockState(state);
            blockEntity.method_31662(client.field_1687);

            if (nbt != null) {
                blockEntity.method_58690(class_11352.method_71417(new class_8942.class_11340(Owo.LOGGER), client.field_1687.method_30349(), nbt));
            }

            return blockEntity;
        }
    }
}
