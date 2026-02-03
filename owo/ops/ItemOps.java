package io.wispforest.owo.ops;

import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1799;

/**
 * A collection of common checks and operations done on {@link class_1799}
 */
public final class ItemOps {

    private ItemOps() {
    }

    /**
     * Checks if stack one can stack onto stack two
     *
     * @param base     The base stack
     * @param addition The stack to be added
     * @return {@code true} if addition can stack onto base
     */
    public static boolean canStack(class_1799 base, class_1799 addition) {
        return base.method_7960() || (canIncreaseBy(base, addition.method_7947()) && class_1799.method_31577(base, addition));
    }

    /**
     * Checks if a stack can increase
     *
     * @param stack The stack to test
     * @return stack.getCount() &lt; stack.getMaxCount()
     */
    public static boolean canIncrease(class_1799 stack) {
        return stack.method_7946() && stack.method_7947() < stack.method_7914();
    }

    /**
     * Checks if a stack can increase by the given amount
     *
     * @param stack The stack to test
     * @param by    The amount to test for
     * @return {@code true} if the stack can increase by the given amount
     */
    public static boolean canIncreaseBy(class_1799 stack, int by) {
        return stack.method_7946() && stack.method_7947() + by <= stack.method_7914();
    }

    /**
     * Returns a copy of the given stack with count set to 1
     */
    public static class_1799 singleCopy(class_1799 stack) {
        class_1799 copy = stack.method_7972();
        copy.method_7939(1);
        return copy;
    }

    /**
     * Decrements the stack
     *
     * @param stack The stack to decrement
     * @return {@code false} if the stack is empty after the operation
     */
    public static boolean emptyAwareDecrement(class_1799 stack) {
        return emptyAwareDecrement(stack, 1);
    }

    /**
     * Decrements the stack
     *
     * @param stack  The stack to decrement
     * @param amount The amount to decrement
     * @return {@code false} if the stack is empty after the operation
     */
    public static boolean emptyAwareDecrement(class_1799 stack, int amount) {
        stack.method_7934(amount);
        return !stack.method_7960();
    }

    /**
     * Decrements the stack in the players hand and replaces it with {@link class_1799#field_8037}
     * if the result would be an empty stack
     *
     * @param player The player to operate on
     * @param hand   The hand to affect
     * @return {@code false} if the stack is empty after the operation
     */
    public static boolean decrementPlayerHandItem(class_1657 player, class_1268 hand) {
        return decrementPlayerHandItem(player, hand, 1);
    }

    /**
     * Decrements the stack in the players hand and replaces it with {@link class_1799#field_8037}
     * if the result would be an empty stack
     *
     * @param player The player to operate on
     * @param hand   The hand to affect
     * @param amount The amount to decrement
     * @return {@code false} if the stack is empty after the operation
     */
    public static boolean decrementPlayerHandItem(class_1657 player, class_1268 hand, int amount) {
        var stack = player.method_5998(hand);
        if (!player.method_68878()) {
            if (!emptyAwareDecrement(stack, amount)) player.method_6122(hand, class_1799.field_8037);
        }
        return !stack.method_7960();
    }
}
