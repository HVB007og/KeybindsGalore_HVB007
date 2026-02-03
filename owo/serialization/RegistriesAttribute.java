package io.wispforest.owo.serialization;

import io.wispforest.endec.SerializationAttribute;
import io.wispforest.owo.mixin.serialization.CachedRegistryInfoGetterAccessor;
import net.minecraft.class_5455;
import net.minecraft.class_6903;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RegistriesAttribute implements SerializationAttribute.Instance {

    public static final SerializationAttribute.WithValue<RegistriesAttribute> REGISTRIES = SerializationAttribute.withValue("registries");

    private final class_6903.class_7863 infoLookup;
    private final @Nullable class_5455 registryAccess;

    private RegistriesAttribute(class_6903.class_7863 infoLookup, @Nullable class_5455 registryAccess) {
        this.infoLookup = infoLookup;
        this.registryAccess = registryAccess;
    }

    public static RegistriesAttribute of(class_5455 registryAccess) {
        return new RegistriesAttribute(
                new class_6903.class_9683(registryAccess),
                registryAccess
        );
    }

    @ApiStatus.Internal
    public static RegistriesAttribute tryFromCachedInfoGetter(class_6903.class_7863 lookup) {
        return (lookup instanceof class_6903.class_9683 cachedGetter)
                ? fromCachedInfoGetter(cachedGetter)
                : fromInfoGetter(lookup);
    }

    public static RegistriesAttribute fromCachedInfoGetter(class_6903.class_9683 cachedGetter) {
        class_5455 registryAccess = null;

        if(((CachedRegistryInfoGetterAccessor) (Object) cachedGetter).owo$getRegistriesLookup() instanceof class_5455 drm) {
            registryAccess = drm;
        }

        return new RegistriesAttribute(cachedGetter, registryAccess);
    }

    public static RegistriesAttribute fromInfoGetter(class_6903.class_7863 lookup) {
        return new RegistriesAttribute(lookup, null);
    }

    public class_6903.class_7863 infoGetter() {
        return this.infoLookup;
    }

    public boolean hasRegistryAccess() {
        return this.registryAccess != null;
    }

    public @NotNull class_5455 registryAccess() {
        if (!this.hasRegistryAccess()) {
            throw new IllegalStateException("This instance of RegistriesAttribute does not supply RegistryAccess");
        }

        return this.registryAccess;
    }

    @Override
    public SerializationAttribute attribute() {
        return REGISTRIES;
    }

    @Override
    public Object value() {
        return this;
    }
}
