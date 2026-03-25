package com.example.util;

public class ValidationUtils {

    public static <T> T requireNonNull(T value, String msg) {
        if (value == null) throw new NullPointerException(msg);
        return value;
    }

    public static String requireNonEmpty(String value, String msg) {
        if (value == null || value.isEmpty()) throw new IllegalArgumentException(msg);
        return value;
    }

    public static double requirePositive(double value, String msg) {
        if (value <= 0) throw new IllegalArgumentException(msg);
        return value;
    }

    public static double requireInRange(double value, double min, double max, String msg) {
        if (value < min || value > max) throw new IllegalArgumentException(msg);
        return value;
    }

    public static <E extends Enum<E>> boolean isValidEnum(String value, Class<E> enumClass) {
        if (value == null) return false;
        for (E constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(value)) return true;
        }
        return false;
    }
}
