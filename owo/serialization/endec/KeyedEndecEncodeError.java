package io.wispforest.owo.serialization.endec;

import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.owo.Owo;
import java.io.PrintWriter;
import java.io.StringWriter;
import net.minecraft.class_8942;

public record KeyedEndecEncodeError(KeyedEndec<?> key, Object obj, Exception exception, boolean encodedDefaultValue, boolean sendEntireException) implements class_8942.class_11337 {

    public KeyedEndecEncodeError(KeyedEndec<?> key, Object obj, Exception exception, boolean encodedDefaultValue) {
        this(key, obj, exception, encodedDefaultValue, Owo.DEBUG);
    }

    @Override
    public String method_71358() {
        var message = new StringWriter();

        var writer = new PrintWriter(message);

        writer.println("Failed to encode value '" + this.obj + "' with KeyedEndec '" + this.key + "'" + (this.encodedDefaultValue ? "and used default value instead" : "") + ": ");

        if (sendEntireException) {
            writer.println(exception.getMessage());
        } else {
            exception.printStackTrace(writer);
        }

        return message.toString();
    }
}
