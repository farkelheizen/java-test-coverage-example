package com.example.service;

import com.example.model.Product;

public class PricingEngine {

    public double calculateDynamicPrice(Product product, double demandFactor, double competitorPrice) {
        double basePrice = product.getPrice();
        double demandAdjusted = basePrice * demandFactor;
        if (competitorPrice > 0 && demandAdjusted > competitorPrice * 1.1) {
            demandAdjusted = competitorPrice * 1.05;
        }
        return Math.max(basePrice * 0.5, demandAdjusted);
    }

    public double applySeasonalAdjustment(double basePrice, int month) {
        return switch (month) {
            case 12, 1, 2 -> basePrice * 1.15; // Winter / holiday season
            case 6, 7, 8 -> basePrice * 1.10; // Summer
            case 11 -> basePrice * 1.20;       // Black Friday month
            default -> basePrice;
        };
    }

    public double calculateMargin(double sellingPrice, double costPrice) {
        if (sellingPrice <= 0) return 0.0;
        return ((sellingPrice - costPrice) / sellingPrice) * 100.0;
    }

    public double suggestOptimalPrice(double costPrice, double targetMargin, double competitorPrice) {
        double priceForMargin = costPrice / (1 - targetMargin / 100.0);
        if (competitorPrice > 0) {
            if (priceForMargin > competitorPrice * 1.15) {
                return competitorPrice * 1.05;
            }
        }
        return priceForMargin;
    }
}
