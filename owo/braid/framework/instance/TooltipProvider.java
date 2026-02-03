package io.wispforest.owo.braid.framework.instance;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.class_2583;
import net.minecraft.class_5684;

public interface TooltipProvider {
    @Nullable List<class_5684> getTooltipComponentsAt(double x, double y);

    @Nullable
    default class_2583 getStyleAt(double x, double y) {
        return null;
    }
}
