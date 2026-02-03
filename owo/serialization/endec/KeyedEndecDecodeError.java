package io.wispforest.owo.serialization.endec;

import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.owo.Owo;
import java.io.PrintWriter;
import java.io.StringWriter;
import net.minecraft.class_2520;
import net.minecraft.class_8942;

// TODO: GET ENDEC TRACE WHEN USING LATEST ENDEC OR SOMETHING?
public record KeyedEndecDecodeError(KeyedEndec<?> key, class_2520 element, Exception exception, boolean sendEntireException) implements class_8942.class_11337 {

    public KeyedEndecDecodeError(KeyedEndec<?> key, class_2520 element, Exception exception) {
        this(key, element, exception, Owo.DEBUG);
    }

    @Override
    public String method_71358() {
        var message = new StringWriter();

        var writer = new PrintWriter(message);

        writer.println("Failed to decode value '" + this.element + "' from KeyedEndec '" + this.key.key() + "': ");

        if (sendEntireException) {
            writer.println(exception.getMessage());
        } else {
            exception.printStackTrace(writer);
        }

        return message.toString();
    }
}
