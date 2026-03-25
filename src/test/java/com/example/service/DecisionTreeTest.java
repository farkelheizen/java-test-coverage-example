package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DecisionTreeTest {

    private DecisionTree tree;

    @BeforeEach
    void setUp() {
        tree = new DecisionTree();
    }

    // ---- classify ----

    @Test
    void classify_youngWithHighIncome_returnsYoungEarner() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 20);
        features.put("income", 60000.0);
        features.put("hasLoan", false);
        assertEquals("YOUNG_EARNER", tree.classify(features));
    }

    @Test
    void classify_youngWithLowIncome_returnsYoungLimited() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 20);
        features.put("income", 30000.0);
        features.put("hasLoan", false);
        assertEquals("YOUNG_LIMITED", tree.classify(features));
    }

    @Test
    void classify_youngWithNonDoubleIncome_returnsYoungLimited() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 20);
        features.put("income", "notADouble");
        features.put("hasLoan", false);
        assertEquals("YOUNG_LIMITED", tree.classify(features));
    }

    @Test
    void classify_midAgeWithLoan_returnsMidLeveraged() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 30);
        features.put("income", 40000.0);
        features.put("hasLoan", true);
        assertEquals("MID_LEVERAGED", tree.classify(features));
    }

    @Test
    void classify_midAgeWithoutLoan_returnsMidStable() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 30);
        features.put("income", 40000.0);
        features.put("hasLoan", false);
        assertEquals("MID_STABLE", tree.classify(features));
    }

    @Test
    void classify_seniorWithHighIncome_returnsSeniorWealthy() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 50);
        features.put("income", 150000.0);
        features.put("hasLoan", false);
        assertEquals("SENIOR_WEALTHY", tree.classify(features));
    }

    @Test
    void classify_seniorWithLowIncome_returnsSeniorModerate() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 50);
        features.put("income", 50000.0);
        features.put("hasLoan", false);
        assertEquals("SENIOR_MODERATE", tree.classify(features));
    }

    @Test
    void classify_nonIntegerAge_fallsIntoElseBranchSeniorModerate() {
        // age is a String (not Integer), falls to else; income not >100000
        Map<String, Object> features = new HashMap<>();
        features.put("age", "notAnInteger");
        features.put("income", 40000.0);
        features.put("hasLoan", false);
        assertEquals("SENIOR_MODERATE", tree.classify(features));
    }

    @Test
    void classify_nonIntegerAgeHighIncome_fallsIntoElseBranchSeniorWealthy() {
        // age is a String (not Integer), falls to else; income > 100000
        Map<String, Object> features = new HashMap<>();
        features.put("age", "notAnInteger");
        features.put("income", 150000.0);
        features.put("hasLoan", false);
        assertEquals("SENIOR_WEALTHY", tree.classify(features));
    }

    // ---- evaluate ----

    @Test
    void evaluate_highIncome_aboveThreshold_returnsTrue() {
        assertTrue(tree.evaluate("HIGH_INCOME", 150000));
    }

    @Test
    void evaluate_highIncome_belowThreshold_returnsFalse() {
        assertFalse(tree.evaluate("HIGH_INCOME", 50000));
    }

    @Test
    void evaluate_lowIncome_belowThreshold_returnsTrue() {
        assertTrue(tree.evaluate("LOW_INCOME", 20000));
    }

    @Test
    void evaluate_lowIncome_aboveThreshold_returnsFalse() {
        assertFalse(tree.evaluate("LOW_INCOME", 40000));
    }

    @Test
    void evaluate_adult_atOrAbove18_returnsTrue() {
        assertTrue(tree.evaluate("ADULT", 20));
    }

    @Test
    void evaluate_adult_below18_returnsFalse() {
        assertFalse(tree.evaluate("ADULT", 15));
    }

    @Test
    void evaluate_unknownNodeWithNumber_returnsFalse() {
        assertFalse(tree.evaluate("UNKNOWN", 100));
    }

    @Test
    void evaluate_premiumCustomer_premiumString_returnsTrue() {
        assertTrue(tree.evaluate("PREMIUM_CUSTOMER", "PREMIUM"));
    }

    @Test
    void evaluate_premiumCustomer_regularString_returnsFalse() {
        assertFalse(tree.evaluate("PREMIUM_CUSTOMER", "REGULAR"));
    }

    @Test
    void evaluate_verified_verifiedString_returnsTrue() {
        assertTrue(tree.evaluate("VERIFIED", "VERIFIED"));
    }

    @Test
    void evaluate_unknownNodeWithString_returnsFalse() {
        assertFalse(tree.evaluate("UNKNOWN", "SOMETHING"));
    }

    @Test
    void evaluate_nullValue_returnsFalse() {
        assertFalse(tree.evaluate("HIGH_INCOME", null));
    }

    @Test
    void evaluate_booleanValue_returnsFalse() {
        assertFalse(tree.evaluate("HIGH_INCOME", Boolean.TRUE));
    }

    // ---- getRecommendation ----

    @Test
    void getRecommendation_youngEarner_returnsPremiumSavings() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 20);
        features.put("income", 60000.0);
        features.put("hasLoan", false);
        assertEquals("PREMIUM_SAVINGS", tree.getRecommendation(features));
    }

    @Test
    void getRecommendation_youngLimited_returnsStarterAccount() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 20);
        features.put("income", 30000.0);
        features.put("hasLoan", false);
        assertEquals("STARTER_ACCOUNT", tree.getRecommendation(features));
    }

    @Test
    void getRecommendation_midLeveraged_returnsDebtManagement() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 30);
        features.put("income", 40000.0);
        features.put("hasLoan", true);
        assertEquals("DEBT_MANAGEMENT", tree.getRecommendation(features));
    }

    @Test
    void getRecommendation_midStable_returnsInvestmentPortfolio() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 30);
        features.put("income", 40000.0);
        features.put("hasLoan", false);
        assertEquals("INVESTMENT_PORTFOLIO", tree.getRecommendation(features));
    }

    @Test
    void getRecommendation_seniorWealthy_returnsWealthManagement() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 50);
        features.put("income", 150000.0);
        features.put("hasLoan", false);
        assertEquals("WEALTH_MANAGEMENT", tree.getRecommendation(features));
    }

    @Test
    void getRecommendation_seniorModerate_returnsStandardAccount() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 50);
        features.put("income", 50000.0);
        features.put("hasLoan", false);
        assertEquals("STANDARD_ACCOUNT", tree.getRecommendation(features));
    }
}
