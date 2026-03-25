package com.example.service;

import com.example.enums.PaymentMethod;
import com.example.model.Payment;

public class PaymentService {

    public Payment processPayment(Payment payment) {
        if (payment.getAmount() <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0");
        }
        PaymentMethod method = payment.getMethod();
        boolean success;
        switch (method) {
            case CREDIT_CARD -> success = processCreditCard(payment);
            case BANK_TRANSFER -> success = processBankTransfer(payment);
            case DEBIT_CARD, PAYPAL, CRYPTOCURRENCY -> success = true;
            case CASH -> success = false;
            default -> success = false;
        }
        payment.setSuccessful(success);
        return payment;
    }

    private boolean processCreditCard(Payment payment) {
        return payment.getTransactionId() != null && !payment.getTransactionId().isEmpty();
    }

    private boolean processBankTransfer(Payment payment) {
        return payment.getInvoice() != null && payment.getInvoice().getOrder() != null;
    }

    public void refundPayment(Payment payment, double refundAmount) {
        if (refundAmount > payment.getAmount()) {
            throw new IllegalArgumentException("Refund amount cannot exceed original payment amount");
        }
        payment.setSuccessful(false);
    }

    public boolean validatePaymentMethod(PaymentMethod method) {
        return method != PaymentMethod.CASH;
    }
}
