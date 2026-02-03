package io.wispforest.owo.mixin.ui;

import io.wispforest.owo.ui.core.UIComponent;
import io.wispforest.owo.ui.inject.GreedyInputUIComponent;
import net.minecraft.class_2561;
import net.minecraft.class_7528;
import net.minecraft.class_7529;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(class_7529.class)
public abstract class MultiLineEditBoxMixin extends class_7528 implements GreedyInputUIComponent {

    public MultiLineEditBoxMixin(int i, int j, int k, int l, class_2561 text) {
        super(i, j, k, l, text);
    }

    @Override
    public void onFocusGained(UIComponent.FocusSource source) {
        super.onFocusGained(source);
        this.method_25365(true);
    }

}
