package io.wispforest.owo.ext;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import net.minecraft.class_1799;
import net.minecraft.class_9323;
import net.minecraft.class_9326;
import net.minecraft.class_9331;
import net.minecraft.class_9335;

@ApiStatus.Internal
public class DerivedComponentMap implements class_9323 {
    private final class_9323 base;
    private final class_9335 delegate;

    public DerivedComponentMap(class_9323 base) {
        this.base = base;
        this.delegate = new class_9335(base);
    }

    public static class_9323 reWrapIfNeeded(class_9323 original) {
        if (original instanceof DerivedComponentMap derived) {
            return new DerivedComponentMap(derived.base);
        } else {
            return original;
        }
    }

    public void derive(class_1799 owner) {
        delegate.method_59772(class_9326.field_49588);
        var builder = class_9326.method_57841();
        owner.method_7909().deriveStackComponents(owner.method_57353(), builder);
        delegate.method_59772(builder.method_57852());
    }

    @Nullable
    @Override
    public <T> T method_58694(class_9331<? extends T> type) {
        return delegate.method_58694(type);
    }

    @Override
    public Set<class_9331<?>> method_57831() {
        return delegate.method_57831();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof DerivedComponentMap thatDerived) {
            return Objects.equals(base, thatDerived.base);
        } else if (o instanceof class_9323.class_9324.class_9325 simpleComponentMap) {
            return Objects.equals(base, simpleComponentMap);
        }

        return o == field_49584 && this.base == field_49584;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(base);
    }
}
