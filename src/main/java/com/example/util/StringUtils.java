package com.example.util;

public class StringUtils {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static String capitalize(String s) {
        if (isNullOrEmpty(s)) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String repeat(String s, int times) {
        if (s == null) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) sb.append(s);
        return sb.toString();
    }

    public static String leftPad(String s, int length, char padChar) {
        if (s == null) s = "";
        if (s.length() >= length) return s;
        return repeat(String.valueOf(padChar), length - s.length()) + s;
    }

    public static String rightPad(String s, int length, char padChar) {
        if (s == null) s = "";
        if (s.length() >= length) return s;
        return s + repeat(String.valueOf(padChar), length - s.length());
    }

    public static String reverse(String s) {
        if (s == null) return null;
        return new StringBuilder(s).reverse().toString();
    }

    public static int countOccurrences(String s, char c) {
        if (s == null) return 0;
        int count = 0;
        for (char ch : s.toCharArray()) {
            if (ch == c) count++;
        }
        return count;
    }
}
