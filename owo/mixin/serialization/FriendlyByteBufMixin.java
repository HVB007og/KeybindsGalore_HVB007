package io.wispforest.owo.mixin.serialization;

import io.wispforest.endec.Endec;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.format.bytebuf.ByteBufDeserializer;
import io.wispforest.endec.format.bytebuf.ByteBufSerializer;
import io.wispforest.endec.util.EndecBuffer;
import net.minecraft.class_2540;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings({"DataFlowIssue"})
@Mixin(class_2540.class)
public abstract class FriendlyByteBufMixin implements EndecBuffer {
    @Override
    public <T> void write(SerializationContext ctx, Endec<T> endec, T value) {
        endec.encodeFully(ctx, () -> ByteBufSerializer.of((class_2540) (Object) this), value);
    }

    @Override
    public <T> T read(SerializationContext ctx, Endec<T> endec) {
        return endec.decodeFully(ctx, ByteBufDeserializer::of, (class_2540) (Object) this);
    }
}
