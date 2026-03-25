package com.example.util;

public class CompressionUtils {

    public static double estimateCompressionRatio(String text) {
        if (text == null || text.isEmpty()) return 1.0;
        long uniqueChars = text.chars().distinct().count();
        return (double) uniqueChars / text.length();
    }

    public static int countRedundancy(String text) {
        if (text == null || text.isEmpty()) return 0;
        long unique = text.chars().distinct().count();
        return (int) (text.length() - unique);
    }

    public static String runLengthEncode(String text) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);
            int count = 1;
            while (i + count < text.length() && text.charAt(i + count) == c) count++;
            sb.append(c);
            if (count > 1) sb.append(count);
            i += count;
        }
        return sb.toString();
    }

    public static String runLengthDecode(String encoded) {
        if (encoded == null || encoded.isEmpty()) return encoded;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < encoded.length()) {
            char c = encoded.charAt(i);
            i++;
            StringBuilder numStr = new StringBuilder();
            while (i < encoded.length() && Character.isDigit(encoded.charAt(i))) {
                numStr.append(encoded.charAt(i));
                i++;
            }
            int count = numStr.length() > 0 ? Integer.parseInt(numStr.toString()) : 1;
            sb.append(String.valueOf(c).repeat(count));
        }
        return sb.toString();
    }
}
