package com.example.service;

public class InsurancePremiumCalculator {

    public double calculateAutoPremium(int age, int yearsOfExperience, int accidentCount, String vehicleType) {
        double base = 500.0;
        if (age < 25) base *= 1.5;
        else if (age > 65) base *= 1.3;
        if (yearsOfExperience < 2) base *= 1.4;
        else if (yearsOfExperience > 10) base *= 0.85;
        base += accidentCount * 200.0;
        if ("SPORTS".equalsIgnoreCase(vehicleType)) base *= 1.3;
        else if ("TRUCK".equalsIgnoreCase(vehicleType)) base *= 1.1;
        return base;
    }

    public double calculateHomePremium(double homeValue, String location, boolean hasSecuritySystem, int yearBuilt) {
        double base = homeValue * 0.005;
        if ("FLOOD_ZONE".equalsIgnoreCase(location)) base *= 1.5;
        else if ("FIRE_ZONE".equalsIgnoreCase(location)) base *= 1.4;
        if (hasSecuritySystem) base *= 0.9;
        int age = java.time.Year.now().getValue() - yearBuilt;
        if (age > 50) base *= 1.3;
        else if (age > 30) base *= 1.15;
        return base;
    }

    public double calculateLifePremium(int age, boolean isSmoker, String healthStatus, double coverageAmount) {
        double ratePerThousand;
        if (age < 30) ratePerThousand = 0.5;
        else if (age < 45) ratePerThousand = 1.0;
        else if (age < 60) ratePerThousand = 2.5;
        else ratePerThousand = 5.0;
        if (isSmoker) ratePerThousand *= 2.0;
        if ("POOR".equalsIgnoreCase(healthStatus)) ratePerThousand *= 1.5;
        else if ("EXCELLENT".equalsIgnoreCase(healthStatus)) ratePerThousand *= 0.85;
        return (coverageAmount / 1000.0) * ratePerThousand * 12;
    }

    public double applyDiscounts(double basePremium, boolean isLoyal, boolean hasMultiplePolicies) {
        double premium = basePremium;
        if (isLoyal) premium *= 0.95;
        if (hasMultiplePolicies) premium *= 0.90;
        return premium;
    }
}
