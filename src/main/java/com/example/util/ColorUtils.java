package com.example.util;

public class ColorUtils {

    public static int[] hexToRgb(String hex) {
        if (hex == null || !isValidHexColor(hex)) return null;
        String clean = hex.startsWith("#") ? hex.substring(1) : hex;
        int r = Integer.parseInt(clean.substring(0, 2), 16);
        int g = Integer.parseInt(clean.substring(2, 4), 16);
        int b = Integer.parseInt(clean.substring(4, 6), 16);
        return new int[]{r, g, b};
    }

    public static String rgbToHex(int r, int g, int b) {
        return String.format("#%02X%02X%02X", r, g, b);
    }

    public static boolean isValidHexColor(String hex) {
        if (hex == null) return false;
        return hex.matches("#?[0-9A-Fa-f]{6}");
    }

    public static String blendColors(String hex1, String hex2) {
        int[] rgb1 = hexToRgb(hex1);
        int[] rgb2 = hexToRgb(hex2);
        if (rgb1 == null || rgb2 == null) return null;
        int r = (rgb1[0] + rgb2[0]) / 2;
        int g = (rgb1[1] + rgb2[1]) / 2;
        int b = (rgb1[2] + rgb2[2]) / 2;
        return rgbToHex(r, g, b);
    }

    public static boolean isDarkColor(String hex) {
        int[] rgb = hexToRgb(hex);
        if (rgb == null) return false;
        // Perceived luminance formula
        double luminance = 0.2126 * rgb[0] + 0.7152 * rgb[1] + 0.0722 * rgb[2];
        return luminance < 128;
    }
}
