package io.wispforest.owo.serialization;

import io.wispforest.endec.Endec;
import io.wispforest.endec.SerializationContext;
import net.minecraft.class_9331;

public interface OwoDataComponentTypeBuilder<T> {
    default class_9331.class_9332<T> endec(Endec<T> endec) {
        return this.endec(endec, SerializationContext.empty());
    }

    default class_9331.class_9332<T> endec(Endec<T> endec, SerializationContext assumedContext) {
        return ((class_9331.class_9332<T>) this)
            .method_57881(CodecUtils.toCodec(endec, assumedContext))
            .method_57882(CodecUtils.toPacketCodec(endec));
    }
}
