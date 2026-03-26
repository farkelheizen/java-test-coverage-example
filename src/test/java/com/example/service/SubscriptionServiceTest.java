package com.example.service;

import com.example.model.Customer;
import com.example.model.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionServiceTest {

    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionService();
    }

    private Customer buildCustomer() {
        Customer customer = new Customer();
        customer.setCustomerId("C1");
        return customer;
    }

    /** Active, non-expired subscription (end date 1 year in the future). */
    private Subscription buildActiveSubscription() {
        return new Subscription("S1", buildCustomer(), "BASIC",
                LocalDate.now(), LocalDate.now().plusYears(1), 9.99, true);
    }

    /** Inactive (cancelled) subscription. */
    private Subscription buildCancelledSubscription() {
        return new Subscription("S2", buildCustomer(), "BASIC",
                LocalDate.now().minusMonths(6), LocalDate.now().minusDays(1), 9.99, false);
    }

    /** Active but already past end date (expired). */
    private Subscription buildExpiredActiveSubscription() {
        return new Subscription("S3", buildCustomer(), "BASIC",
                LocalDate.now().minusYears(2), LocalDate.now().minusDays(1), 9.99, true);
    }

    // ── subscribe ────────────────────────────────────────────────────────────

    @Test
    void subscribe_createsActiveSubscription() {
        Subscription sub = subscriptionService.subscribe(buildCustomer(), "PREMIUM", 19.99);
        assertTrue(sub.isActive());
    }

    @Test
    void subscribe_setsCorrectPlanName() {
        Subscription sub = subscriptionService.subscribe(buildCustomer(), "PREMIUM", 19.99);
        assertEquals("PREMIUM", sub.getPlanName());
    }

    @Test
    void subscribe_setsCorrectMonthlyPrice() {
        Subscription sub = subscriptionService.subscribe(buildCustomer(), "BASIC", 9.99);
        assertEquals(9.99, sub.getMonthlyPrice(), 0.0001);
    }

    @Test
    void subscribe_startDateIsToday() {
        Subscription sub = subscriptionService.subscribe(buildCustomer(), "BASIC", 9.99);
        assertEquals(LocalDate.now(), sub.getStartDate());
    }

    @Test
    void subscribe_endDateIsOneYearFromNow() {
        Subscription sub = subscriptionService.subscribe(buildCustomer(), "BASIC", 9.99);
        assertEquals(LocalDate.now().plusYears(1), sub.getEndDate());
    }

    @Test
    void subscribe_setsCustomer() {
        Customer customer = buildCustomer();
        Subscription sub = subscriptionService.subscribe(customer, "BASIC", 9.99);
        assertSame(customer, sub.getCustomer());
    }

    // ── cancelSubscription ────────────────────────────────────────────────────

    @Test
    void cancelSubscription_activeSub_setsActiveFalse() {
        Subscription sub = buildActiveSubscription();
        subscriptionService.cancelSubscription(sub);
        assertFalse(sub.isActive());
    }

    @Test
    void cancelSubscription_activeSub_setsEndDateToToday() {
        Subscription sub = buildActiveSubscription();
        subscriptionService.cancelSubscription(sub);
        assertEquals(LocalDate.now(), sub.getEndDate());
    }

    @Test
    void cancelSubscription_expiredSubscription_throwsIllegalStateException() {
        Subscription sub = buildExpiredActiveSubscription();
        assertThrows(IllegalStateException.class,
                () -> subscriptionService.cancelSubscription(sub));
    }

    // ── renewSubscription ─────────────────────────────────────────────────────

    @Test
    void renewSubscription_validMonths_extendsEndDate() {
        Subscription sub = buildActiveSubscription();
        LocalDate originalEnd = sub.getEndDate();
        subscriptionService.renewSubscription(sub, 3);
        assertEquals(originalEnd.plusMonths(3), sub.getEndDate());
    }

    @Test
    void renewSubscription_setsActiveTrue() {
        Subscription sub = buildCancelledSubscription();
        sub.setEndDate(LocalDate.now().plusDays(1)); // prevent expired exception in cancel
        subscriptionService.renewSubscription(sub, 1);
        assertTrue(sub.isActive());
    }

    @Test
    void renewSubscription_zeroMonths_throwsIllegalArgumentException() {
        Subscription sub = buildActiveSubscription();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> subscriptionService.renewSubscription(sub, 0));
        assertTrue(ex.getMessage().contains("positive"));
    }

    @Test
    void renewSubscription_negativeMonths_throwsIllegalArgumentException() {
        Subscription sub = buildActiveSubscription();
        assertThrows(IllegalArgumentException.class,
                () -> subscriptionService.renewSubscription(sub, -2));
    }

    // ── getSubscriptionStatus ─────────────────────────────────────────────────

    @Test
    void getSubscriptionStatus_activeNonExpired_returnsACTIVE() {
        Subscription sub = buildActiveSubscription();
        assertEquals("ACTIVE", subscriptionService.getSubscriptionStatus(sub));
    }

    @Test
    void getSubscriptionStatus_inactiveSubscription_returnsCANCELLED() {
        Subscription sub = buildCancelledSubscription();
        assertEquals("CANCELLED", subscriptionService.getSubscriptionStatus(sub));
    }

    @Test
    void getSubscriptionStatus_activeButExpired_returnsEXPIRED() {
        Subscription sub = buildExpiredActiveSubscription();
        assertEquals("EXPIRED", subscriptionService.getSubscriptionStatus(sub));
    }
}
