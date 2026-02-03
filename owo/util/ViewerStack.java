package io.wispforest.owo.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_9326;

// TODO: pick better name
public interface ViewerStack {
    long count();

    class_9326 componentChanges();

    record OfItem(ItemVariant item, long count) implements ViewerStack {
        public static final OfItem EMPTY = new OfItem(ItemVariant.of(class_1799.field_8037), 0);

        public static OfItem of(class_1792 item) {
            return new OfItem(ItemVariant.of(item), 1);
        }

        public static OfItem of(class_1799 stack) {
            return new OfItem(ItemVariant.of(stack), stack.method_7947());
        }

        public class_1799 asStack() {
            return item.toStack((int) count);
        }

        @Override
        public class_9326 componentChanges() {
            return item.getComponents();
        }
    }

    record OfFluid(FluidVariant fluid, long count) implements ViewerStack {
        @Override
        public class_9326 componentChanges() {
            return fluid.getComponents();
        }
    }
}
