package com.example.service;

import com.example.model.Customer;
import com.example.model.Order;

public class DiscountCalculator {

    public double calculateDiscount(Order order, Customer customer) {
        double discount = 0.0;
        double total = order.getTotalAmount();
        if (customer.isPremium()) {
            discount += total * 0.10;
        }
        String tier = new CustomerService().getLoyaltyTier(customer);
        if ("GOLD".equals(tier)) {
            discount += total * 0.05;
        }
        return discount;
    }

    public double applyPromoCode(Order order, String code) {
        double total = order.getTotalAmount();
        return switch (code.toUpperCase()) {
            case "SAVE10" -> total * 0.10;
            case "SAVE20" -> total * 0.20;
            case "FREESHIP" -> 9.99;
            default -> 0.0;
        };
    }

    public double calculateBulkDiscount(int quantity, double unitPrice) {
        double discountRate;
        if (quantity >= 100) {
            discountRate = 0.20;
        } else if (quantity >= 50) {
            discountRate = 0.15;
        } else if (quantity >= 25) {
            discountRate = 0.10;
        } else if (quantity >= 10) {
            discountRate = 0.05;
        } else {
            discountRate = 0.0;
        }
        return quantity * unitPrice * discountRate;
    }
}
