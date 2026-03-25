package com.example.service;

import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.model.Product;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    public double generateSalesReport(List<Order> orders, LocalDate from, LocalDate to) {
        return orders.stream()
                .filter(o -> {
                    LocalDate orderDate = o.getOrderDate().toLocalDate();
                    return !orderDate.isBefore(from) && !orderDate.isAfter(to);
                })
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    public List<Product> getTopProducts(List<OrderItem> items, int topN) {
        Map<String, Integer> quantityByProduct = new HashMap<>();
        Map<String, Product> productMap = new HashMap<>();
        for (OrderItem item : items) {
            String id = item.getProduct().getProductId();
            quantityByProduct.merge(id, item.getQuantity(), Integer::sum);
            productMap.put(id, item.getProduct());
        }
        return quantityByProduct.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topN)
                .map(e -> productMap.get(e.getKey()))
                .collect(Collectors.toList());
    }

    public Map<String, Double> calculateRevenueByCategory(List<Order> orders) {
        Map<String, Double> revenue = new HashMap<>();
        for (Order order : orders) {
            if (order.getItems() == null) continue;
            for (OrderItem item : order.getItems()) {
                String category = item.getProduct().getCategory().name();
                revenue.merge(category, item.getTotalPrice(), Double::sum);
            }
        }
        return revenue;
    }
}
