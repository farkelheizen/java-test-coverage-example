package com.example.service;

import com.example.model.Customer;
import com.example.model.Subscription;
import java.time.LocalDate;

public class SubscriptionService {

    public Subscription subscribe(Customer customer, String plan, double price) {
        Subscription subscription = new Subscription();
        subscription.setCustomer(customer);
        subscription.setPlanName(plan);
        subscription.setMonthlyPrice(price);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusYears(1));
        subscription.setActive(true);
        return subscription;
    }

    public void cancelSubscription(Subscription subscription) {
        if (subscription.isExpired()) {
            throw new IllegalStateException("Cannot cancel an already expired subscription");
        }
        subscription.setEndDate(LocalDate.now());
        subscription.setActive(false);
    }

    public void renewSubscription(Subscription subscription, int months) {
        if (months <= 0) {
            throw new IllegalArgumentException("Months must be positive");
        }
        subscription.setEndDate(subscription.getEndDate().plusMonths(months));
        subscription.setActive(true);
    }

    public String getSubscriptionStatus(Subscription subscription) {
        if (!subscription.isActive()) {
            return "CANCELLED";
        } else if (subscription.isExpired()) {
            return "EXPIRED";
        } else {
            return "ACTIVE";
        }
    }
}
