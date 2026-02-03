package io.wispforest.owo.client.screens;

import java.util.function.Predicate;
import net.minecraft.class_1263;
import net.minecraft.class_1735;
import net.minecraft.class_1799;

/**
 * A slot that uses the provided {@code insertCondition}
 * to decide which items can be inserted
 */
public class ValidatingSlot extends class_1735 {

    private final Predicate<class_1799> insertCondition;

    public ValidatingSlot(class_1263 container, int index, int x, int y, Predicate<class_1799> insertCondition) {
        super(container, index, x, y);
        this.insertCondition = insertCondition;
    }

    @Override
    public boolean method_7680(class_1799 stack) {
        return insertCondition.test(stack);
    }

}
