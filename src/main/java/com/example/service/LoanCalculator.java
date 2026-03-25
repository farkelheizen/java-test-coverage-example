package com.example.service;

public class LoanCalculator {

    public double calculateMonthlyPayment(double principal, double annualRate, int termMonths) {
        if (annualRate == 0) return principal / termMonths;
        double monthlyRate = annualRate / 12.0;
        double factor = Math.pow(1 + monthlyRate, termMonths);
        return principal * (monthlyRate * factor) / (factor - 1);
    }

    public double calculateTotalInterest(double principal, double annualRate, int termMonths) {
        double monthlyPayment = calculateMonthlyPayment(principal, annualRate, termMonths);
        double totalPaid = monthlyPayment * termMonths;
        return totalPaid - principal;
    }

    public boolean isAffordable(double monthlyPayment, double monthlyIncome) {
        return monthlyPayment <= monthlyIncome * 0.28;
    }

    public double calculateLoanAmount(double monthlyIncome, double annualRate, int termMonths, double dtiRatio) {
        double maxMonthlyPayment = monthlyIncome * dtiRatio;
        if (annualRate == 0) return maxMonthlyPayment * termMonths;
        double monthlyRate = annualRate / 12.0;
        double factor = Math.pow(1 + monthlyRate, termMonths);
        return maxMonthlyPayment * (factor - 1) / (monthlyRate * factor);
    }
}
