package com.example.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaginationService {

    public <T> List<T> paginate(List<T> items, int page, int pageSize) {
        validatePageParams(page, pageSize);
        int fromIndex = (page - 1) * pageSize;
        if (fromIndex >= items.size()) return List.of();
        int toIndex = Math.min(fromIndex + pageSize, items.size());
        return items.subList(fromIndex, toIndex);
    }

    public int getTotalPages(int totalItems, int pageSize) {
        if (pageSize <= 0) throw new IllegalArgumentException("Page size must be positive");
        return (int) Math.ceil((double) totalItems / pageSize);
    }

    public void validatePageParams(int page, int pageSize) {
        if (page < 1) throw new IllegalArgumentException("Page must be >= 1");
        if (pageSize < 1 || pageSize > 100) throw new IllegalArgumentException("Page size must be between 1 and 100");
    }

    public Map<String, Object> createPageInfo(int currentPage, int pageSize, long totalItems) {
        Map<String, Object> info = new HashMap<>();
        info.put("currentPage", currentPage);
        info.put("pageSize", pageSize);
        info.put("totalItems", totalItems);
        info.put("totalPages", getTotalPages((int) totalItems, pageSize));
        info.put("hasNext", currentPage < getTotalPages((int) totalItems, pageSize));
        info.put("hasPrevious", currentPage > 1);
        return info;
    }
}
