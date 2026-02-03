package io.wispforest.owo.serialization.endec;

import com.mojang.datafixers.util.Function3;
import io.wispforest.endec.Endec;
import io.wispforest.endec.SerializationAttributes;
import io.wispforest.endec.impl.ReflectiveEndecBuilder;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.class_1799;
import net.minecraft.class_1923;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2378;
import net.minecraft.class_2382;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3965;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import net.minecraft.class_8824;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Function;

public final class MinecraftEndecs {

    private MinecraftEndecs() {}

    // --- MC Types ---

    public static final Endec<class_2540> FRIENDLY_BYTE_BUF = Endec.BYTES
            .xmap(bytes -> {
                var buffer = PacketByteBufs.create();
                buffer.method_52983(bytes);

                return buffer;
            }, buffer -> {
                var rinx = buffer.readerIndex();

                var bytes = new byte[buffer.readableBytes()];
                buffer.method_52979(bytes);

                buffer.method_52988(rinx);

                return bytes;
            });

    public static final Endec<class_2960> IDENTIFIER = Endec.STRING.xmap(class_2960::method_60654, class_2960::toString);
    public static final Endec<class_1799> ITEM_STACK = CodecUtils.toEndecWithRegistries(class_1799.field_49266, class_1799.field_49268);
    public static final Endec<class_2561> TEXT = CodecUtils.toEndec(class_8824.field_46597, class_8824.field_49668);

    public static final Endec<class_2382> VEC3I = vectorEndec("Vec3i", Endec.INT, class_2382::new, class_2382::method_10263, class_2382::method_10264, class_2382::method_10260);
    public static final Endec<class_243> VEC3 = vectorEndec("Vec3", Endec.DOUBLE, class_243::new, class_243::method_10216, class_243::method_10214, class_243::method_10215);
    public static final Endec<Vector3f> VECTOR3F = vectorEndec("Vector3f", Endec.FLOAT, Vector3f::new, Vector3f::x, Vector3f::y, Vector3f::z);

    public static final Endec<class_2338> BLOCK_POS = Endec
            .ifAttr(
                    SerializationAttributes.HUMAN_READABLE,
                    vectorEndec("BlockPos", Endec.INT, class_2338::new, class_2338::method_10263, class_2338::method_10264, class_2338::method_10260)
            ).orElse(
                    Endec.LONG.xmap(class_2338::method_10092, class_2338::method_10063)
            );

    public static final Endec<class_1923> CHUNK_POS = Endec
            .ifAttr(
                    SerializationAttributes.HUMAN_READABLE,
                    Endec.INT.listOf().validate(ints -> {
                        if (ints.size() != 2) {
                            throw new IllegalStateException("ChunkPos array must have two elements");
                        }
                    }).xmap(
                            ints -> new class_1923(ints.get(0), ints.get(1)),
                            chunkPos -> List.of(chunkPos.field_9181, chunkPos.field_9180)
                    )
            )
            .orElse(Endec.LONG.xmap(class_1923::new, class_1923::method_8324));

    public static final Endec<class_3965> BLOCK_HIT_RESULT = StructEndecBuilder.of(
            VEC3.fieldOf("pos", class_3965::method_17784),
            Endec.forEnum(class_2350.class).fieldOf("side", class_3965::method_17780),
            BLOCK_POS.fieldOf("block_pos", class_3965::method_17777),
            Endec.BOOLEAN.fieldOf("inside_block", class_3965::method_17781),
            Endec.BOOLEAN.fieldOf("missed", $ -> $.method_17783() == class_239.class_240.field_1333),
            (pos, side, blockPos, insideBlock, missed) -> !missed
                    ? new class_3965(pos, side, blockPos, insideBlock)
                    : class_3965.method_17778(pos, side, blockPos)
    );

    // --- Constructors for MC types ---

    public static ReflectiveEndecBuilder addDefaults(ReflectiveEndecBuilder builder) {
        builder.register(FRIENDLY_BYTE_BUF, class_2540.class);

        builder.register(IDENTIFIER, class_2960.class)
                .register(ITEM_STACK, class_1799.class)
                .register(TEXT, class_2561.class);

        builder.register(VEC3I, class_2382.class)
                .register(VEC3, class_243.class)
                .register(VECTOR3F, Vector3f.class);

        builder.register(BLOCK_POS, class_2338.class)
                .register(CHUNK_POS, class_1923.class);

        builder.register(BLOCK_HIT_RESULT, class_3965.class);

        return builder;
    }

    public static <T> Endec<T> ofRegistry(class_2378<T> registry) {
        return IDENTIFIER.xmap(registry::method_63535, registry::method_10221);
    }

    public static <T> Endec<class_6862<T>> unprefixedTagKey(class_5321<? extends class_2378<T>> registry) {
        return IDENTIFIER.xmap(id -> class_6862.method_40092(registry, id), class_6862::comp_327);
    }

    public static <T> Endec<class_6862<T>> prefixedTagKey(class_5321<? extends class_2378<T>> registry) {
        return Endec.STRING.xmap(
                s -> class_6862.method_40092(registry, class_2960.method_60654(s.substring(1))),
                tag -> "#" + tag.comp_327()
        );
    }

    private static <C, V> Endec<V> vectorEndec(String name, Endec<C> componentEndec, Function3<C, C, C, V> constructor, Function<V, C> xGetter, Function<V, C> yGetter, Function<V, C> zGetter) {
        return componentEndec.listOf().validate(ints -> {
            if (ints.size() != 3) {
                throw new IllegalStateException(name + " array must have three elements");
            }
        }).xmap(
                components -> constructor.apply(components.get(0), components.get(1), components.get(2)),
                vector -> List.of(xGetter.apply(vector), yGetter.apply(vector), zGetter.apply(vector))
        );
    }
}
