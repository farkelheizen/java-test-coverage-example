package com.example.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {

    public static boolean isInteger(String s) {
        if (s == null || s.isEmpty()) return false;
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String s) {
        if (s == null || s.isEmpty()) return false;
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double roundToDecimalPlaces(double value, int places) {
        return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    public static double percentageOf(double value, double total) {
        if (total == 0) return 0.0;
        return (value / total) * 100.0;
    }

    public static double safeDivide(double numerator, double denominator, double defaultValue) {
        if (denominator == 0) return defaultValue;
        return numerator / denominator;
    }
}
