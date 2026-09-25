package net.hvb007.keybindsgalore.config;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

public final class ConfigurationCodec {
    private ConfigurationCodec() {
    }

    public static Object parseValue(Class<?> type, Type genericType, String value) {
        if (type == short.class) {
            return parseShort(value);
        }
        if (type == int.class) {
            return parseInt(value);
        }
        if (type == float.class) {
            return Float.parseFloat(value);
        }
        if (type == boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (type == ArrayList.class) {
            return parseList(genericType, value);
        }
        throw new IllegalArgumentException("Unrecognized configuration type: " + type.getName());
    }

    public static String formatValue(String fieldName, Object value) {
        String key = fieldName.toUpperCase(Locale.ROOT);
        return key + "=" + formatRawValue(key, value);
    }

    public static String formatRawValue(String fieldName, Object value) {
        String key = fieldName.toUpperCase(Locale.ROOT);
        if (value instanceof Integer && key.contains("COLOR")) {
            return String.format(Locale.ROOT, "0x%08X", (Integer) value);
        }
        return String.valueOf(value);
    }

    public static ArrayList<String> parseStringList(String value) {
        ArrayList<String> result = new ArrayList<>();
        for (String item : splitList(value)) {
            if (!item.isEmpty()) {
                result.add(item);
            }
        }
        return result;
    }

    public static ArrayList<Integer> parseIntegerList(String value) {
        ArrayList<Integer> result = new ArrayList<>();
        for (String item : splitList(value)) {
            if (!item.isEmpty()) {
                result.add(Integer.parseInt(item));
            }
        }
        return result;
    }

    private static ArrayList<?> parseList(Type genericType, String value) {
        if (!(genericType instanceof ParameterizedType parameterizedType)) {
            throw new IllegalArgumentException("ArrayList must have a parameterized element type");
        }
        Type elementType = parameterizedType.getActualTypeArguments()[0];
        if (elementType == String.class) {
            return parseStringList(value);
        }
        if (elementType == Integer.class) {
            return parseIntegerList(value);
        }
        throw new IllegalArgumentException("Unrecognized ArrayList element type: " + elementType.getTypeName());
    }

    private static String[] splitList(String value) {
        String[] values = value.replaceAll("[\\[\\]]+", "").split(",");
        if (values.length == 1 && values[0].trim().isEmpty()) {
            return new String[0];
        }
        String[] result = new String[values.length];
        for (int index = 0; index < values.length; index++) {
            result[index] = values[index].trim();
        }
        return result;
    }

    private static short parseShort(String value) {
        if (value.startsWith("0x")) {
            return Short.parseShort(value.replace("0x", ""), 16);
        }
        return Short.parseShort(value);
    }

    private static int parseInt(String value) {
        if (value.startsWith("0x")) {
            return (int) Long.parseLong(value.replace("0x", ""), 16);
        }
        return Integer.parseInt(value);
    }
}
