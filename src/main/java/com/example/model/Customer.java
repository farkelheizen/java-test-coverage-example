package com.example.model;

import java.time.LocalDate;

public class Customer extends Person {
    private String customerId;
    private int loyaltyPoints;
    private boolean isPremium;

    public Customer() {}

    public Customer(long id, String firstName, String lastName, LocalDate dateOfBirth,
                    Email email, PhoneNumber phone, Address address,
                    String customerId, int loyaltyPoints, boolean isPremium) {
        super(id, firstName, lastName, dateOfBirth, email, phone, address);
        this.customerId = customerId;
        this.loyaltyPoints = loyaltyPoints;
        this.isPremium = isPremium;
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    public boolean isPremium() { return isPremium; }
    public void setPremium(boolean premium) { isPremium = premium; }
}
