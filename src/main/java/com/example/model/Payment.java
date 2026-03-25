package com.example.model;

import com.example.enums.PaymentMethod;
import java.time.LocalDateTime;

public class Payment {
    private String paymentId;
    private Invoice invoice;
    private double amount;
    private LocalDateTime paymentDate;
    private PaymentMethod method;
    private String transactionId;
    private boolean isSuccessful;

    public Payment() {}

    public Payment(String paymentId, Invoice invoice, double amount, LocalDateTime paymentDate,
                   PaymentMethod method, String transactionId, boolean isSuccessful) {
        this.paymentId = paymentId;
        this.invoice = invoice;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.method = method;
        this.transactionId = transactionId;
        this.isSuccessful = isSuccessful;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public boolean isSuccessful() { return isSuccessful; }
    public void setSuccessful(boolean successful) { isSuccessful = successful; }
}
