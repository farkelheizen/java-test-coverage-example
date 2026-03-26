package com.example.service;

import com.example.enums.PaymentMethod;
import com.example.model.Invoice;
import com.example.model.Order;
import com.example.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService();
    }

    // --- processPayment ---

    @Test
    void processPayment_throwsWhenAmountIsZero() {
        Payment p = new Payment();
        p.setAmount(0);
        p.setMethod(PaymentMethod.CREDIT_CARD);
        assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(p));
    }

    @Test
    void processPayment_throwsWhenAmountIsNegative() {
        Payment p = new Payment();
        p.setAmount(-10.0);
        p.setMethod(PaymentMethod.CREDIT_CARD);
        assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(p));
    }

    @Test
    void processPayment_creditCard_successWhenTransactionIdPresent() {
        Payment p = new Payment();
        p.setAmount(100.0);
        p.setMethod(PaymentMethod.CREDIT_CARD);
        p.setTransactionId("TX123");
        Payment result = paymentService.processPayment(p);
        assertTrue(result.isSuccessful());
    }

    @Test
    void processPayment_creditCard_failsWhenTransactionIdIsNull() {
        Payment p = new Payment();
        p.setAmount(100.0);
        p.setMethod(PaymentMethod.CREDIT_CARD);
        p.setTransactionId(null);
        Payment result = paymentService.processPayment(p);
        assertFalse(result.isSuccessful());
    }

    @Test
    void processPayment_creditCard_failsWhenTransactionIdIsEmpty() {
        Payment p = new Payment();
        p.setAmount(100.0);
        p.setMethod(PaymentMethod.CREDIT_CARD);
        p.setTransactionId("");
        Payment result = paymentService.processPayment(p);
        assertFalse(result.isSuccessful());
    }

    @Test
    void processPayment_bankTransfer_successWhenInvoiceAndOrderPresent() {
        Order order = new Order();
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        Payment p = new Payment();
        p.setAmount(200.0);
        p.setMethod(PaymentMethod.BANK_TRANSFER);
        p.setInvoice(invoice);
        Payment result = paymentService.processPayment(p);
        assertTrue(result.isSuccessful());
    }

    @Test
    void processPayment_bankTransfer_failsWhenInvoiceIsNull() {
        Payment p = new Payment();
        p.setAmount(200.0);
        p.setMethod(PaymentMethod.BANK_TRANSFER);
        p.setInvoice(null);
        Payment result = paymentService.processPayment(p);
        assertFalse(result.isSuccessful());
    }

    @Test
    void processPayment_bankTransfer_failsWhenOrderIsNull() {
        Invoice invoice = new Invoice();
        invoice.setOrder(null);
        Payment p = new Payment();
        p.setAmount(200.0);
        p.setMethod(PaymentMethod.BANK_TRANSFER);
        p.setInvoice(invoice);
        Payment result = paymentService.processPayment(p);
        assertFalse(result.isSuccessful());
    }

    @Test
    void processPayment_debitCard_alwaysSucceeds() {
        Payment p = new Payment();
        p.setAmount(50.0);
        p.setMethod(PaymentMethod.DEBIT_CARD);
        assertTrue(paymentService.processPayment(p).isSuccessful());
    }

    @Test
    void processPayment_paypal_alwaysSucceeds() {
        Payment p = new Payment();
        p.setAmount(50.0);
        p.setMethod(PaymentMethod.PAYPAL);
        assertTrue(paymentService.processPayment(p).isSuccessful());
    }

    @Test
    void processPayment_cryptocurrency_alwaysSucceeds() {
        Payment p = new Payment();
        p.setAmount(50.0);
        p.setMethod(PaymentMethod.CRYPTOCURRENCY);
        assertTrue(paymentService.processPayment(p).isSuccessful());
    }

    @Test
    void processPayment_cash_alwaysFails() {
        Payment p = new Payment();
        p.setAmount(50.0);
        p.setMethod(PaymentMethod.CASH);
        assertFalse(paymentService.processPayment(p).isSuccessful());
    }

    @Test
    void processPayment_returnsTheSamePaymentObject() {
        Payment p = new Payment();
        p.setAmount(10.0);
        p.setMethod(PaymentMethod.PAYPAL);
        assertSame(p, paymentService.processPayment(p));
    }

    // --- refundPayment ---

    @Test
    void refundPayment_throwsWhenRefundExceedsOriginal() {
        Payment p = new Payment();
        p.setAmount(100.0);
        assertThrows(IllegalArgumentException.class, () -> paymentService.refundPayment(p, 150.0));
    }

    @Test
    void refundPayment_setsSuccessfulFalseOnValidRefund() {
        Payment p = new Payment();
        p.setAmount(100.0);
        p.setSuccessful(true);
        paymentService.refundPayment(p, 50.0);
        assertFalse(p.isSuccessful());
    }

    @Test
    void refundPayment_allowsRefundEqualToOriginal() {
        Payment p = new Payment();
        p.setAmount(100.0);
        assertDoesNotThrow(() -> paymentService.refundPayment(p, 100.0));
        assertFalse(p.isSuccessful());
    }

    // --- validatePaymentMethod ---

    @Test
    void validatePaymentMethod_returnsFalseForCash() {
        assertFalse(paymentService.validatePaymentMethod(PaymentMethod.CASH));
    }

    @Test
    void validatePaymentMethod_returnsTrueForCreditCard() {
        assertTrue(paymentService.validatePaymentMethod(PaymentMethod.CREDIT_CARD));
    }

    @Test
    void validatePaymentMethod_returnsTrueForDebitCard() {
        assertTrue(paymentService.validatePaymentMethod(PaymentMethod.DEBIT_CARD));
    }

    @Test
    void validatePaymentMethod_returnsTrueForPaypal() {
        assertTrue(paymentService.validatePaymentMethod(PaymentMethod.PAYPAL));
    }

    @Test
    void validatePaymentMethod_returnsTrueForBankTransfer() {
        assertTrue(paymentService.validatePaymentMethod(PaymentMethod.BANK_TRANSFER));
    }

    @Test
    void validatePaymentMethod_returnsTrueForCryptocurrency() {
        assertTrue(paymentService.validatePaymentMethod(PaymentMethod.CRYPTOCURRENCY));
    }
}
