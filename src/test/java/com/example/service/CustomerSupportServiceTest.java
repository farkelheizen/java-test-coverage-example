package com.example.service;

import com.example.enums.Priority;
import com.example.model.Customer;
import com.example.model.Order;
import com.example.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerSupportServiceTest {

    private CustomerSupportService customerSupportService;

    @BeforeEach
    void setUp() {
        customerSupportService = new CustomerSupportService();
    }

    // --- categorizeTicket ---

    @Test
    void categorizeTicket_returnsOtherWhenDescriptionIsNull() {
        assertEquals("OTHER", customerSupportService.categorizeTicket(null));
    }

    @Test
    void categorizeTicket_returnsBillingForBillKeyword() {
        assertEquals("BILLING", customerSupportService.categorizeTicket("I have a bill question"));
    }

    @Test
    void categorizeTicket_returnsBillingForInvoiceKeyword() {
        assertEquals("BILLING", customerSupportService.categorizeTicket("My invoice is wrong"));
    }

    @Test
    void categorizeTicket_returnsBillingForChargeKeyword() {
        assertEquals("BILLING", customerSupportService.categorizeTicket("I was charged twice"));
    }

    @Test
    void categorizeTicket_returnsTechnicalForErrorKeyword() {
        assertEquals("TECHNICAL", customerSupportService.categorizeTicket("There is an error on the page"));
    }

    @Test
    void categorizeTicket_returnsTechnicalForBugKeyword() {
        assertEquals("TECHNICAL", customerSupportService.categorizeTicket("Found a bug in the app"));
    }

    @Test
    void categorizeTicket_returnsTechnicalForCrashKeyword() {
        assertEquals("TECHNICAL", customerSupportService.categorizeTicket("The app crash constantly"));
    }

    @Test
    void categorizeTicket_returnsTechnicalForNotWorkingKeyword() {
        assertEquals("TECHNICAL", customerSupportService.categorizeTicket("Login is not working"));
    }

    @Test
    void categorizeTicket_returnsShippingForShipKeyword() {
        assertEquals("SHIPPING", customerSupportService.categorizeTicket("When will my shipment arrive?"));
    }

    @Test
    void categorizeTicket_returnsShippingForDeliveryKeyword() {
        assertEquals("SHIPPING", customerSupportService.categorizeTicket("Delivery is late"));
    }

    @Test
    void categorizeTicket_returnsShippingForTrackingKeyword() {
        assertEquals("SHIPPING", customerSupportService.categorizeTicket("I need the tracking number"));
    }

    @Test
    void categorizeTicket_returnsReturnsForReturnKeyword() {
        assertEquals("RETURNS", customerSupportService.categorizeTicket("I want to return my order"));
    }

    @Test
    void categorizeTicket_returnsReturnsForRefundKeyword() {
        assertEquals("RETURNS", customerSupportService.categorizeTicket("Please refund my purchase"));
    }

    @Test
    void categorizeTicket_returnsReturnsForExchangeKeyword() {
        assertEquals("RETURNS", customerSupportService.categorizeTicket("I want an exchange"));
    }

    @Test
    void categorizeTicket_returnsOtherForUnknownDescription() {
        assertEquals("OTHER", customerSupportService.categorizeTicket("General inquiry about the product"));
    }

    @Test
    void categorizeTicket_isCaseInsensitive() {
        assertEquals("BILLING", customerSupportService.categorizeTicket("INVOICE ISSUE"));
    }

    // --- calculatePriority ---

    @Test
    void calculatePriority_returnsHighForPremiumCustomer() {
        Customer customer = new Customer();
        customer.setPremium(true);
        Order order = new Order();
        assertEquals(Priority.HIGH, customerSupportService.calculatePriority(order, customer));
    }

    @Test
    void calculatePriority_returnsHighForOrderOver500() {
        Customer customer = new Customer();
        customer.setPremium(false);
        customer.setLoyaltyPoints(0);
        Order order = new Order();
        OrderItem item = new OrderItem("i1", null, 1, 501.0);
        order.setItems(List.of(item));
        assertEquals(Priority.HIGH, customerSupportService.calculatePriority(order, customer));
    }

    @Test
    void calculatePriority_returnsMediumForLoyaltyOver2000() {
        Customer customer = new Customer();
        customer.setPremium(false);
        customer.setLoyaltyPoints(2001);
        Order order = new Order();
        OrderItem item = new OrderItem("i1", null, 1, 100.0);
        order.setItems(List.of(item));
        assertEquals(Priority.MEDIUM, customerSupportService.calculatePriority(order, customer));
    }

    @Test
    void calculatePriority_returnsLowForStandardCustomer() {
        Customer customer = new Customer();
        customer.setPremium(false);
        customer.setLoyaltyPoints(100);
        Order order = new Order();
        OrderItem item = new OrderItem("i1", null, 1, 50.0);
        order.setItems(List.of(item));
        assertEquals(Priority.LOW, customerSupportService.calculatePriority(order, customer));
    }

    // --- estimateResolutionTime ---

    @Test
    void estimateResolutionTime_billingWithCritical() {
        // base=24, factor=0.25 => ceil(6) = 6
        assertEquals(6, customerSupportService.estimateResolutionTime("BILLING", Priority.CRITICAL));
    }

    @Test
    void estimateResolutionTime_billingWithHigh() {
        // base=24, factor=0.5 => ceil(12) = 12
        assertEquals(12, customerSupportService.estimateResolutionTime("BILLING", Priority.HIGH));
    }

    @Test
    void estimateResolutionTime_billingWithMedium() {
        // base=24, factor=0.75 => ceil(18) = 18
        assertEquals(18, customerSupportService.estimateResolutionTime("BILLING", Priority.MEDIUM));
    }

    @Test
    void estimateResolutionTime_billingWithLow() {
        // base=24, factor=1.0 => 24
        assertEquals(24, customerSupportService.estimateResolutionTime("BILLING", Priority.LOW));
    }

    @Test
    void estimateResolutionTime_technicalWithHigh() {
        // base=48, factor=0.5 => 24
        assertEquals(24, customerSupportService.estimateResolutionTime("TECHNICAL", Priority.HIGH));
    }

    @Test
    void estimateResolutionTime_technicalWithLow() {
        // base=48, factor=1.0 => 48
        assertEquals(48, customerSupportService.estimateResolutionTime("TECHNICAL", Priority.LOW));
    }

    @Test
    void estimateResolutionTime_shippingWithCritical() {
        // base=12, factor=0.25 => ceil(3) = 3
        assertEquals(3, customerSupportService.estimateResolutionTime("SHIPPING", Priority.CRITICAL));
    }

    @Test
    void estimateResolutionTime_shippingWithLow() {
        // base=12, factor=1.0 => 12
        assertEquals(12, customerSupportService.estimateResolutionTime("SHIPPING", Priority.LOW));
    }

    @Test
    void estimateResolutionTime_returnsWithCritical() {
        // base=72, factor=0.25 => ceil(18) = 18
        assertEquals(18, customerSupportService.estimateResolutionTime("RETURNS", Priority.CRITICAL));
    }

    @Test
    void estimateResolutionTime_returnsWithLow() {
        // base=72, factor=1.0 => 72
        assertEquals(72, customerSupportService.estimateResolutionTime("RETURNS", Priority.LOW));
    }

    @Test
    void estimateResolutionTime_defaultCategoryWithMedium() {
        // base=36, factor=0.75 => ceil(27) = 27
        assertEquals(27, customerSupportService.estimateResolutionTime("UNKNOWN", Priority.MEDIUM));
    }

    @Test
    void estimateResolutionTime_defaultCategoryWithLow() {
        // base=36, factor=1.0 => 36
        assertEquals(36, customerSupportService.estimateResolutionTime("OTHER", Priority.LOW));
    }

    // --- escalateTicket ---

    @Test
    void escalateTicket_throwsWhenReasonIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> customerSupportService.escalateTicket("T1", Priority.LOW, null));
    }

    @Test
    void escalateTicket_throwsWhenReasonIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> customerSupportService.escalateTicket("T1", Priority.LOW, ""));
    }

    @Test
    void escalateTicket_escalatesFromLowToMedium() {
        assertEquals(Priority.MEDIUM,
                customerSupportService.escalateTicket("T1", Priority.LOW, "Customer request"));
    }

    @Test
    void escalateTicket_escalatesFromMediumToHigh() {
        assertEquals(Priority.HIGH,
                customerSupportService.escalateTicket("T1", Priority.MEDIUM, "Urgent issue"));
    }

    @Test
    void escalateTicket_escalatesFromHighToCritical() {
        assertEquals(Priority.CRITICAL,
                customerSupportService.escalateTicket("T1", Priority.HIGH, "Business impact"));
    }

    @Test
    void escalateTicket_staysAtCriticalWhenAlreadyCritical() {
        assertEquals(Priority.CRITICAL,
                customerSupportService.escalateTicket("T1", Priority.CRITICAL, "Already critical"));
    }
}
