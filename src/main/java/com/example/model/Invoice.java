package com.example.model;

import java.time.LocalDateTime;

public class Invoice {
    private String invoiceId;
    private Order order;
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private double totalAmount;
    private boolean isPaid;

    public Invoice() {}

    public Invoice(String invoiceId, Order order, LocalDateTime invoiceDate,
                   LocalDateTime dueDate, double totalAmount, boolean isPaid) {
        this.invoiceId = invoiceId;
        this.order = order;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount;
        this.isPaid = isPaid;
    }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public LocalDateTime getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDateTime invoiceDate) { this.invoiceDate = invoiceDate; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
}
