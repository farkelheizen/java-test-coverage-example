package com.example.service;

public class StringProcessingService {

    public String toCamelCase(String input) {
        if (input == null || input.isEmpty()) return input;
        String[] parts = input.split("_");
        StringBuilder result = new StringBuilder(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                result.append(Character.toUpperCase(parts[i].charAt(0)));
                result.append(parts[i].substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    public String toSnakeCase(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.replaceAll("([A-Z])", "_$1").toLowerCase().replaceAll("^_", "");
    }

    public String truncate(String text, int maxLength, String suffix) {
        if (text == null) return null;
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - suffix.length()) + suffix;
    }

    public int countWords(String text) {
        if (text == null || text.isBlank()) return 0;
        return text.trim().split("\\s+").length;
    }

    public boolean isPalindrome(String text) {
        if (text == null) return false;
        String cleaned = text.toLowerCase().replaceAll("[^a-z0-9]", "");
        int left = 0, right = cleaned.length() - 1;
        while (left < right) {
            if (cleaned.charAt(left) != cleaned.charAt(right)) return false;
            left++;
            right--;
        }
        return true;
    }
}
