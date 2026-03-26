package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RiskAssessmentServiceTest {

    private RiskAssessmentService riskAssessmentService;

    @BeforeEach
    void setUp() {
        riskAssessmentService = new RiskAssessmentService();
    }

    // --- assessFinancialRisk ---

    @Test
    void assessFinancialRisk_returns100WhenAssetsAreZero() {
        assertEquals(100.0, riskAssessmentService.assessFinancialRisk(0, 50000, 60000));
    }

    @Test
    void assessFinancialRisk_returns100WhenAssetsAreNegative() {
        assertEquals(100.0, riskAssessmentService.assessFinancialRisk(-1000, 50000, 60000));
    }

    @Test
    void assessFinancialRisk_noIncomeScoreWhenIncomeAbove100000() {
        // debtToAsset = 0/100000 = 0, incomeScore = 0
        double result = riskAssessmentService.assessFinancialRisk(100000, 0, 150000);
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void assessFinancialRisk_incomeScore20WhenIncomeBetween50000And100000() {
        // debtToAsset = 0, incomeScore = 20
        double result = riskAssessmentService.assessFinancialRisk(100000, 0, 75000);
        assertEquals(20.0, result, 0.001);
    }

    @Test
    void assessFinancialRisk_incomeScore40WhenIncomeAtOrBelow50000() {
        // debtToAsset = 0, incomeScore = 40
        double result = riskAssessmentService.assessFinancialRisk(100000, 0, 50000);
        assertEquals(40.0, result, 0.001);
    }

    @Test
    void assessFinancialRisk_incomeScore40WhenIncomeIsZero() {
        double result = riskAssessmentService.assessFinancialRisk(100000, 0, 0);
        assertEquals(40.0, result, 0.001);
    }

    @Test
    void assessFinancialRisk_cappedAt100() {
        // Very high liabilities relative to assets drives score over 100
        double result = riskAssessmentService.assessFinancialRisk(1000, 5000, 0);
        assertEquals(100.0, result, 0.001);
    }

    @Test
    void assessFinancialRisk_combinedCalculation() {
        // assets=100000, liabilities=50000, income=75000
        // debtToAsset = 0.5, incomeScore = 20
        // score = 0.5*60 + 20 = 50
        double result = riskAssessmentService.assessFinancialRisk(100000, 50000, 75000);
        assertEquals(50.0, result, 0.001);
    }

    // --- assessOperationalRisk ---

    @Test
    void assessOperationalRisk_baselineIs50() {
        // employees=100 (neither <10 nor >500), revenue=1M (neither <500k nor >10M), years=5 (neither <2 nor >10)
        double result = riskAssessmentService.assessOperationalRisk(100, 1_000_000, 5);
        assertEquals(50.0, result, 0.001);
    }

    @Test
    void assessOperationalRisk_smallEmployeeCountAdds20() {
        // employees=5, medium revenue, medium years => 50+20=70
        double result = riskAssessmentService.assessOperationalRisk(5, 1_000_000, 5);
        assertEquals(70.0, result, 0.001);
    }

    @Test
    void assessOperationalRisk_largeEmployeeCountSubtracts10() {
        // employees=501, medium revenue, medium years => 50-10=40
        double result = riskAssessmentService.assessOperationalRisk(501, 1_000_000, 5);
        assertEquals(40.0, result, 0.001);
    }

    @Test
    void assessOperationalRisk_lowRevenueAdds15() {
        // medium employees, revenue=400000, medium years => 50+15=65
        double result = riskAssessmentService.assessOperationalRisk(100, 400_000, 5);
        assertEquals(65.0, result, 0.001);
    }

    @Test
    void assessOperationalRisk_highRevenueSubtracts15() {
        // medium employees, revenue=11M, medium years => 50-15=35
        double result = riskAssessmentService.assessOperationalRisk(100, 11_000_000, 5);
        assertEquals(35.0, result, 0.001);
    }

    @Test
    void assessOperationalRisk_youngBusinessAdds20() {
        // medium employees, medium revenue, years=1 => 50+20=70
        double result = riskAssessmentService.assessOperationalRisk(100, 1_000_000, 1);
        assertEquals(70.0, result, 0.001);
    }

    @Test
    void assessOperationalRisk_establishedBusinessSubtracts10() {
        // medium employees, medium revenue, years=11 => 50-10=40
        double result = riskAssessmentService.assessOperationalRisk(100, 1_000_000, 11);
        assertEquals(40.0, result, 0.001);
    }

    @Test
    void assessOperationalRisk_clampedToZeroMinimum() {
        // large employee count (-10), high revenue (-15), established (-10) => 50-10-15-10=15, no negative possible here
        // To get below 0: need to subtract more than 50
        // Actually max subtraction is 10+15+10 = 35 => 50-35=15, so can't go below 0 with normal combos
        // Let's just confirm it works with the max subtraction
        double result = riskAssessmentService.assessOperationalRisk(501, 11_000_000, 11);
        assertEquals(15.0, result, 0.001);
    }

    @Test
    void assessOperationalRisk_clampedTo100Maximum() {
        // small employees (+20), low revenue (+15), young business (+20) => 50+20+15+20=105, capped at 100
        double result = riskAssessmentService.assessOperationalRisk(5, 400_000, 1);
        assertEquals(100.0, result, 0.001);
    }

    // --- calculateOverallRiskScore ---

    @Test
    void calculateOverallRiskScore_correctWeightedAverage() {
        // 40*0.4 + 50*0.35 + 60*0.25 = 16 + 17.5 + 15 = 48.5
        double result = riskAssessmentService.calculateOverallRiskScore(40, 50, 60);
        assertEquals(48.5, result, 0.001);
    }

    @Test
    void calculateOverallRiskScore_allZeros() {
        assertEquals(0.0, riskAssessmentService.calculateOverallRiskScore(0, 0, 0), 0.001);
    }

    @Test
    void calculateOverallRiskScore_all100() {
        assertEquals(100.0, riskAssessmentService.calculateOverallRiskScore(100, 100, 100), 0.001);
    }

    // --- getRiskCategory ---

    @Test
    void getRiskCategory_returnsLowWhenScoreBelow30() {
        assertEquals("LOW", riskAssessmentService.getRiskCategory(0));
        assertEquals("LOW", riskAssessmentService.getRiskCategory(29.9));
    }

    @Test
    void getRiskCategory_returnsMediumWhenScoreFrom30To59() {
        assertEquals("MEDIUM", riskAssessmentService.getRiskCategory(30));
        assertEquals("MEDIUM", riskAssessmentService.getRiskCategory(59.9));
    }

    @Test
    void getRiskCategory_returnsHighWhenScoreFrom60To79() {
        assertEquals("HIGH", riskAssessmentService.getRiskCategory(60));
        assertEquals("HIGH", riskAssessmentService.getRiskCategory(79.9));
    }

    @Test
    void getRiskCategory_returnsCriticalWhenScoreAtOrAbove80() {
        assertEquals("CRITICAL", riskAssessmentService.getRiskCategory(80));
        assertEquals("CRITICAL", riskAssessmentService.getRiskCategory(100));
    }
}
