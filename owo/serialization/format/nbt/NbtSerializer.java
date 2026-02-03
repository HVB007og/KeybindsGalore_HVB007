package io.wispforest.owo.serialization.format.nbt;

import com.google.common.collect.MapMaker;
import io.wispforest.endec.Endec;
import io.wispforest.endec.SelfDescribedSerializer;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.Serializer;
import io.wispforest.endec.util.RecursiveSerializer;
import net.minecraft.class_2479;
import net.minecraft.class_2481;
import net.minecraft.class_2487;
import net.minecraft.class_2489;
import net.minecraft.class_2491;
import net.minecraft.class_2494;
import net.minecraft.class_2497;
import net.minecraft.class_2499;
import net.minecraft.class_2503;
import net.minecraft.class_2516;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_8703;
import net.minecraft.class_8704;
import net.minecraft.nbt.*;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class NbtSerializer extends RecursiveSerializer<class_2520> implements SelfDescribedSerializer<class_2520> {

    protected class_2520 prefix;

    protected NbtSerializer(class_2520 prefix) {
        super(class_2491.field_21033);
        this.prefix = prefix;
    }

    public static NbtSerializer of(class_2520 prefix) {
        return new NbtSerializer(prefix);
    }

    public static NbtSerializer of() {
        return of(null);
    }

    // ---

    @Override
    public void writeByte(SerializationContext ctx, byte value) {
        this.consume(class_2481.method_23233(value));
    }

    @Override
    public void writeShort(SerializationContext ctx, short value) {
        this.consume(class_2516.method_23254(value));
    }

    @Override
    public void writeInt(SerializationContext ctx, int value) {
        this.consume(class_2497.method_23247(value));
    }

    @Override
    public void writeLong(SerializationContext ctx, long value) {
        this.consume(class_2503.method_23251(value));
    }

    @Override
    public void writeFloat(SerializationContext ctx, float value) {
        this.consume(class_2494.method_23244(value));
    }

    @Override
    public void writeDouble(SerializationContext ctx, double value) {
        this.consume(class_2489.method_23241(value));
    }

    // ---

    @Override
    public void writeVarInt(SerializationContext ctx, int value) {
        this.consume(switch (class_8703.method_53015(value)) {
            case 0, 1 -> class_2481.method_23233((byte) value);
            case 2 -> class_2516.method_23254((short) value);
            default -> class_2497.method_23247(value);
        });
    }

    @Override
    public void writeVarLong(SerializationContext ctx, long value) {
        this.consume(switch (class_8704.method_53019(value)) {
            case 0, 1 -> class_2481.method_23233((byte) value);
            case 2 -> class_2516.method_23254((short) value);
            case 3, 4 -> class_2497.method_23247((int) value);
            default -> class_2503.method_23251(value);
        });
    }

    // ---

    @Override
    public void writeBoolean(SerializationContext ctx, boolean value) {
        this.consume(class_2481.method_23234(value));
    }

    @Override
    public void writeString(SerializationContext ctx, String value) {
        this.consume(class_2519.method_23256(value));
    }

    @Override
    public void writeBytes(SerializationContext ctx, byte[] bytes) {
        this.consume(new class_2479(bytes));
    }

    private final Set<class_2520> encodedOptionals = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());

    @Override
    public <V> void writeOptional(SerializationContext ctx, Endec<V> endec, Optional<V> optional) {
        MutableObject<class_2520> frameData = new MutableObject<>();

        this.frame(encoded -> {
            try (var struct = this.struct()) {
                struct.field("present", ctx, Endec.BOOLEAN, optional.isPresent());
                optional.ifPresent(value -> struct.field("value", ctx, endec, value));
            }

            var compound = encoded.require("optional representation");

            encodedOptionals.add(compound);
            frameData.setValue(compound);
        });

        this.consume(frameData.getValue());
    }

    // ---

    @Override
    public <E> Serializer.Sequence<E> sequence(SerializationContext ctx, Endec<E> elementEndec, int size) {
        return new io.wispforest.owo.serialization.format.nbt.NbtSerializer.Sequence<>(ctx, elementEndec);
    }

    @Override
    public <V> Serializer.Map<V> map(SerializationContext ctx, Endec<V> valueEndec, int size) {
        return new io.wispforest.owo.serialization.format.nbt.NbtSerializer.Map<>(ctx, valueEndec);
    }

    @Override
    public Struct struct() {
        return new io.wispforest.owo.serialization.format.nbt.NbtSerializer.Map<>(null, null);
    }

    // ---

    private class Map<V> implements Serializer.Map<V>, Struct {

        private final SerializationContext ctx;
        private final Endec<V> valueEndec;
        private final class_2487 result;

        private Map(SerializationContext ctx, Endec<V> valueEndec) {
            this.ctx = ctx;
            this.valueEndec = valueEndec;

            if (NbtSerializer.this.prefix != null) {
                if (NbtSerializer.this.prefix instanceof class_2487 prefixMap) {
                    this.result = prefixMap;
                } else {
                    throw new IllegalStateException("Incompatible prefix of type " + NbtSerializer.this.prefix.getClass().getSimpleName() + " provided for NBT map/struct");
                }
            } else {
                this.result = new class_2487();
            }
        }

        @Override
        public void entry(String key, V value) {
            NbtSerializer.this.frame(encoded -> {
                this.valueEndec.encode(this.ctx, NbtSerializer.this, value);
                this.result.method_10566(key, encoded.require("map value"));
            });
        }

        @Override
        public <F> Struct field(String name, SerializationContext ctx, Endec<F> endec, F value, boolean mayOmit) {
            NbtSerializer.this.frame(encoded -> {
                endec.encode(ctx, NbtSerializer.this, value);

                var element = encoded.require("struct field");

                if (mayOmit && NbtSerializer.this.encodedOptionals.contains(element)) {
                    var nbtCompound = (class_2487) element;

                    if(!nbtCompound.method_68566("present", false)) return;

                    element = nbtCompound.method_10580("value");
                }

                this.result.method_10566(name, element);
            });

            return this;
        }

        @Override
        public void end() {
            NbtSerializer.this.consume(this.result);
        }
    }

    private class Sequence<V> implements Serializer.Sequence<V> {

        private final SerializationContext ctx;
        private final Endec<V> valueEndec;
        private final class_2499 result;

        private Sequence(SerializationContext ctx, Endec<V> valueEndec) {
            this.ctx = ctx;
            this.valueEndec = valueEndec;

            if (NbtSerializer.this.prefix != null) {
                if (NbtSerializer.this.prefix instanceof class_2499 prefixList) {
                    this.result = prefixList;
                } else {
                    throw new IllegalStateException("Incompatible prefix of type " + NbtSerializer.this.prefix.getClass().getSimpleName() + " provided for NBT sequence");
                }
            } else {
                this.result = new class_2499();
            }
        }

        @Override
        public void element(V element) {
            NbtSerializer.this.frame(encoded -> {
                this.valueEndec.encode(this.ctx, NbtSerializer.this, element);
                this.result.add(encoded.require("sequence element"));
            });
        }

        @Override
        public void end() {
            NbtSerializer.this.consume(this.result);
        }
    }
}
