package com.example.service;

import com.example.enums.Priority;
import com.example.model.Customer;
import com.example.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerSupportServiceTest {

    @Mock
    private Order order;

    @Mock
    private Customer customer;

    @InjectMocks
    private CustomerSupportService service;

    // ---- categorizeTicket ----

    @Test
    void categorizeTicket_nullDescription_returnsOther() {
        assertEquals("OTHER", service.categorizeTicket(null));
    }

    @Test
    void categorizeTicket_containsBill_returnsBilling() {
        assertEquals("BILLING", service.categorizeTicket("I have a billing question"));
    }

    @Test
    void categorizeTicket_containsInvoice_returnsBilling() {
        assertEquals("BILLING", service.categorizeTicket("My invoice is wrong"));
    }

    @Test
    void categorizeTicket_containsCharge_returnsBilling() {
        assertEquals("BILLING", service.categorizeTicket("Unexpected charge on my account"));
    }

    @Test
    void categorizeTicket_containsError_returnsTechnical() {
        assertEquals("TECHNICAL", service.categorizeTicket("There is an error in the app"));
    }

    @Test
    void categorizeTicket_containsBug_returnsTechnical() {
        assertEquals("TECHNICAL", service.categorizeTicket("Found a bug in the system"));
    }

    @Test
    void categorizeTicket_containsCrash_returnsTechnical() {
        assertEquals("TECHNICAL", service.categorizeTicket("App crash on startup"));
    }

    @Test
    void categorizeTicket_containsNotWorking_returnsTechnical() {
        assertEquals("TECHNICAL", service.categorizeTicket("Feature is not working"));
    }

    @Test
    void categorizeTicket_containsShip_returnsShipping() {
        assertEquals("SHIPPING", service.categorizeTicket("When will my shipment arrive?"));
    }

    @Test
    void categorizeTicket_containsDelivery_returnsShipping() {
        assertEquals("SHIPPING", service.categorizeTicket("Delivery is late"));
    }

    @Test
    void categorizeTicket_containsTracking_returnsShipping() {
        assertEquals("SHIPPING", service.categorizeTicket("I need a tracking number"));
    }

    @Test
    void categorizeTicket_containsReturn_returnsReturns() {
        assertEquals("RETURNS", service.categorizeTicket("I want to return this item"));
    }

    @Test
    void categorizeTicket_containsRefund_returnsReturns() {
        assertEquals("RETURNS", service.categorizeTicket("Please issue a refund"));
    }

    @Test
    void categorizeTicket_containsExchange_returnsReturns() {
        assertEquals("RETURNS", service.categorizeTicket("I want to exchange my product"));
    }

    @Test
    void categorizeTicket_noKeywordMatch_returnsOther() {
        assertEquals("OTHER", service.categorizeTicket("General inquiry about the service"));
    }

    // ---- calculatePriority ----

    @Test
    void calculatePriority_premiumCustomer_returnsHigh() {
        when(customer.isPremium()).thenReturn(true);
        assertEquals(Priority.HIGH, service.calculatePriority(order, customer));
    }

    @Test
    void calculatePriority_notPremiumHighOrderAmount_returnsHigh() {
        when(customer.isPremium()).thenReturn(false);
        when(order.getTotalAmount()).thenReturn(600.0);
        assertEquals(Priority.HIGH, service.calculatePriority(order, customer));
    }

    @Test
    void calculatePriority_notPremiumLowAmountHighLoyalty_returnsMedium() {
        when(customer.isPremium()).thenReturn(false);
        when(order.getTotalAmount()).thenReturn(100.0);
        when(customer.getLoyaltyPoints()).thenReturn(3000);
        assertEquals(Priority.MEDIUM, service.calculatePriority(order, customer));
    }

    @Test
    void calculatePriority_notPremiumLowAmountLowLoyalty_returnsLow() {
        when(customer.isPremium()).thenReturn(false);
        when(order.getTotalAmount()).thenReturn(100.0);
        when(customer.getLoyaltyPoints()).thenReturn(500);
        assertEquals(Priority.LOW, service.calculatePriority(order, customer));
    }

    // ---- estimateResolutionTime ----

    @Test
    void estimateResolutionTime_billingCategoryHighPriority() {
        // base=24, factor=0.5 -> ceil(12) = 12
        assertEquals(12, service.estimateResolutionTime("BILLING", Priority.HIGH));
    }

    @Test
    void estimateResolutionTime_technicalCategoryCriticalPriority() {
        // base=48, factor=0.25 -> ceil(12) = 12
        assertEquals(12, service.estimateResolutionTime("TECHNICAL", Priority.CRITICAL));
    }

    @Test
    void estimateResolutionTime_shippingCategoryMediumPriority() {
        // base=12, factor=0.75 -> ceil(9) = 9
        assertEquals(9, service.estimateResolutionTime("SHIPPING", Priority.MEDIUM));
    }

    @Test
    void estimateResolutionTime_returnsCategoryLowPriority() {
        // base=72, factor=1.0 -> 72
        assertEquals(72, service.estimateResolutionTime("RETURNS", Priority.LOW));
    }

    @Test
    void estimateResolutionTime_defaultCategoryHighPriority() {
        // base=36, factor=0.5 -> 18
        assertEquals(18, service.estimateResolutionTime("OTHER", Priority.HIGH));
    }

    @Test
    void estimateResolutionTime_billingCategoryLowPriority() {
        // base=24, factor=1.0 -> 24
        assertEquals(24, service.estimateResolutionTime("BILLING", Priority.LOW));
    }

    @Test
    void estimateResolutionTime_technicalCategoryMediumPriority() {
        // base=48, factor=0.75 -> 36
        assertEquals(36, service.estimateResolutionTime("TECHNICAL", Priority.MEDIUM));
    }

    @Test
    void estimateResolutionTime_defaultCategoryCriticalPriority() {
        // base=36, factor=0.25 -> ceil(9) = 9
        assertEquals(9, service.estimateResolutionTime("OTHER", Priority.CRITICAL));
    }

    @Test
    void estimateResolutionTime_shippingCategoryLowPriority() {
        // base=12, factor=1.0 -> 12
        assertEquals(12, service.estimateResolutionTime("SHIPPING", Priority.LOW));
    }

    @Test
    void estimateResolutionTime_returnsCategoryHighPriority() {
        // base=72, factor=0.5 -> 36
        assertEquals(36, service.estimateResolutionTime("RETURNS", Priority.HIGH));
    }

    // ---- escalateTicket ----

    @Test
    void escalateTicket_nullReason_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.escalateTicket("T1", Priority.LOW, null));
    }

    @Test
    void escalateTicket_emptyReason_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.escalateTicket("T1", Priority.LOW, ""));
    }

    @Test
    void escalateTicket_lowPriority_returnsMedium() {
        assertEquals(Priority.MEDIUM, service.escalateTicket("T1", Priority.LOW, "Urgent issue"));
    }

    @Test
    void escalateTicket_mediumPriority_returnsHigh() {
        assertEquals(Priority.HIGH, service.escalateTicket("T1", Priority.MEDIUM, "Escalating"));
    }

    @Test
    void escalateTicket_highPriority_returnsCritical() {
        assertEquals(Priority.CRITICAL, service.escalateTicket("T1", Priority.HIGH, "Critical matter"));
    }

    @Test
    void escalateTicket_criticalPriority_remainsCritical() {
        assertEquals(Priority.CRITICAL, service.escalateTicket("T1", Priority.CRITICAL, "Already critical"));
    }
}
