package io.wispforest.owo.mixin.itemgroup;

import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.util.pond.OwoItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;
import net.minecraft.class_1761;
import net.minecraft.class_1792;

@Mixin(class_1792.class)
public class ItemMixin implements OwoItemExtensions {

    @Nullable
    protected class_1761 owo$group = null;

    @Unique
    private int owo$tab = 0;

    @Unique
    private BiConsumer<class_1792, class_1761.class_7704> owo$stackGenerator;

    @Unique
    private boolean owo$trackUsageStat = false;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void grabTab(class_1792.class_1793 settings, CallbackInfo ci) {
        this.owo$tab = settings.tab();
        this.owo$stackGenerator = settings.stackGenerator();
        this.owo$group = settings.group();
        this.owo$trackUsageStat = settings.shouldTrackUsageStat();
    }

    @Override
    public int owo$tab() {
        return owo$tab;
    }

    @Override
    public BiConsumer<class_1792, class_1761.class_7704> owo$stackGenerator() {
        return this.owo$stackGenerator != null ? this.owo$stackGenerator : OwoItemGroup.DEFAULT_STACK_GENERATOR;
    }

    @Override
    public void owo$setGroup(class_1761 group) {
        this.owo$group = group;
    }

    @Override
    public @Nullable class_1761 owo$group() {
        return this.owo$group;
    }

    @Override
    public boolean owo$shouldTrackUsageStat() {
        return this.owo$trackUsageStat;
    }
}
