package com.example.service;

public class ValidationService {

    public boolean validateEmail(String email) {
        if (email == null || email.length() < 3 || email.length() > 254) {
            return false;
        }
        int atIndex = email.indexOf('@');
        if (atIndex < 1) {
            return false;
        }
        String domain = email.substring(atIndex + 1);
        return domain.contains(".");
    }

    public boolean validatePhoneNumber(String phone) {
        if (phone == null) return false;
        String cleaned = phone.replaceAll("[+\\s\\-]", "");
        if (!cleaned.matches("\\d+")) return false;
        return cleaned.length() >= 10 && cleaned.length() <= 15;
    }

    public boolean validateZipCode(String zipCode, String country) {
        if (zipCode == null || country == null) return false;
        return switch (country.toUpperCase()) {
            case "US" -> zipCode.matches("\\d{5}(-\\d{4})?");
            case "CA" -> zipCode.matches("[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d");
            case "UK", "GB" -> zipCode.matches("[A-Za-z]{1,2}\\d[A-Za-z\\d]?\\s*\\d[A-Za-z]{2}");
            default -> zipCode.length() >= 3 && zipCode.length() <= 10;
        };
    }

    public boolean validatePassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
