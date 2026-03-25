package com.example.service;

import com.example.model.Customer;

public class CustomerService {

    public Customer registerCustomer(Customer customer) {
        if (customer.getFirstName() == null || customer.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (customer.getLastName() == null || customer.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (customer.getEmail() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return customer;
    }

    public Customer upgradeToPremium(Customer customer, int loyaltyPoints) {
        if (loyaltyPoints >= 1000) {
            customer.setPremium(true);
        } else {
            throw new IllegalArgumentException("Insufficient loyalty points for premium upgrade. Required: 1000, have: " + loyaltyPoints);
        }
        return customer;
    }

    public String getLoyaltyTier(Customer customer) {
        int points = customer.getLoyaltyPoints();
        if (points >= 5000) {
            return "GOLD";
        } else if (points >= 2000) {
            return "SILVER";
        } else if (points >= 500) {
            return "BRONZE";
        } else {
            return "STANDARD";
        }
    }
}
