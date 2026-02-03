package io.wispforest.owo.mixin.serialization;

import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.endec.util.MapCarrierDecodable;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.KeyedEndecDecodeError;
import net.minecraft.class_11352;
import net.minecraft.class_11371;
import net.minecraft.class_2487;
import net.minecraft.class_8942;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(class_11352.class)
public abstract class TagValueInputMixin implements MapCarrierDecodable {
    @Shadow
    @Final
    private class_2487 input;

    @Shadow
    @Final
    private class_8942 problemReporter;

    @Shadow
    @Final
    private class_11371 context;

    // TODO: Maybe pass in the ErrorReporter for use within Endecs?
    @Override
    public <T> T getWithErrors(SerializationContext ctx, @NotNull KeyedEndec<T> key) {
        ctx = CodecUtils.createContext(this.context.method_71485(), ctx);

        return this.input.getWithErrors(ctx, key);
    }

    @Override
    public <T> T get(SerializationContext ctx, @NotNull KeyedEndec<T> key) {
        try {
            return this.getWithErrors(ctx, key);
        } catch (Exception e) {
            this.problemReporter.method_54947(new KeyedEndecDecodeError(key, this.input.method_10580(key.key()), e));

            return key.defaultValue();
        }
    }
}
