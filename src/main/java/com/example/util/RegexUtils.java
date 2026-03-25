package com.example.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static boolean matches(String input, String pattern) {
        if (input == null || pattern == null) return false;
        return input.matches(pattern);
    }

    public static String extractGroup(String input, String pattern, int group) {
        if (input == null || pattern == null) return null;
        Matcher m = Pattern.compile(pattern).matcher(input);
        if (m.find() && group <= m.groupCount()) {
            return m.group(group);
        }
        return null;
    }

    public static String replaceAll(String input, String pattern, String replacement) {
        if (input == null) return null;
        return input.replaceAll(pattern, replacement);
    }

    public static List<String> findAll(String input, String pattern) {
        List<String> results = new ArrayList<>();
        if (input == null || pattern == null) return results;
        Matcher m = Pattern.compile(pattern).matcher(input);
        while (m.find()) {
            results.add(m.group());
        }
        return results;
    }
}
