package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InsurancePremiumCalculatorTest {

    private InsurancePremiumCalculator calc;

    @BeforeEach
    void setUp() {
        calc = new InsurancePremiumCalculator();
    }

    // ── calculateAutoPremium ─────────────────────────────────────────────────

    @Test
    void calculateAutoPremium_youngDriverBaseMultiplier() {
        // age < 25 → base*1.5; experience 5, no accidents, standard vehicle
        double result = calc.calculateAutoPremium(20, 5, 0, "CAR");
        assertEquals(500.0 * 1.5, result, 1e-9);
    }

    @Test
    void calculateAutoPremium_seniorDriverBaseMultiplier() {
        // age > 65 → base*1.3; experience=1 (<2) → *1.4
        double result = calc.calculateAutoPremium(70, 1, 0, "CAR");
        assertEquals(500.0 * 1.3 * 1.4, result, 1e-9);
    }

    @Test
    void calculateAutoPremium_experiencedDriverDiscount() {
        // age 30-65, experience > 10 → base*0.85
        double result = calc.calculateAutoPremium(40, 15, 0, "CAR");
        assertEquals(500.0 * 0.85, result, 1e-9);
    }

    @Test
    void calculateAutoPremium_inexperiencedDriverSurcharge() {
        // age 30-65, experience < 2 → base*1.4
        double result = calc.calculateAutoPremium(30, 1, 0, "CAR");
        assertEquals(500.0 * 1.4, result, 1e-9);
    }

    @Test
    void calculateAutoPremium_accidentsAddFlatRate() {
        // age 30, experience 5, 2 accidents = 500 + 2*200 = 900
        double result = calc.calculateAutoPremium(30, 5, 2, "CAR");
        assertEquals(900.0, result, 1e-9);
    }

    @Test
    void calculateAutoPremium_sportsVehicleSurcharge() {
        // base 500, SPORTS → *1.3
        double result = calc.calculateAutoPremium(30, 5, 0, "SPORTS");
        assertEquals(500.0 * 1.3, result, 1e-9);
    }

    @Test
    void calculateAutoPremium_sportsVehicleCaseInsensitive() {
        double result = calc.calculateAutoPremium(30, 5, 0, "sports");
        assertEquals(500.0 * 1.3, result, 1e-9);
    }

    @Test
    void calculateAutoPremium_truckVehicleSurcharge() {
        // base 500, TRUCK → *1.1
        double result = calc.calculateAutoPremium(30, 5, 0, "TRUCK");
        assertEquals(500.0 * 1.1, result, 1e-9);
    }

    @Test
    void calculateAutoPremium_middleAgeStandardVehicle() {
        // age=40, experience=5, no accidents, no sport — no modifiers
        double result = calc.calculateAutoPremium(40, 5, 0, "CAR");
        assertEquals(500.0, result, 1e-9);
    }

    // ── calculateHomePremium ──────────────────────────────────────────────────

    @Test
    void calculateHomePremium_normalLocationNoSecurityNewHome() {
        // age <= 30 → no age multiplier
        int currentYear = java.time.Year.now().getValue();
        int yearBuilt = currentYear - 10; // age = 10
        double base = 100_000 * 0.005;
        assertEquals(base, calc.calculateHomePremium(100_000, "NORMAL", false, yearBuilt), 1e-9);
    }

    @Test
    void calculateHomePremium_floodZoneSurcharge() {
        int yearBuilt = java.time.Year.now().getValue() - 10;
        double expected = 100_000 * 0.005 * 1.5;
        assertEquals(expected, calc.calculateHomePremium(100_000, "FLOOD_ZONE", false, yearBuilt), 1e-9);
    }

    @Test
    void calculateHomePremium_fireZoneSurcharge() {
        int yearBuilt = java.time.Year.now().getValue() - 10;
        double expected = 100_000 * 0.005 * 1.4;
        assertEquals(expected, calc.calculateHomePremium(100_000, "FIRE_ZONE", false, yearBuilt), 1e-9);
    }

    @Test
    void calculateHomePremium_securitySystemDiscount() {
        int yearBuilt = java.time.Year.now().getValue() - 10;
        double expected = 100_000 * 0.005 * 0.9;
        assertEquals(expected, calc.calculateHomePremium(100_000, "NORMAL", true, yearBuilt), 1e-9);
    }

    @Test
    void calculateHomePremium_houseOver50YearsOld() {
        // age > 50 → base * 1.3; use yearBuilt 100 years in the past (always > 50)
        int yearBuilt = java.time.Year.now().getValue() - 100;
        double expected = 100_000 * 0.005 * 1.3;
        assertEquals(expected, calc.calculateHomePremium(100_000, "NORMAL", false, yearBuilt), 1e-9);
    }

    @Test
    void calculateHomePremium_houseBetween30And50YearsOld() {
        // age > 30 but <= 50 → base * 1.15; use yearBuilt 40 years in the past
        int yearBuilt = java.time.Year.now().getValue() - 40;
        double expected = 100_000 * 0.005 * 1.15;
        assertEquals(expected, calc.calculateHomePremium(100_000, "NORMAL", false, yearBuilt), 1e-9);
    }

    @Test
    void calculateHomePremium_locationCaseInsensitive() {
        int yearBuilt = java.time.Year.now().getValue() - 10;
        double expected = 100_000 * 0.005 * 1.5;
        assertEquals(expected, calc.calculateHomePremium(100_000, "flood_zone", false, yearBuilt), 1e-9);
    }

    @Test
    void calculateHomePremium_combinedModifiers() {
        // FLOOD_ZONE * security * age>50
        int yearBuilt = java.time.Year.now().getValue() - 100;
        double expected = 100_000 * 0.005 * 1.5 * 0.9 * 1.3;
        assertEquals(expected, calc.calculateHomePremium(100_000, "FLOOD_ZONE", true, yearBuilt), 1e-9);
    }

    // ── calculateLifePremium ─────────────────────────────────────────────────

    @Test
    void calculateLifePremium_youngNonSmokerGoodHealth() {
        // age < 30, not smoker, GOOD health → rate=0.5
        double expected = (100_000.0 / 1000.0) * 0.5 * 12;
        assertEquals(expected, calc.calculateLifePremium(25, false, "GOOD", 100_000), 1e-9);
    }

    @Test
    void calculateLifePremium_middleAgeNonSmoker() {
        // age 30-44 → rate=1.0
        double expected = (100_000.0 / 1000.0) * 1.0 * 12;
        assertEquals(expected, calc.calculateLifePremium(35, false, "GOOD", 100_000), 1e-9);
    }

    @Test
    void calculateLifePremium_seniorAgeNonSmoker() {
        // age 45-59 → rate=2.5
        double expected = (100_000.0 / 1000.0) * 2.5 * 12;
        assertEquals(expected, calc.calculateLifePremium(50, false, "GOOD", 100_000), 1e-9);
    }

    @Test
    void calculateLifePremium_elderlyNonSmoker() {
        // age >= 60 → rate=5.0
        double expected = (100_000.0 / 1000.0) * 5.0 * 12;
        assertEquals(expected, calc.calculateLifePremium(65, false, "GOOD", 100_000), 1e-9);
    }

    @Test
    void calculateLifePremium_smokerDoublesRate() {
        // age < 30, smoker → rate = 0.5*2.0 = 1.0
        double expected = (100_000.0 / 1000.0) * 1.0 * 12;
        assertEquals(expected, calc.calculateLifePremium(25, true, "GOOD", 100_000), 1e-9);
    }

    @Test
    void calculateLifePremium_poorHealthIncreasesRate() {
        // age < 30, POOR → rate = 0.5*1.5 = 0.75
        double expected = (100_000.0 / 1000.0) * 0.75 * 12;
        assertEquals(expected, calc.calculateLifePremium(25, false, "POOR", 100_000), 1e-9);
    }

    @Test
    void calculateLifePremium_excellentHealthReducesRate() {
        // age < 30, EXCELLENT → rate = 0.5*0.85 = 0.425
        double expected = (100_000.0 / 1000.0) * 0.425 * 12;
        assertEquals(expected, calc.calculateLifePremium(25, false, "EXCELLENT", 100_000), 1e-9);
    }

    // ── applyDiscounts ───────────────────────────────────────────────────────

    @Test
    void applyDiscounts_noDiscounts() {
        assertEquals(1000.0, calc.applyDiscounts(1000.0, false, false), 1e-9);
    }

    @Test
    void applyDiscounts_loyalDiscount() {
        assertEquals(1000.0 * 0.95, calc.applyDiscounts(1000.0, true, false), 1e-9);
    }

    @Test
    void applyDiscounts_multiplePoliciesDiscount() {
        assertEquals(1000.0 * 0.90, calc.applyDiscounts(1000.0, false, true), 1e-9);
    }

    @Test
    void applyDiscounts_bothDiscountsApplied() {
        assertEquals(1000.0 * 0.95 * 0.90, calc.applyDiscounts(1000.0, true, true), 1e-9);
    }
}
