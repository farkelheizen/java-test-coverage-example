package com.example.util;

public class MathUtils {

    public static double roundUp(double value) {
        return Math.ceil(value);
    }

    public static double roundDown(double value) {
        return Math.floor(value);
    }

    public static double average(double... values) {
        if (values == null || values.length == 0) return 0.0;
        double sum = 0;
        for (double v : values) sum += v;
        return sum / values.length;
    }

    public static double max(double... values) {
        if (values == null || values.length == 0) throw new IllegalArgumentException("No values provided");
        double max = values[0];
        for (double v : values) if (v > max) max = v;
        return max;
    }

    public static double min(double... values) {
        if (values == null || values.length == 0) throw new IllegalArgumentException("No values provided");
        double min = values[0];
        for (double v : values) if (v < min) min = v;
        return min;
    }

    public static double sumOf(double... values) {
        if (values == null) return 0.0;
        double sum = 0;
        for (double v : values) sum += v;
        return sum;
    }

    public static double standardDeviation(double... values) {
        if (values == null || values.length < 2) return 0.0;
        double mean = average(values);
        double sumSq = 0;
        for (double v : values) sumSq += Math.pow(v - mean, 2);
        return Math.sqrt(sumSq / values.length);
    }
}
