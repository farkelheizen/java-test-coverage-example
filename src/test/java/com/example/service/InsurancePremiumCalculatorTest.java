package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class InsurancePremiumCalculatorTest {

    private InsurancePremiumCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new InsurancePremiumCalculator();
    }

    // ---- calculateAutoPremium ----

    @Test
    void calculateAutoPremium_youngDriver_appliesYouthMultiplier() {
        // age<25: base=500*1.5=750, exp>=2 and <=10 (no modifier), accidents=0, sedan
        double result = calculator.calculateAutoPremium(20, 5, 0, "SEDAN");
        assertEquals(750.0, result, 0.001);
    }

    @Test
    void calculateAutoPremium_seniorDriver_appliesSeniorMultiplier() {
        // age>65: base=500*1.3=650, exp>=2: no mod, accidents=0, sedan
        double result = calculator.calculateAutoPremium(70, 5, 0, "SEDAN");
        assertEquals(650.0, result, 0.001);
    }

    @Test
    void calculateAutoPremium_midAgeDriver_noAgeMultiplier() {
        // age 25-65: base=500, exp>=2: no mod, accidents=0, sedan
        double result = calculator.calculateAutoPremium(40, 5, 0, "SEDAN");
        assertEquals(500.0, result, 0.001);
    }

    @Test
    void calculateAutoPremium_lowExperience_appliesInexperienceMultiplier() {
        // age 25-65: base=500, exp<2: *1.4=700, accidents=0, sedan
        double result = calculator.calculateAutoPremium(30, 1, 0, "SEDAN");
        assertEquals(700.0, result, 0.001);
    }

    @Test
    void calculateAutoPremium_highExperience_appliesDiscountMultiplier() {
        // age 25-65: base=500, exp>10: *0.85=425, accidents=0, sedan
        double result = calculator.calculateAutoPremium(40, 15, 0, "SEDAN");
        assertEquals(425.0, result, 0.001);
    }

    @Test
    void calculateAutoPremium_withAccidents_addsAccidentCost() {
        // age 25-65: base=500, exp in range, accidents=2: +400, sedan -> 900
        double result = calculator.calculateAutoPremium(40, 5, 2, "SEDAN");
        assertEquals(900.0, result, 0.001);
    }

    @Test
    void calculateAutoPremium_sportsVehicle_appliesSportsMultiplier() {
        // age 25-65: base=500, exp in range, accidents=0, SPORTS: *1.3=650
        double result = calculator.calculateAutoPremium(40, 5, 0, "SPORTS");
        assertEquals(650.0, result, 0.001);
    }

    @Test
    void calculateAutoPremium_truckVehicle_appliesTruckMultiplier() {
        // age 25-65: base=500, exp in range, accidents=0, TRUCK: *1.1=550
        double result = calculator.calculateAutoPremium(40, 5, 0, "TRUCK");
        assertEquals(550.0, result, 0.001);
    }

    @Test
    void calculateAutoPremium_sportsVehicleCaseInsensitive_appliesMultiplier() {
        // SPORTS lowercase
        double result = calculator.calculateAutoPremium(40, 5, 0, "sports");
        assertEquals(650.0, result, 0.001);
    }

    @Test
    void calculateAutoPremium_youngDriverLowExpSportsWithAccident_combinedFactors() {
        // age<25: *1.5, exp<2: *1.4, accidents=1: +200, SPORTS: *1.3
        // base=500 * 1.5 = 750, * 1.4 = 1050, + 200 = 1250, * 1.3 = 1625
        double result = calculator.calculateAutoPremium(20, 1, 1, "SPORTS");
        assertEquals(1625.0, result, 0.001);
    }

    // ---- calculateHomePremium ----

    @Test
    void calculateHomePremium_floodZone_appliesFloodMultiplier() {
        // base = 200000 * 0.005 = 1000, FLOOD_ZONE: *1.5 = 1500, no security
        // yearBuilt 2000: age = currentYear-2000, likely <=30 (if current >= 2000)
        int recentYear = Year.now().getValue() - 10; // age = 10, <= 30
        double result = calculator.calculateHomePremium(200000, "FLOOD_ZONE", false, recentYear);
        assertEquals(1500.0, result, 0.001);
    }

    @Test
    void calculateHomePremium_fireZone_appliesFireMultiplier() {
        int recentYear = Year.now().getValue() - 10;
        double result = calculator.calculateHomePremium(200000, "FIRE_ZONE", false, recentYear);
        assertEquals(1400.0, result, 0.001);
    }

    @Test
    void calculateHomePremium_otherLocation_noZoneMultiplier() {
        int recentYear = Year.now().getValue() - 10;
        double result = calculator.calculateHomePremium(200000, "SUBURBAN", false, recentYear);
        assertEquals(1000.0, result, 0.001);
    }

    @Test
    void calculateHomePremium_withSecuritySystem_appliesDiscount() {
        int recentYear = Year.now().getValue() - 10;
        // base=1000, no zone, security: *0.9 = 900
        double result = calculator.calculateHomePremium(200000, "SUBURBAN", true, recentYear);
        assertEquals(900.0, result, 0.001);
    }

    @Test
    void calculateHomePremium_houseOlderThan50Years_appliesOldHouseMultiplier() {
        int oldYear = Year.now().getValue() - 60;
        // base=1000, no zone, no security, age>50: *1.3 = 1300
        double result = calculator.calculateHomePremium(200000, "SUBURBAN", false, oldYear);
        assertEquals(1300.0, result, 0.001);
    }

    @Test
    void calculateHomePremium_houseAge31To50_appliesModerateMultiplier() {
        int year = Year.now().getValue() - 40;
        // base=1000, no zone, no security, age>30 but <=50: *1.15 = 1150
        double result = calculator.calculateHomePremium(200000, "SUBURBAN", false, year);
        assertEquals(1150.0, result, 0.001);
    }

    @Test
    void calculateHomePremium_floodZoneWithSecurity_combinedFactors() {
        int recentYear = Year.now().getValue() - 10;
        // base=1000, FLOOD_ZONE: *1.5=1500, security: *0.9=1350
        double result = calculator.calculateHomePremium(200000, "FLOOD_ZONE", true, recentYear);
        assertEquals(1350.0, result, 0.001);
    }

    // ---- calculateLifePremium ----

    @Test
    void calculateLifePremium_ageUnder30_usesLowRate() {
        // age<30: rate=0.5, non-smoker, GOOD health
        // coverage=100000: (100000/1000) * 0.5 * 12 = 600
        double result = calculator.calculateLifePremium(25, false, "GOOD", 100000);
        assertEquals(600.0, result, 0.001);
    }

    @Test
    void calculateLifePremium_age30to44_usesMidRate() {
        // age 30-44: rate=1.0, non-smoker, GOOD
        // (100000/1000) * 1.0 * 12 = 1200
        double result = calculator.calculateLifePremium(35, false, "GOOD", 100000);
        assertEquals(1200.0, result, 0.001);
    }

    @Test
    void calculateLifePremium_age45to59_usesHigherRate() {
        // age 45-59: rate=2.5, non-smoker, GOOD
        // (100000/1000) * 2.5 * 12 = 3000
        double result = calculator.calculateLifePremium(50, false, "GOOD", 100000);
        assertEquals(3000.0, result, 0.001);
    }

    @Test
    void calculateLifePremium_age60Plus_usesHighestRate() {
        // age>=60: rate=5.0, non-smoker, GOOD
        // (100000/1000) * 5.0 * 12 = 6000
        double result = calculator.calculateLifePremium(65, false, "GOOD", 100000);
        assertEquals(6000.0, result, 0.001);
    }

    @Test
    void calculateLifePremium_smoker_doublesRate() {
        // age<30: rate=0.5*2=1.0, GOOD health
        // (100000/1000) * 1.0 * 12 = 1200
        double result = calculator.calculateLifePremium(25, true, "GOOD", 100000);
        assertEquals(1200.0, result, 0.001);
    }

    @Test
    void calculateLifePremium_poorHealth_appliesPoorHealthMultiplier() {
        // age<30: rate=0.5, POOR: *1.5=0.75
        // (100000/1000) * 0.75 * 12 = 900
        double result = calculator.calculateLifePremium(25, false, "POOR", 100000);
        assertEquals(900.0, result, 0.001);
    }

    @Test
    void calculateLifePremium_excellentHealth_appliesDiscount() {
        // age<30: rate=0.5, EXCELLENT: *0.85=0.425
        // (100000/1000) * 0.425 * 12 = 510
        double result = calculator.calculateLifePremium(25, false, "EXCELLENT", 100000);
        assertEquals(510.0, result, 0.001);
    }

    @Test
    void calculateLifePremium_smokerPoorHealth_combinedMultipliers() {
        // age<30: rate=0.5, smoker: *2=1.0, POOR: *1.5=1.5
        // (100000/1000) * 1.5 * 12 = 1800
        double result = calculator.calculateLifePremium(25, true, "POOR", 100000);
        assertEquals(1800.0, result, 0.001);
    }

    // ---- applyDiscounts ----

    @Test
    void applyDiscounts_neitherLoyalNorMultiplePolicies_noDiscount() {
        assertEquals(1000.0, calculator.applyDiscounts(1000.0, false, false), 0.001);
    }

    @Test
    void applyDiscounts_loyalOnly_appliesLoyaltyDiscount() {
        assertEquals(950.0, calculator.applyDiscounts(1000.0, true, false), 0.001);
    }

    @Test
    void applyDiscounts_multiplePoliciesOnly_appliesPolicyDiscount() {
        assertEquals(900.0, calculator.applyDiscounts(1000.0, false, true), 0.001);
    }

    @Test
    void applyDiscounts_bothLoyalAndMultiplePolicies_appliesBothDiscounts() {
        // 1000 * 0.95 * 0.90 = 855
        assertEquals(855.0, calculator.applyDiscounts(1000.0, true, true), 0.001);
    }
}
