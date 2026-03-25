package com.example.service;

import com.example.model.Invoice;
import com.example.model.Order;
import java.time.LocalDateTime;

public class BillingService {

    public Invoice generateInvoice(Order order) {
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setDueDate(LocalDateTime.now().plusDays(30));
        invoice.setTotalAmount(order.getTotalAmount());
        invoice.setPaid(false);
        return invoice;
    }

    public void processInvoicePayment(Invoice invoice, double amount) {
        if (amount < invoice.getTotalAmount()) {
            throw new IllegalArgumentException("Amount is less than invoice total");
        }
        invoice.setPaid(true);
    }

    public boolean sendReminder(Invoice invoice) {
        if (!invoice.isPaid() && LocalDateTime.now().isAfter(invoice.getDueDate())) {
            // Trigger notification (simplified)
            return true;
        }
        return false;
    }
}
