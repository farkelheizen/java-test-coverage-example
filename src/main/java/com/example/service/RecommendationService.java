package com.example.service;

import com.example.model.Customer;
import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.model.Product;
import java.util.*;
import java.util.stream.Collectors;

public class RecommendationService {

    public List<Product> getRelatedProducts(Product product, List<Product> catalog) {
        double minPrice = product.getPrice() * 0.7;
        double maxPrice = product.getPrice() * 1.3;
        return catalog.stream()
                .filter(p -> !p.getProductId().equals(product.getProductId()))
                .filter(p -> p.getCategory() == product.getCategory())
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    public List<Product> getPersonalizedRecommendations(Customer customer, List<Order> history,
                                                         List<Product> catalog) {
        Set<String> purchasedIds = new HashSet<>();
        Map<String, Integer> categoryCount = new HashMap<>();
        for (Order order : history) {
            if (order.getItems() == null) continue;
            for (OrderItem item : order.getItems()) {
                purchasedIds.add(item.getProduct().getProductId());
                categoryCount.merge(item.getProduct().getCategory().name(), 1, Integer::sum);
            }
        }
        String topCategory = categoryCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return catalog.stream()
                .filter(p -> !purchasedIds.contains(p.getProductId()))
                .filter(p -> topCategory == null || p.getCategory().name().equals(topCategory))
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Product> getBestSellers(List<OrderItem> allItems, int topN) {
        Map<String, Integer> quantityMap = new HashMap<>();
        Map<String, Product> productMap = new HashMap<>();
        for (OrderItem item : allItems) {
            String id = item.getProduct().getProductId();
            quantityMap.merge(id, item.getQuantity(), Integer::sum);
            productMap.put(id, item.getProduct());
        }
        return quantityMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topN)
                .map(e -> productMap.get(e.getKey()))
                .collect(Collectors.toList());
    }
}
