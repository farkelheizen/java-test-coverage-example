package com.example.model;

import com.example.enums.OrderStatus;
import com.example.enums.PaymentMethod;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String orderId;
    private Customer customer;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private Address shippingAddress;
    private PaymentMethod paymentMethod;

    public Order() {}

    public Order(String orderId, Customer customer, List<OrderItem> items, OrderStatus status,
                 LocalDateTime orderDate, Address shippingAddress, PaymentMethod paymentMethod) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = items;
        this.status = status;
        this.orderDate = orderDate;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
    }

    public double getTotalAmount() {
        if (items == null) return 0.0;
        return items.stream().mapToDouble(OrderItem::getTotalPrice).sum();
    }

    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public Address getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}
