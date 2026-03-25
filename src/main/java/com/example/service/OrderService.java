package com.example.service;

import com.example.enums.OrderStatus;
import com.example.model.Order;

public class OrderService {

    public Order placeOrder(Order order) {
        if (order.getCustomer() == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        if (order.getPaymentMethod() != null) {
            order.setStatus(OrderStatus.PROCESSING);
        } else {
            order.setStatus(OrderStatus.CANCELLED);
        }
        return order;
    }

    public Order cancelOrder(Order order) {
        if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PROCESSING) {
            order.setStatus(OrderStatus.CANCELLED);
        } else {
            throw new IllegalStateException("Cannot cancel order in status: " + order.getStatus());
        }
        return order;
    }

    public double calculateTotal(Order order) {
        double subtotal = order.getTotalAmount();
        double tax = subtotal * 0.08;
        double shipping = subtotal < 50.0 ? 9.99 : 0.0;
        return subtotal + tax + shipping;
    }
}
