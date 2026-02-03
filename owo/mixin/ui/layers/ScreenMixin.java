package io.wispforest.owo.mixin.ui.layers;

import io.wispforest.owo.ui.core.ParentUIComponent;
import io.wispforest.owo.ui.layers.Layer;
import io.wispforest.owo.ui.layers.Layers;
import io.wispforest.owo.util.pond.OwoScreenExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;
import net.minecraft.class_362;
import net.minecraft.class_437;

@Mixin(value = class_437.class, priority = 1100)
public abstract class ScreenMixin extends class_362 implements OwoScreenExtension {

    @Shadow public int width;
    @Shadow public int height;

    private final List<Layer<?, ?>.Instance> owo$instances = new ArrayList<>();
    private final List<Layer<?, ?>.Instance> owo$instancesView = Collections.unmodifiableList(this.owo$instances);
    private final Map<Layer<?, ?>, Layer<?, ?>.Instance> owo$layersToInstances = new HashMap<>();

    private boolean owo$layersInitialized = false;

    @SuppressWarnings("ConstantConditions")
    private class_437 owo$this() {
        return (class_437) (Object) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void owo$updateLayers() {
        if (this.owo$layersInitialized) {
            for (var instance : this.owo$instances) {
                instance.resize(this.width, this.height);
            }
        } else {
            for (var layer : Layers.getLayers((Class<class_437>) this.owo$this().getClass())) {
                var instance = layer.instantiate(this.owo$this());
                this.owo$instances.add(instance);
                this.owo$layersToInstances.put(layer, instance);

                instance.adapter.inflateAndMount();
            }

            this.owo$layersInitialized = true;
        }

        this.owo$instances.forEach(Layer.Instance::dispatchLayoutUpdates);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S extends class_437, R extends ParentUIComponent> Layer<S, R>.Instance owo$getInstance(Layer<S, R> layer) {
        return (Layer<S, R>.Instance) this.owo$layersToInstances.get(layer);
    }

    @Override
    public List<Layer<?, ?>.Instance> owo$getInstancesView() {
        return this.owo$instancesView;
    }
}
