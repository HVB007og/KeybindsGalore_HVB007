package io.wispforest.owo.mixin.registry;

import com.mojang.serialization.Lifecycle;
import io.wispforest.owo.util.OwoFreezer;
import io.wispforest.owo.util.pond.OwoSimpleRegistryExtensions;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.class_2370;
import net.minecraft.class_2385;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_9248;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mixin(class_2370.class)
public abstract class MappedRegistryMixin<T> implements class_2385<T>, OwoSimpleRegistryExtensions<T> {

    @Shadow private Map<T, class_6880.class_6883<T>> unregisteredIntrusiveHolders;
    @Shadow @Final private Map<class_5321<T>, class_6880.class_6883<T>> byKey;
    @Shadow @Final private Map<class_2960, class_6880.class_6883<T>> byLocation;
    @Shadow @Final private Map<T, class_6880.class_6883<T>> byValue;
    @Shadow @Final private ObjectList<class_6880.class_6883<T>> byId;
    @Shadow @Final private Reference2IntMap<T> toId;
    @Shadow @Final private Map<class_5321<T>, class_9248> registrationInfos;
    @Shadow private Lifecycle registryLifecycle;

    //--

    /**
     * Copy of the {@link class_2370#method_10272} function but uses {@link List#set} instead of {@link List#add} for {@link class_2370#method_10200}
     */
    public class_6880.class_6883<T> owo$set(int id, class_5321<T> arg, T object, class_9248 arg2) {
        this.byValue.remove(object);

        OwoFreezer.checkRegister("Registry Set Calls"); //this.assertNotFrozen(arg);

        Objects.requireNonNull(arg);
        Objects.requireNonNull(object);

        class_6880.class_6883<T> reference;

        if (this.unregisteredIntrusiveHolders != null) {
            reference = this.unregisteredIntrusiveHolders.remove(object);

            if (reference == null) {
                throw new AssertionError("Missing intrusive holder for " + arg + ":" + object);
            }

            ((ReferenceAccessor<T>) reference).owo$setRegistryKey(arg);
        } else {
            reference = this.byKey.computeIfAbsent(arg, k -> class_6880.class_6883.method_40234(this, k));
            ((ReferenceAccessor<T>) reference).owo$setValue((T)object);
        }

        this.byKey.put(arg, reference);
        this.byLocation.put(arg.method_29177(), reference);
        this.byValue.put(object, reference);
        this.byId.set(id, reference);
        this.toId.put(object, id);
        this.registrationInfos.put(arg, arg2);
        this.registryLifecycle = this.registryLifecycle.add(arg2.comp_2355());

        // TODO: SHOULD WE BE REFIREING THE EVENT?
        RegistryEntryAddedCallback.event(this).invoker().onEntryAdded(id, arg.method_29177(), (T)object);

        return reference;
    }
}
