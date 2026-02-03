package io.wispforest.owo.util;

import net.minecraft.class_1262;
import net.minecraft.class_1263;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2371;

/**
 * A simple {@code Inventory} implementation with only default methods + an item list getter.
 * <p>
 * Originally by Juuz
 */
public interface ImplementedContainer extends class_1263 {

    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    class_2371<class_1799> getItems();

    /**
     * Creates an inventory from the item list.
     */
    static ImplementedContainer of(class_2371<class_1799> items) {
        return () -> items;
    }

    /**
     * Creates a new inventory with the specified size.
     */
    static ImplementedContainer ofSize(int size) {
        return of(class_2371.method_10213(size, class_1799.field_8037));
    }

    /**
     * Returns the inventory size.
     */
    @Override
    default int method_5439() {
        return getItems().size();
    }

    /**
     * Checks if the inventory is empty.
     *
     * @return true if this inventory has only empty stacks, false otherwise.
     */
    @Override
    default boolean method_5442() {
        for (int i = 0; i < method_5439(); i++) {
            class_1799 stack = method_5438(i);
            if (!stack.method_7960()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves the item in the slot.
     */
    @Override
    default class_1799 method_5438(int slot) {
        return getItems().get(slot);
    }

    /**
     * Removes items from an inventory slot.
     *
     * @param slot  The slot to remove from.
     * @param count How many items to remove. If there are fewer items in the slot than what are requested,
     *              takes all items in that slot.
     */
    @Override
    default class_1799 method_5434(int slot, int count) {
        class_1799 result = class_1262.method_5430(getItems(), slot, count);
        if (!result.method_7960()) {
            method_5431();
        }
        return result;
    }

    /**
     * Removes all items from an inventory slot.
     *
     * @param slot The slot to remove from.
     */
    @Override
    default class_1799 method_5441(int slot) {
        return class_1262.method_5428(getItems(), slot);
    }

    /**
     * Replaces the current stack in an inventory slot with the provided stack.
     *
     * @param slot  The inventory slot of which to replace the itemstack.
     * @param stack The replacing itemstack. If the stack is too big for
     *              this inventory ({@link class_1263#method_5444()}),
     *              it gets resized to this inventory's maximum amount.
     */
    @Override
    default void method_5447(int slot, class_1799 stack) {
        getItems().set(slot, stack);
        if (stack.method_7947() > method_5444()) {
            stack.method_7939(method_5444());
        }
    }

    /**
     * Clears the inventory.
     */
    @Override
    default void method_5448() {
        getItems().clear();
    }

    /**
     * Marks the state as dirty.
     * Must be called after changes in the inventory, so that the game can properly save
     * the inventory contents and notify neighboring blocks of inventory changes.
     */
    @Override
    default void method_5431() {
        // Override if you want behavior.
    }

    /**
     * @return true if the player can use the inventory, false otherwise.
     */
    @Override
    default boolean method_5443(class_1657 player) {
        return true;
    }
}