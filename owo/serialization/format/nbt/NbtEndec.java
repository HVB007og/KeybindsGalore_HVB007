package io.wispforest.owo.serialization.format.nbt;

import com.google.common.io.ByteStreams;
import io.wispforest.endec.*;
import java.io.IOException;
import net.minecraft.class_2487;
import net.minecraft.class_2505;
import net.minecraft.class_2507;
import net.minecraft.class_2520;

public final class NbtEndec implements Endec<class_2520> {

    public static final Endec<class_2520> ELEMENT = new NbtEndec();
    public static final Endec<class_2487> COMPOUND = new NbtEndec().xmap(class_2487.class::cast, compound -> compound);

    private NbtEndec() {}

    @Override
    public void encode(SerializationContext ctx, Serializer<?> serializer, class_2520 value) {
        if (serializer instanceof SelfDescribedSerializer<?>) {
            NbtDeserializer.of(value).readAny(ctx, serializer);
            return;
        }

        try {
            var output = ByteStreams.newDataOutput();
            class_2507.method_52893(value, output);

            serializer.writeBytes(ctx, output.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to encode binary NBT in NbtEndec", e);
        }
    }

    @Override
    public class_2520 decode(SerializationContext ctx, Deserializer<?> deserializer) {
        if (deserializer instanceof SelfDescribedDeserializer<?> selfDescribedDeserializer) {
            var nbt = NbtSerializer.of();
            selfDescribedDeserializer.readAny(ctx, nbt);

            return nbt.result();
        }

        try {
            return class_2507.method_52894(ByteStreams.newDataInput(deserializer.readBytes(ctx)), class_2505.method_53898());
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse binary NBT in NbtEndec", e);
        }
    }
}
