package com.example.service;

import com.example.enums.Priority;
import com.example.model.Customer;
import com.example.model.Order;

public class CustomerSupportService {

    public String categorizeTicket(String description) {
        if (description == null) return "OTHER";
        String lower = description.toLowerCase();
        if (lower.contains("bill") || lower.contains("invoice") || lower.contains("charge")) {
            return "BILLING";
        } else if (lower.contains("error") || lower.contains("bug") || lower.contains("crash")
                || lower.contains("not working")) {
            return "TECHNICAL";
        } else if (lower.contains("ship") || lower.contains("delivery") || lower.contains("tracking")) {
            return "SHIPPING";
        } else if (lower.contains("return") || lower.contains("refund") || lower.contains("exchange")) {
            return "RETURNS";
        } else {
            return "OTHER";
        }
    }

    public Priority calculatePriority(Order order, Customer customer) {
        if (customer.isPremium()) {
            return Priority.HIGH;
        }
        if (order.getTotalAmount() > 500) {
            return Priority.HIGH;
        }
        if (customer.getLoyaltyPoints() > 2000) {
            return Priority.MEDIUM;
        }
        return Priority.LOW;
    }

    public int estimateResolutionTime(String category, Priority priority) {
        int baseHours = switch (category) {
            case "BILLING" -> 24;
            case "TECHNICAL" -> 48;
            case "SHIPPING" -> 12;
            case "RETURNS" -> 72;
            default -> 36;
        };
        double priorityFactor = switch (priority) {
            case CRITICAL -> 0.25;
            case HIGH -> 0.5;
            case MEDIUM -> 0.75;
            case LOW -> 1.0;
        };
        return (int) Math.ceil(baseHours * priorityFactor);
    }

    public Priority escalateTicket(String ticketId, Priority current, String reason) {
        if (reason == null || reason.isEmpty()) {
            throw new IllegalArgumentException("Escalation reason cannot be empty");
        }
        return switch (current) {
            case LOW -> Priority.MEDIUM;
            case MEDIUM -> Priority.HIGH;
            case HIGH -> Priority.CRITICAL;
            case CRITICAL -> Priority.CRITICAL;
        };
    }
}
