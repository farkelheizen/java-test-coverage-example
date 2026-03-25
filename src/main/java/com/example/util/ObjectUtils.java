package com.example.util;

public class ObjectUtils {

    public static <T> T defaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    @SafeVarargs
    public static <T> T coalesce(T... values) {
        for (T v : values) {
            if (v != null) return v;
        }
        return null;
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    public static boolean deepEquals(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return a.toString().equals(b.toString());
    }
}
