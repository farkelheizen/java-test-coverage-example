package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreditScoreEvaluatorTest {

    private CreditScoreEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new CreditScoreEvaluator();
    }

    // ─── evaluateScore ────────────────────────────────────────────────────────

    @Test
    void evaluateScore_belowRange_returnsInvalid() {
        assertEquals("INVALID", evaluator.evaluateScore(299));
    }

    @Test
    void evaluateScore_aboveRange_returnsInvalid() {
        assertEquals("INVALID", evaluator.evaluateScore(851));
    }

    @Test
    void evaluateScore_lowerBoundary_returnsNotInvalid() {
        assertNotEquals("INVALID", evaluator.evaluateScore(300));
    }

    @Test
    void evaluateScore_upperBoundary_returnsExcellent() {
        assertEquals("EXCELLENT", evaluator.evaluateScore(850));
    }

    @Test
    void evaluateScore_750_returnsExcellent() {
        assertEquals("EXCELLENT", evaluator.evaluateScore(750));
    }

    @Test
    void evaluateScore_800_returnsExcellent() {
        assertEquals("EXCELLENT", evaluator.evaluateScore(800));
    }

    @Test
    void evaluateScore_670_returnsGood() {
        assertEquals("GOOD", evaluator.evaluateScore(670));
    }

    @Test
    void evaluateScore_700_returnsGood() {
        assertEquals("GOOD", evaluator.evaluateScore(700));
    }

    @Test
    void evaluateScore_580_returnsFair() {
        assertEquals("FAIR", evaluator.evaluateScore(580));
    }

    @Test
    void evaluateScore_620_returnsFair() {
        assertEquals("FAIR", evaluator.evaluateScore(620));
    }

    @Test
    void evaluateScore_579_returnsPoor() {
        assertEquals("POOR", evaluator.evaluateScore(579));
    }

    @Test
    void evaluateScore_300_returnsPoor() {
        assertEquals("POOR", evaluator.evaluateScore(300));
    }

    // ─── getLoanEligibility ───────────────────────────────────────────────────

    @Test
    void getLoanEligibility_creditScoreBelow580_returnsFalse() {
        assertFalse(evaluator.getLoanEligibility(579, 50000, 10000));
    }

    @Test
    void getLoanEligibility_incomeZero_returnsFalse() {
        assertFalse(evaluator.getLoanEligibility(700, 0, 10000));
    }

    @Test
    void getLoanEligibility_incomeNegative_returnsFalse() {
        assertFalse(evaluator.getLoanEligibility(700, -1000, 10000));
    }

    @Test
    void getLoanEligibility_excellentScore_ratioWithinLimit_returnsTrue() {
        // score=750, income=100000, amount=400000 → ratio=4.0 ≤ 5.0
        assertTrue(evaluator.getLoanEligibility(750, 100000, 400000));
    }

    @Test
    void getLoanEligibility_excellentScore_ratioExceedsLimit_returnsFalse() {
        // score=750, income=100000, amount=600000 → ratio=6.0 > 5.0
        assertFalse(evaluator.getLoanEligibility(750, 100000, 600000));
    }

    @Test
    void getLoanEligibility_goodScore_ratioWithinLimit_returnsTrue() {
        // score=670, income=100000, amount=250000 → ratio=2.5 ≤ 3.0
        assertTrue(evaluator.getLoanEligibility(670, 100000, 250000));
    }

    @Test
    void getLoanEligibility_goodScore_ratioExceedsLimit_returnsFalse() {
        // score=670, income=100000, amount=350000 → ratio=3.5 > 3.0
        assertFalse(evaluator.getLoanEligibility(670, 100000, 350000));
    }

    @Test
    void getLoanEligibility_fairScore_ratioWithinLimit_returnsTrue() {
        // score=580, income=100000, amount=100000 → ratio=1.0 ≤ 1.5
        assertTrue(evaluator.getLoanEligibility(580, 100000, 100000));
    }

    @Test
    void getLoanEligibility_fairScore_ratioExceedsLimit_returnsFalse() {
        // score=580, income=100000, amount=200000 → ratio=2.0 > 1.5
        assertFalse(evaluator.getLoanEligibility(580, 100000, 200000));
    }

    // ─── calculateRiskLevel ───────────────────────────────────────────────────

    @Test
    void calculateRiskLevel_excellentScoreLowRatio_returnsLow() {
        assertEquals("LOW", evaluator.calculateRiskLevel(750, 0.1));
    }

    @Test
    void calculateRiskLevel_excellentScoreHighRatio_fallsThroughToMedium() {
        // score>=750 but ratio>=0.2 → first condition fails, check next
        assertEquals("MEDIUM", evaluator.calculateRiskLevel(750, 0.25));
    }

    @Test
    void calculateRiskLevel_goodScoreLowRatio_returnsMedium() {
        assertEquals("MEDIUM", evaluator.calculateRiskLevel(670, 0.3));
    }

    @Test
    void calculateRiskLevel_goodScoreHighRatio_fallsThroughToHigh() {
        // score>=670 but ratio>=0.35
        assertEquals("HIGH", evaluator.calculateRiskLevel(670, 0.4));
    }

    @Test
    void calculateRiskLevel_fairScoreLowRatio_returnsHigh() {
        assertEquals("HIGH", evaluator.calculateRiskLevel(580, 0.4));
    }

    @Test
    void calculateRiskLevel_poorScoreHighRatio_returnsVeryHigh() {
        assertEquals("VERY_HIGH", evaluator.calculateRiskLevel(500, 0.6));
    }

    @Test
    void calculateRiskLevel_fairScoreRatioAtOrAbove05_returnsVeryHigh() {
        assertEquals("VERY_HIGH", evaluator.calculateRiskLevel(580, 0.5));
    }
}
