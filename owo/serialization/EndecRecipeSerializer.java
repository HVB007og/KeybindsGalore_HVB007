package io.wispforest.owo.serialization;

import com.mojang.serialization.MapCodec;
import io.wispforest.endec.Endec;
import io.wispforest.endec.SerializationAttributes;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.StructEndec;
import net.minecraft.class_1860;
import net.minecraft.class_1865;
import net.minecraft.class_2540;
import net.minecraft.class_9129;
import net.minecraft.class_9139;

public class EndecRecipeSerializer<R extends class_1860<?>> implements class_1865<R> {

    private final class_9139<class_2540, R> packetCodec;
    private final MapCodec<R> codec;

    public EndecRecipeSerializer(StructEndec<R> endec, Endec<R> networkEndec) {
        this.packetCodec = CodecUtils.toPacketCodec(networkEndec);
        this.codec = CodecUtils.toMapCodec(endec, SerializationContext.attributes(SerializationAttributes.HUMAN_READABLE));
    }

    public EndecRecipeSerializer(StructEndec<R> endec) {
        this(endec, endec);
    }

    @Override
    public MapCodec<R> method_53736() {
        return this.codec;
    }

    @Override
    public class_9139<class_9129, R> method_56104() {
        return this.packetCodec.method_56430();
    }
}
