package com.example.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DecisionTreeTest {

    @InjectMocks
    private DecisionTree decisionTree;

    // ------------------------------------------------------------------ classify

    @Test
    void classify_youngWithHighIncome_returnsYoungEarner() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 20);
        features.put("income", 60000.0);
        assertEquals("YOUNG_EARNER", decisionTree.classify(features));
    }

    @Test
    void classify_youngWithLowIncome_returnsYoungLimited() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 22);
        features.put("income", 30000.0);
        assertEquals("YOUNG_LIMITED", decisionTree.classify(features));
    }

    @Test
    void classify_youngWithNoIncome_returnsYoungLimited() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 18);
        // income not present
        assertEquals("YOUNG_LIMITED", decisionTree.classify(features));
    }

    @Test
    void classify_midAgeWithLoan_returnsMidLeveraged() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 35);
        features.put("hasLoan", Boolean.TRUE);
        assertEquals("MID_LEVERAGED", decisionTree.classify(features));
    }

    @Test
    void classify_midAgeWithoutLoan_returnsMidStable() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 40);
        features.put("hasLoan", Boolean.FALSE);
        assertEquals("MID_STABLE", decisionTree.classify(features));
    }

    @Test
    void classify_midAgeNullLoan_returnsMidStable() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 30);
        features.put("hasLoan", null);
        assertEquals("MID_STABLE", decisionTree.classify(features));
    }

    @Test
    void classify_seniorWithHighIncome_returnsSeniorWealthy() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 60);
        features.put("income", 150000.0);
        assertEquals("SENIOR_WEALTHY", decisionTree.classify(features));
    }

    @Test
    void classify_seniorWithModerateIncome_returnsSeniorModerate() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 55);
        features.put("income", 80000.0);
        assertEquals("SENIOR_MODERATE", decisionTree.classify(features));
    }

    @Test
    void classify_seniorNoIncome_returnsSeniorModerate() {
        Map<String, Object> features = new HashMap<>();
        features.put("age", 70);
        assertEquals("SENIOR_MODERATE", decisionTree.classify(features));
    }

    @Test
    void classify_ageNotInteger_fallsToSeniorBranch() {
        // age is String, not Integer - neither young nor mid pattern matches -> falls to else
        Map<String, Object> features = new HashMap<>();
        features.put("age", "thirty");
        assertEquals("SENIOR_MODERATE", decisionTree.classify(features));
    }

    @Test
    void classify_boundaryAge25_inMidBranch() {
        // age 25 is NOT < 25, so mid branch
        Map<String, Object> features = new HashMap<>();
        features.put("age", 25);
        features.put("hasLoan", Boolean.FALSE);
        assertEquals("MID_STABLE", decisionTree.classify(features));
    }

    @Test
    void classify_boundaryAge45_inSeniorBranch() {
        // age 45 is NOT < 45, so senior branch
        Map<String, Object> features = new HashMap<>();
        features.put("age", 45);
        features.put("income", 200000.0);
        assertEquals("SENIOR_WEALTHY", decisionTree.classify(features));
    }

    // ------------------------------------------------------------------ evaluate

    @Test
    void evaluate_highIncome_trueWhenAbove100k() {
        assertTrue(decisionTree.evaluate("HIGH_INCOME", 150000.0));
    }

    @Test
    void evaluate_highIncome_falseWhenBelow100k() {
        assertFalse(decisionTree.evaluate("HIGH_INCOME", 99999.0));
    }

    @Test
    void evaluate_lowIncome_trueWhenBelow30k() {
        assertTrue(decisionTree.evaluate("LOW_INCOME", 25000.0));
    }

    @Test
    void evaluate_lowIncome_falseWhenAbove30k() {
        assertFalse(decisionTree.evaluate("LOW_INCOME", 35000.0));
    }

    @Test
    void evaluate_adult_trueAtAge18() {
        assertTrue(decisionTree.evaluate("ADULT", 18));
    }

    @Test
    void evaluate_adult_falseWhenUnder18() {
        assertFalse(decisionTree.evaluate("ADULT", 17));
    }

    @Test
    void evaluate_unknownNumericNode_returnsFalse() {
        assertFalse(decisionTree.evaluate("UNKNOWN_NODE", 50000.0));
    }

    @Test
    void evaluate_premiumCustomer_trueForPremiumString() {
        assertTrue(decisionTree.evaluate("PREMIUM_CUSTOMER", "PREMIUM"));
    }

    @Test
    void evaluate_premiumCustomer_caseInsensitive() {
        assertTrue(decisionTree.evaluate("PREMIUM_CUSTOMER", "premium"));
    }

    @Test
    void evaluate_premiumCustomer_falseForOtherString() {
        assertFalse(decisionTree.evaluate("PREMIUM_CUSTOMER", "STANDARD"));
    }

    @Test
    void evaluate_verified_trueForVerifiedString() {
        assertTrue(decisionTree.evaluate("VERIFIED", "VERIFIED"));
    }

    @Test
    void evaluate_verified_caseInsensitive() {
        assertTrue(decisionTree.evaluate("VERIFIED", "verified"));
    }

    @Test
    void evaluate_unknownStringNode_returnsFalse() {
        assertFalse(decisionTree.evaluate("BOGUS", "anything"));
    }

    @Test
    void evaluate_nullValue_returnsFalse() {
        assertFalse(decisionTree.evaluate("HIGH_INCOME", null));
    }

    @Test
    void evaluate_booleanValue_returnsFalse() {
        assertFalse(decisionTree.evaluate("HIGH_INCOME", Boolean.TRUE));
    }

    // ------------------------------------------------------------------ getRecommendation

    @Test
    void getRecommendation_youngEarner_returnsPremiumSavings() {
        Map<String, Object> data = Map.of("age", 20, "income", 60000.0);
        assertEquals("PREMIUM_SAVINGS", decisionTree.getRecommendation(data));
    }

    @Test
    void getRecommendation_youngLimited_returnsStarterAccount() {
        Map<String, Object> data = Map.of("age", 22, "income", 20000.0);
        assertEquals("STARTER_ACCOUNT", decisionTree.getRecommendation(data));
    }

    @Test
    void getRecommendation_midLeveraged_returnsDebtManagement() {
        Map<String, Object> data = Map.of("age", 35, "hasLoan", Boolean.TRUE);
        assertEquals("DEBT_MANAGEMENT", decisionTree.getRecommendation(data));
    }

    @Test
    void getRecommendation_midStable_returnsInvestmentPortfolio() {
        Map<String, Object> data = Map.of("age", 38, "hasLoan", Boolean.FALSE);
        assertEquals("INVESTMENT_PORTFOLIO", decisionTree.getRecommendation(data));
    }

    @Test
    void getRecommendation_seniorWealthy_returnsWealthManagement() {
        Map<String, Object> data = Map.of("age", 60, "income", 200000.0);
        assertEquals("WEALTH_MANAGEMENT", decisionTree.getRecommendation(data));
    }

    @Test
    void getRecommendation_seniorModerate_returnsStandardAccount() {
        Map<String, Object> data = Map.of("age", 55, "income", 70000.0);
        assertEquals("STANDARD_ACCOUNT", decisionTree.getRecommendation(data));
    }
}
