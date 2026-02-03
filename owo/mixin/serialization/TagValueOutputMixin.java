package io.wispforest.owo.mixin.serialization;

import com.mojang.serialization.DynamicOps;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.endec.util.MapCarrierEncodable;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.KeyedEndecEncodeError;
import net.minecraft.class_11362;
import net.minecraft.class_2487;
import net.minecraft.class_2520;
import net.minecraft.class_8942;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(class_11362.class)
public abstract class TagValueOutputMixin implements MapCarrierEncodable {
    @Shadow
    @Final
    private class_2487 output;

    @Shadow
    @Final
    private class_8942 problemReporter;

    @Shadow
    @Final
    private DynamicOps<class_2520> ops;

    @Override
    public <T> void put(SerializationContext ctx, @NotNull KeyedEndec<T> key, @NotNull T value) {
        ctx = CodecUtils.createContext(this.ops, ctx);

        try {
            this.output.put(ctx, key, value);
        } catch (Exception e) {
            problemReporter.method_54947(new KeyedEndecEncodeError(key, value, e, false));
        }
    }
}
