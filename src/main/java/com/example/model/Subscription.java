package com.example.model;

import java.time.LocalDate;

public class Subscription {
    private String subscriptionId;
    private Customer customer;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private double monthlyPrice;
    private boolean isActive;

    public Subscription() {}

    public Subscription(String subscriptionId, Customer customer, String planName,
                        LocalDate startDate, LocalDate endDate, double monthlyPrice, boolean isActive) {
        this.subscriptionId = subscriptionId;
        this.customer = customer;
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthlyPrice = monthlyPrice;
        this.isActive = isActive;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate);
    }

    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public double getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(double monthlyPrice) { this.monthlyPrice = monthlyPrice; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
