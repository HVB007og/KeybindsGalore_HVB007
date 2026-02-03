package io.wispforest.owo.client.screens;

import io.wispforest.owo.mixin.AbstractContainerMenuInvoker;
import net.minecraft.class_1657;
import net.minecraft.class_1703;
import net.minecraft.class_1799;

/**
 * A collection of utilities to ease implementing a simple {@link net.minecraft.class_465}
 */
public class MenuUtils {

    /**
     * Can be used as an implementation of {@link net.minecraft.class_1703#method_7601(class_1657, int)}
     * for simple screens with a lower (player) and upper (main) inventory
     *
     * <pre>
     * {@code
     * @Override
     * public ItemStack quickMove(PlayerEntity player, int invSlot) {
     *     return MenuUtils.handleSlotTransfer(this, invSlot, this.inventory.size());
     * }
     * }
     * </pre>
     *
     * @param menu               The target AbstractContainerMenu
     * @param clickedSlotIndex   The slot index that was clicked
     * @param upperInventorySize The size of the upper (main) inventory
     * @return The return value for {{@link net.minecraft.class_1703#method_7601(class_1657, int)}}
     */
    public static class_1799 handleSlotTransfer(class_1703 menu, int clickedSlotIndex, int upperInventorySize) {
        final var slots = menu.field_7761;
        final var clickedSlot = slots.get(clickedSlotIndex);
        if (!clickedSlot.method_7681()) return class_1799.field_8037;

        final var clickedStack = clickedSlot.method_7677();

        if (clickedSlotIndex < upperInventorySize) {
            if (!insertIntoSlotRange(menu, clickedStack, upperInventorySize, slots.size(), true)) {
                return class_1799.field_8037;
            }
        } else {
            if (!insertIntoSlotRange(menu, clickedStack, 0, upperInventorySize)) {
                return class_1799.field_8037;
            }
        }

        if (clickedStack.method_7960()) {
            clickedSlot.method_53512(class_1799.field_8037);
        } else {
            clickedSlot.method_7668();
        }

        return clickedStack;
    }

    /**
     * Shorthand of {@link #insertIntoSlotRange(class_1703, class_1799, int, int, boolean)} with
     * {@code false} for {@code fromLast}
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean insertIntoSlotRange(class_1703 menu, class_1799 addition, int beginIndex, int endIndex) {
        return insertIntoSlotRange(menu, addition, beginIndex, endIndex, false);
    }

    /**
     * Tries to insert the {@code addition} stack into all slots in the given range
     *
     * @param menu       The AbstractContainerMenu to operate on
     * @param beginIndex The index of the first slot to check
     * @param endIndex   The index of the last slot to check
     * @param addition   The ItemStack to try and insert, this gets mutated
     *                   if insertion (partly) succeeds
     * @param fromLast   If {@code true}, iterate the range of slots in
     *                   opposite order
     * @return {@code true} if state was modified
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean insertIntoSlotRange(class_1703 menu, class_1799 addition, int beginIndex, int endIndex, boolean fromLast) {
        return ((AbstractContainerMenuInvoker) menu).owo$insertItem(addition, beginIndex, endIndex, fromLast);
    }

}
