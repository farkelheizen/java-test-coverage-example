package com.example.util;

import java.util.Map;
import java.util.Set;

public class CurrencyUtils {

    private static final Map<String, Double> RATES_TO_USD = Map.of(
            "USD", 1.0,
            "EUR", 1.08,
            "GBP", 1.27,
            "JPY", 0.0067
    );

    private static final Map<String, String> SYMBOLS = Map.of(
            "USD", "$", "EUR", "€", "GBP", "£", "JPY", "¥"
    );

    private static final Set<String> VALID_CODES = Set.of("USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF");

    public static double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        Double fromRate = RATES_TO_USD.getOrDefault(fromCurrency.toUpperCase(), 1.0);
        Double toRate = RATES_TO_USD.getOrDefault(toCurrency.toUpperCase(), 1.0);
        double amountInUsd = amount * fromRate;
        return amountInUsd / toRate;
    }

    public static String formatWithSymbol(double amount, String currency) {
        String symbol = SYMBOLS.getOrDefault(currency.toUpperCase(), currency + " ");
        return symbol + String.format("%.2f", amount);
    }

    public static boolean isValidCurrencyCode(String code) {
        return code != null && VALID_CODES.contains(code.toUpperCase());
    }

    public static int getRoundingPrecision(String currency) {
        if ("JPY".equalsIgnoreCase(currency)) return 0;
        return 2;
    }
}
