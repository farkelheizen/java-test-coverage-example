package com.example.service;

import java.util.Map;

public class DecisionTree {

    public String classify(Map<String, Object> features) {
        Object age = features.get("age");
        Object income = features.get("income");
        Object hasLoan = features.get("hasLoan");

        if (age instanceof Integer a && a < 25) {
            if (income instanceof Double inc && inc > 50000) {
                return "YOUNG_EARNER";
            }
            return "YOUNG_LIMITED";
        } else if (age instanceof Integer a && a < 45) {
            if (Boolean.TRUE.equals(hasLoan)) {
                return "MID_LEVERAGED";
            }
            return "MID_STABLE";
        } else {
            if (income instanceof Double inc && inc > 100000) {
                return "SENIOR_WEALTHY";
            }
            return "SENIOR_MODERATE";
        }
    }

    public boolean evaluate(String nodeName, Object value) {
        if (value instanceof Number n) {
            return switch (nodeName) {
                case "HIGH_INCOME" -> n.doubleValue() > 100000;
                case "LOW_INCOME" -> n.doubleValue() < 30000;
                case "ADULT" -> n.doubleValue() >= 18;
                default -> false;
            };
        } else if (value instanceof String s) {
            return switch (nodeName) {
                case "PREMIUM_CUSTOMER" -> "PREMIUM".equalsIgnoreCase(s);
                case "VERIFIED" -> "VERIFIED".equalsIgnoreCase(s);
                default -> false;
            };
        }
        return false;
    }

    public String getRecommendation(Map<String, Object> customerData) {
        String category = classify(customerData);
        return switch (category) {
            case "YOUNG_EARNER" -> "PREMIUM_SAVINGS";
            case "YOUNG_LIMITED" -> "STARTER_ACCOUNT";
            case "MID_LEVERAGED" -> "DEBT_MANAGEMENT";
            case "MID_STABLE" -> "INVESTMENT_PORTFOLIO";
            case "SENIOR_WEALTHY" -> "WEALTH_MANAGEMENT";
            default -> "STANDARD_ACCOUNT";
        };
    }
}
