package com.example.service;

import com.example.model.Product;

public class ProductService {

    public Product addProduct(Product product) {
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        return product;
    }

    public double applyDiscount(Product product, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 100");
        }
        double discountAmount = product.getPrice() * (discountPercent / 100.0);
        double newPrice = product.getPrice() - discountAmount;
        product.setPrice(newPrice);
        return newPrice;
    }

    public boolean isInStock(Product product) {
        return product.getStockQuantity() > 0;
    }

    public void restockProduct(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be greater than 0");
        }
        product.setStockQuantity(product.getStockQuantity() + quantity);
    }
}
