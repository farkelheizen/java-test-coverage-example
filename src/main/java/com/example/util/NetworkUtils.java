package com.example.util;

public class NetworkUtils {

    public static boolean isValidIpAddress(String ip) {
        if (ip == null) return false;
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;
        for (String part : parts) {
            try {
                int val = Integer.parseInt(part);
                if (val < 0 || val > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidUrl(String url) {
        if (url == null) return false;
        return url.startsWith("http://") || url.startsWith("https://");
    }

    public static String extractDomain(String url) {
        if (!isValidUrl(url)) return null;
        String withoutProtocol = url.replaceFirst("https?://", "");
        int slashIndex = withoutProtocol.indexOf('/');
        return slashIndex >= 0 ? withoutProtocol.substring(0, slashIndex) : withoutProtocol;
    }

    public static String extractProtocol(String url) {
        if (url == null) return null;
        int colonIndex = url.indexOf("://");
        if (colonIndex < 0) return null;
        return url.substring(0, colonIndex);
    }

    public static boolean isLocalhost(String host) {
        if (host == null) return false;
        return "localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host) || "::1".equals(host);
    }
}
