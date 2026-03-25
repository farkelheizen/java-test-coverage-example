package com.example.service;

public class ShippingRateCalculator {

    public double calculateRate(double weightKg, double distanceKm, String serviceLevel) {
        double baseRate = weightKg * 0.5 + distanceKm * 0.01;
        return switch (serviceLevel.toUpperCase()) {
            case "EXPRESS" -> baseRate * 2.0;
            case "OVERNIGHT" -> baseRate * 3.5;
            default -> baseRate; // STANDARD
        };
    }

    public double calculateDimensionalWeight(double length, double width, double height) {
        return (length * width * height) / 5000.0;
    }

    public double getBillableWeight(double actualWeight, double dimensionalWeight) {
        return Math.max(actualWeight, dimensionalWeight);
    }

    public double applyFuelSurcharge(double baseRate, double fuelSurchargePercent) {
        return baseRate + (baseRate * fuelSurchargePercent / 100.0);
    }
}
