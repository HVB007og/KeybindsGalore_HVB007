package io.wispforest.owo.mixin.itemgroup;

import io.wispforest.owo.itemgroup.ItemGroupReference;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.OwoItemSettingsExtension;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.BiConsumer;
import net.minecraft.class_1761;
import net.minecraft.class_1792;

@Mixin(class_1792.class_1793.class)
public class ItemSettingsMixin implements OwoItemSettingsExtension {
    private OwoItemGroup owo$group = null;
    private int owo$tab = 0;
    private BiConsumer<class_1792, class_1761.class_7704> owo$stackGenerator = null;
    private boolean owo$trackUsageStat = false;

    @Override
    public class_1792.class_1793 group(ItemGroupReference ref) {
        this.owo$group = ref.group();
        this.owo$tab = ref.tab();

        return (class_1792.class_1793)(Object) this;
    }

    @Override
    public class_1792.class_1793 group(OwoItemGroup group) {
        this.owo$group = group;

        return (class_1792.class_1793)(Object) this;
    }

    @Override
    public OwoItemGroup group() {
        return owo$group;
    }

    @Override
    public class_1792.class_1793 tab(int tab) {
        this.owo$tab = tab;

        return (class_1792.class_1793)(Object) this;
    }

    @Override
    public int tab() {
        return owo$tab;
    }

    @Override
    public class_1792.class_1793 stackGenerator(BiConsumer<class_1792, class_1761.class_7704> generator) {
        this.owo$stackGenerator = generator;

        return (class_1792.class_1793)(Object) this;
    }

    @Override
    public BiConsumer<class_1792, class_1761.class_7704> stackGenerator() {
        return owo$stackGenerator;
    }

    @Override
    public class_1792.class_1793 trackUsageStat() {
        this.owo$trackUsageStat = true;

        return (class_1792.class_1793)(Object) this;
    }

    @Override
    public boolean shouldTrackUsageStat() {
        return owo$trackUsageStat;
    }
}
