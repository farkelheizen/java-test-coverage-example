package com.example.util;

public class VersionUtils {

    public static int[] parseVersion(String versionString) {
        if (versionString == null || versionString.isEmpty()) return new int[]{0, 0, 0};
        String[] parts = versionString.split("\\.");
        int[] result = new int[3];
        for (int i = 0; i < Math.min(3, parts.length); i++) {
            try {
                result[i] = Integer.parseInt(parts[i].replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                result[i] = 0;
            }
        }
        return result;
    }

    public static int compareVersions(String v1, String v2) {
        int[] parts1 = parseVersion(v1);
        int[] parts2 = parseVersion(v2);
        for (int i = 0; i < 3; i++) {
            if (parts1[i] < parts2[i]) return -1;
            if (parts1[i] > parts2[i]) return 1;
        }
        return 0;
    }

    public static boolean isCompatible(String required, String actual) {
        int[] req = parseVersion(required);
        int[] act = parseVersion(actual);
        if (act[0] != req[0]) return false; // Major version must match
        if (act[1] < req[1]) return false;  // Minor must be >= required
        return true;
    }

    public static String incrementVersion(String version, String part) {
        int[] parts = parseVersion(version);
        return switch (part.toLowerCase()) {
            case "major" -> (parts[0] + 1) + ".0.0";
            case "minor" -> parts[0] + "." + (parts[1] + 1) + ".0";
            case "patch" -> parts[0] + "." + parts[1] + "." + (parts[2] + 1);
            default -> version;
        };
    }
}
