package io.wispforest.owo.serialization.format.nbt;

import com.google.common.collect.MapMaker;
import io.wispforest.endec.*;
import io.wispforest.endec.util.RecursiveDeserializer;
import net.minecraft.class_2479;
import net.minecraft.class_2481;
import net.minecraft.class_2483;
import net.minecraft.class_2487;
import net.minecraft.class_2489;
import net.minecraft.class_2494;
import net.minecraft.class_2497;
import net.minecraft.class_2503;
import net.minecraft.class_2514;
import net.minecraft.class_2516;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class NbtDeserializer extends RecursiveDeserializer<class_2520> implements SelfDescribedDeserializer<class_2520> {

    protected NbtDeserializer(class_2520 element) {
        super(element);
    }

    public static NbtDeserializer of(class_2520 element) {
        return new NbtDeserializer(element);
    }

    private <N extends class_2520> N getAs(SerializationContext ctx, class_2520 element, Class<N> clazz) {
        if (!clazz.isInstance(element)) {
            ctx.throwMalformedInput("Expected a " + clazz.getSimpleName() + ", found a " + element.getClass().getSimpleName());
        }

        return clazz.cast(element);
    }

    // ---

    @Override
    public byte readByte(SerializationContext ctx) {
        return this.getAs(ctx, this.getValue(), class_2481.class).method_10698();
    }

    @Override
    public short readShort(SerializationContext ctx) {
        return this.getAs(ctx, this.getValue(), class_2516.class).method_10696();
    }

    @Override
    public int readInt(SerializationContext ctx) {
        return this.getAs(ctx, this.getValue(), class_2497.class).method_10701();
    }

    @Override
    public long readLong(SerializationContext ctx) {
        return this.getAs(ctx, this.getValue(), class_2503.class).method_10699();
    }

    @Override
    public float readFloat(SerializationContext ctx) {
        return this.getAs(ctx, this.getValue(), class_2494.class).method_10700();
    }

    @Override
    public double readDouble(SerializationContext ctx) {
        return this.getAs(ctx, this.getValue(), class_2489.class).method_10697();
    }

    // ---

    @Override
    public int readVarInt(SerializationContext ctx) {
        return this.getAs(ctx, this.getValue(), class_2514.class).method_10701();
    }

    @Override
    public long readVarLong(SerializationContext ctx) {
        return this.getAs(ctx, this.getValue(), class_2514.class).method_10699();
    }

    // ---

    @Override
    public boolean readBoolean(SerializationContext ctx) {
        return this.getAs(ctx, this.getValue(), class_2481.class).method_10698() != 0;
    }

    @Override
    public String readString(SerializationContext ctx) {
        return this.getAs(ctx, this.getValue(), class_2519.class).method_68658().get();
    }

    @Override
    public byte[] readBytes(SerializationContext ctx) {
        return this.getAs(ctx, this.getValue(), class_2479.class).method_10521();
    }

    private final Set<class_2520> encodedOptionals = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());

    @Override
    public <V> Optional<V> readOptional(SerializationContext ctx, Endec<V> endec) {
        var value = this.getValue();
        if (this.encodedOptionals.contains(value)) {
            return Optional.of(endec.decode(ctx, this));
        }

        var struct = this.struct(ctx);
        return struct.field("present", ctx, Endec.BOOLEAN)
                ? Optional.of(struct.field("value", ctx, endec))
                : Optional.empty();
    }

    // ---

    @Override
    public <E> Deserializer.Sequence<E> sequence(SerializationContext ctx, Endec<E> elementEndec) {
        //noinspection unchecked
        var list = this.getAs(ctx, this.getValue(), class_2483.class);
        return new io.wispforest.owo.serialization.format.nbt.NbtDeserializer.Sequence<E>(ctx, elementEndec, list, list.size());
    }

    @Override
    public <V> Deserializer.Map<V> map(SerializationContext ctx, Endec<V> valueEndec) {
        return new io.wispforest.owo.serialization.format.nbt.NbtDeserializer.Map<>(ctx, valueEndec, this.getAs(ctx, this.getValue(), class_2487.class));
    }

    @Override
    public Deserializer.Struct struct(SerializationContext ctx) {
        return new io.wispforest.owo.serialization.format.nbt.NbtDeserializer.Struct(this.getAs(ctx, this.getValue(), class_2487.class));
    }

    // ---

    @Override
    public <S> void readAny(SerializationContext ctx, Serializer<S> visitor) {
        this.decodeValue(ctx, visitor, this.getValue());
    }

    private <S> void decodeValue(SerializationContext ctx, Serializer<S> visitor, class_2520 value) {
        switch (value.method_10711()) {
            case class_2520.field_33251 -> visitor.writeByte(ctx, ((class_2481) value).method_10698());
            case class_2520.field_33252 -> visitor.writeShort(ctx, ((class_2516) value).method_10696());
            case class_2520.field_33253 -> visitor.writeInt(ctx, ((class_2497) value).method_10701());
            case class_2520.field_33254 -> visitor.writeLong(ctx, ((class_2503) value).method_10699());
            case class_2520.field_33255 -> visitor.writeFloat(ctx, ((class_2494) value).method_10700());
            case class_2520.field_33256 -> visitor.writeDouble(ctx, ((class_2489) value).method_10697());
            case class_2520.field_33258 -> visitor.writeString(ctx, value.method_68658().get());
            case class_2520.field_33257 -> visitor.writeBytes(ctx, ((class_2479) value).method_10521());
            case class_2520.field_33261, class_2520.field_33262, class_2520.field_33259 -> {
                var list = (class_2483) value;
                try (var sequence = visitor.sequence(ctx, Endec.<class_2520>of(this::decodeValue, (ctx1, deserializer) -> null), list.size())) {
                    list.forEach(sequence::element);
                }
            }
            case class_2520.field_33260 -> {
                var compound = (class_2487) value;
                try (var map = visitor.map(ctx, Endec.<class_2520>of(this::decodeValue, (ctx1, deserializer) -> null), compound.method_10546())) {
                    for (var key : compound.method_10541()) {
                        map.entry(key, compound.method_10580(key));
                    }
                }
            }
            default ->
                    throw new IllegalArgumentException("Non-standard, unrecognized NbtElement implementation cannot be decoded");
        }
    }

    // ---

    private class Sequence<V> implements Deserializer.Sequence<V> {

        private final SerializationContext ctx;
        private final Endec<V> valueEndec;
        private final Iterator<class_2520> elements;
        private final int size;

        private Sequence(SerializationContext ctx, Endec<V> valueEndec, Iterable<class_2520> elements, int size) {
            this.ctx = ctx;
            this.valueEndec = valueEndec;

            this.elements = elements.iterator();
            this.size = size;
        }

        @Override
        public int estimatedSize() {
            return this.size;
        }

        @Override
        public boolean hasNext() {
            return this.elements.hasNext();
        }

        @Override
        public V next() {
            var value = this.elements.next();

            return NbtDeserializer.this.frame(
                    () -> value,
                    () -> this.valueEndec.decode(this.ctx, NbtDeserializer.this)
            );
        }
    }

    private class Map<V> implements Deserializer.Map<V> {

        private final SerializationContext ctx;
        private final Endec<V> valueEndec;
        private final class_2487 compound;
        private final Iterator<String> keys;
        private final int size;

        private Map(SerializationContext ctx, Endec<V> valueEndec, class_2487 compound) {
            this.ctx = ctx;
            this.valueEndec = valueEndec;

            this.compound = compound;
            this.keys = compound.method_10541().iterator();
            this.size = compound.method_10546();
        }

        @Override
        public int estimatedSize() {
            return this.size;
        }

        @Override
        public boolean hasNext() {
            return this.keys.hasNext();
        }

        @Override
        public java.util.Map.Entry<String, V> next() {
            var key = this.keys.next();
            return NbtDeserializer.this.frame(
                    () -> this.compound.method_10580(key),
                    () -> java.util.Map.entry(key, this.valueEndec.decode(this.ctx, NbtDeserializer.this))
            );
        }
    }

    public class Struct implements Deserializer.Struct {

        private final class_2487 compound;

        public Struct(class_2487 compound) {
            this.compound = compound;
        }

        @Override
        public <F> @Nullable F field(String name, SerializationContext ctx, Endec<F> endec, @Nullable Supplier<F> defaultValueFactory) {
            if (!this.compound.method_10545(name)) {
                if (defaultValueFactory == null) {
                    throw new IllegalStateException("Field '" + name + "' was missing from serialized data, but no default value was provided");
                }

                return defaultValueFactory.get();
            }
            var element = this.compound.method_10580(name);
            if (defaultValueFactory != null) NbtDeserializer.this.encodedOptionals.add(element);
            return NbtDeserializer.this.frame(
                    () -> element,
                    () -> endec.decode(ctx, NbtDeserializer.this)
            );
        }
    }
}
