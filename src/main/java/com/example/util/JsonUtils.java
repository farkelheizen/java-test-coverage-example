package com.example.util;

import java.util.List;
import java.util.Map;

public class JsonUtils {

    public static String escapeJsonString(String s) {
        if (s == null) return "null";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static String toJsonString(Map<String, Object> map) {
        if (map == null) return "null";
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escapeJsonString(entry.getKey())).append("\":");
            Object value = entry.getValue();
            if (value instanceof String s) {
                sb.append("\"").append(escapeJsonString(s)).append("\"");
            } else if (value == null) {
                sb.append("null");
            } else {
                sb.append(value);
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    public static String buildJsonArray(List<String> items) {
        if (items == null) return "null";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(escapeJsonString(items.get(i))).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String buildJsonObject(Map<String, String> map) {
        if (map == null) return "null";
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escapeJsonString(entry.getKey())).append("\":");
            sb.append("\"").append(escapeJsonString(entry.getValue())).append("\"");
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
