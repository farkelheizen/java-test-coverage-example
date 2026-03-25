package com.example.util;

public class FileUtils {

    public static String getExtension(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) return "";
        return filename.substring(dotIndex + 1);
    }

    public static String removeExtension(String filename) {
        if (filename == null) return null;
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) return filename;
        return filename.substring(0, dotIndex);
    }

    public static boolean isValidPath(String path) {
        if (path == null || path.isEmpty()) return false;
        return !path.contains("\0");
    }

    public static String normalizePath(String path) {
        if (path == null) return null;
        return path.replace("\\", "/").replaceAll("/+", "/");
    }

    public static String joinPaths(String... parts) {
        if (parts == null || parts.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isEmpty()) continue;
            if (sb.length() > 0 && !sb.toString().endsWith("/") && !part.startsWith("/")) {
                sb.append("/");
            }
            sb.append(part);
        }
        return normalizePath(sb.toString());
    }
}
