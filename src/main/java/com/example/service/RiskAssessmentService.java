package com.example.service;

public class RiskAssessmentService {

    public double assessFinancialRisk(double assets, double liabilities, double income) {
        if (assets <= 0) return 100.0;
        double debtToAssetRatio = liabilities / assets;
        double incomeScore = income > 100000 ? 0 : (income > 50000 ? 20 : 40);
        return Math.min(100.0, debtToAssetRatio * 60 + incomeScore);
    }

    public double assessOperationalRisk(int employeeCount, double annualRevenue, int yearsInBusiness) {
        double risk = 50.0;
        if (employeeCount < 10) risk += 20;
        else if (employeeCount > 500) risk -= 10;
        if (annualRevenue < 500000) risk += 15;
        else if (annualRevenue > 10000000) risk -= 15;
        if (yearsInBusiness < 2) risk += 20;
        else if (yearsInBusiness > 10) risk -= 10;
        return Math.max(0, Math.min(100.0, risk));
    }

    public double calculateOverallRiskScore(double financialRisk, double operationalRisk, double marketRisk) {
        return (financialRisk * 0.4) + (operationalRisk * 0.35) + (marketRisk * 0.25);
    }

    public String getRiskCategory(double score) {
        if (score < 30) return "LOW";
        else if (score < 60) return "MEDIUM";
        else if (score < 80) return "HIGH";
        else return "CRITICAL";
    }
}
