package com.example.service;

import com.example.enums.ProductCategory;
import com.example.model.Product;

public class TaxCalculator {

    public double calculateTax(double amount, String state) {
        double rate = switch (state.toUpperCase()) {
            case "CA" -> 0.0725;
            case "NY" -> 0.08;
            case "TX" -> 0.0625;
            case "FL" -> 0.06;
            default -> 0.05;
        };
        return amount * rate;
    }

    public double calculateVAT(double amount, String country) {
        double rate = switch (country.toUpperCase()) {
            case "UK", "GB" -> 0.20;
            case "DE" -> 0.19;
            case "FR" -> 0.20;
            case "US" -> 0.0;
            default -> 0.15;
        };
        return amount * rate;
    }

    public boolean isTaxExempt(Product product) {
        return product.getCategory() == ProductCategory.FOOD;
    }

    public double calculateTotalWithTax(double subtotal, String state, String country) {
        double stateTax = calculateTax(subtotal, state);
        double vat = calculateVAT(subtotal, country);
        return subtotal + stateTax + vat;
    }
}
