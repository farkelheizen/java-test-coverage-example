package com.example.service;

import java.util.ArrayList;
import java.util.List;

public class FileProcessingService {

    public List<String> parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        if (line == null || line.isEmpty()) return fields;
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString().trim());
        return fields;
    }

    public boolean validateFileExtension(String filename, List<String> allowedExtensions) {
        if (filename == null || filename.isEmpty()) return false;
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) return false;
        String ext = filename.substring(dotIndex + 1).toLowerCase();
        return allowedExtensions.stream().anyMatch(e -> e.equalsIgnoreCase(ext));
    }

    public String formatFileSize(long bytes) {
        if (bytes >= 1_073_741_824L) {
            return String.format("%.1f GB", bytes / 1_073_741_824.0);
        } else if (bytes >= 1_048_576L) {
            return String.format("%.1f MB", bytes / 1_048_576.0);
        } else if (bytes >= 1024L) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else {
            return bytes + " B";
        }
    }

    public String sanitizeFilename(String filename) {
        if (filename == null) return "";
        return filename.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
