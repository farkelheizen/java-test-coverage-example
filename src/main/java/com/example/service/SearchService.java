package com.example.service;

import com.example.enums.ProductCategory;
import com.example.model.Product;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SearchService {

    public List<Product> searchProducts(List<Product> products, String query,
                                         ProductCategory category, Double maxPrice) {
        return products.stream()
                .filter(p -> {
                    if (query != null && !query.isEmpty()) {
                        if (!p.getName().toLowerCase().contains(query.toLowerCase())
                                && !p.getDescription().toLowerCase().contains(query.toLowerCase())) {
                            return false;
                        }
                    }
                    if (category != null && p.getCategory() != category) return false;
                    if (maxPrice != null && p.getPrice() > maxPrice) return false;
                    return true;
                })
                .collect(Collectors.toList());
    }

    public List<Product> sortResults(List<Product> products, String sortBy, boolean ascending) {
        Comparator<Product> comparator = switch (sortBy.toLowerCase()) {
            case "price" -> Comparator.comparingDouble(Product::getPrice);
            case "name" -> Comparator.comparing(Product::getName);
            default -> Comparator.comparing(Product::getProductId);
        };
        if (!ascending) comparator = comparator.reversed();
        return products.stream().sorted(comparator).collect(Collectors.toList());
    }

    public List<Product> filterByPriceRange(List<Product> products, double min, double max) {
        return products.stream()
                .filter(p -> p.getPrice() >= min && p.getPrice() <= max)
                .collect(Collectors.toList());
    }
}
