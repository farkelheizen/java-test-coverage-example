package com.example.util;

public class FormatUtils {

    public static String formatCurrency(double amount, String currencyCode) {
        String symbol = switch (currencyCode.toUpperCase()) {
            case "USD" -> "$";
            case "EUR" -> "€";
            case "GBP" -> "£";
            case "JPY" -> "¥";
            default -> currencyCode + " ";
        };
        return symbol + String.format("%.2f", amount);
    }

    public static String formatPercentage(double value, int decimals) {
        return String.format("%." + decimals + "f%%", value);
    }

    public static String formatPhoneNumber(String digits) {
        if (digits == null) return null;
        String cleaned = digits.replaceAll("[^0-9]", "");
        if (cleaned.length() == 10) {
            return String.format("(%s) %s-%s",
                    cleaned.substring(0, 3),
                    cleaned.substring(3, 6),
                    cleaned.substring(6));
        }
        return digits;
    }

    public static String formatCreditCard(String number) {
        if (number == null || number.length() < 4) return number;
        String last4 = number.substring(number.length() - 4);
        return "*".repeat(number.length() - 4) + last4;
    }
}
