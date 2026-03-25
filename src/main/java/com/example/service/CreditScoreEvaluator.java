package com.example.service;

public class CreditScoreEvaluator {

    public String evaluateScore(int score) {
        if (score < 300 || score > 850) {
            return "INVALID";
        } else if (score >= 750) {
            return "EXCELLENT";
        } else if (score >= 670) {
            return "GOOD";
        } else if (score >= 580) {
            return "FAIR";
        } else {
            return "POOR";
        }
    }

    public boolean getLoanEligibility(int creditScore, double income, double requestedAmount) {
        if (creditScore < 580) return false;
        if (income <= 0) return false;
        double ratio = requestedAmount / income;
        if (creditScore >= 750) {
            return ratio <= 5.0;
        } else if (creditScore >= 670) {
            return ratio <= 3.0;
        } else {
            return ratio <= 1.5;
        }
    }

    public String calculateRiskLevel(int score, double debtToIncomeRatio) {
        if (score >= 750 && debtToIncomeRatio < 0.2) {
            return "LOW";
        } else if (score >= 670 && debtToIncomeRatio < 0.35) {
            return "MEDIUM";
        } else if (score >= 580 && debtToIncomeRatio < 0.5) {
            return "HIGH";
        } else {
            return "VERY_HIGH";
        }
    }
}
