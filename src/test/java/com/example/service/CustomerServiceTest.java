package com.example.service;

import com.example.model.Customer;
import com.example.model.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService();
    }

    // --- registerCustomer ---

    @Test
    void registerCustomer_throwsWhenFirstNameIsNull() {
        Customer c = new Customer();
        c.setFirstName(null);
        c.setLastName("Doe");
        c.setEmail(new Email("john", "example.com"));
        assertThrows(IllegalArgumentException.class, () -> customerService.registerCustomer(c));
    }

    @Test
    void registerCustomer_throwsWhenFirstNameIsEmpty() {
        Customer c = new Customer();
        c.setFirstName("");
        c.setLastName("Doe");
        c.setEmail(new Email("john", "example.com"));
        assertThrows(IllegalArgumentException.class, () -> customerService.registerCustomer(c));
    }

    @Test
    void registerCustomer_throwsWhenLastNameIsNull() {
        Customer c = new Customer();
        c.setFirstName("John");
        c.setLastName(null);
        c.setEmail(new Email("john", "example.com"));
        assertThrows(IllegalArgumentException.class, () -> customerService.registerCustomer(c));
    }

    @Test
    void registerCustomer_throwsWhenLastNameIsEmpty() {
        Customer c = new Customer();
        c.setFirstName("John");
        c.setLastName("");
        c.setEmail(new Email("john", "example.com"));
        assertThrows(IllegalArgumentException.class, () -> customerService.registerCustomer(c));
    }

    @Test
    void registerCustomer_throwsWhenEmailIsNull() {
        Customer c = new Customer();
        c.setFirstName("John");
        c.setLastName("Doe");
        c.setEmail(null);
        assertThrows(IllegalArgumentException.class, () -> customerService.registerCustomer(c));
    }

    @Test
    void registerCustomer_returnsCustomerWhenValid() {
        Customer c = new Customer();
        c.setFirstName("John");
        c.setLastName("Doe");
        c.setEmail(new Email("john", "example.com"));
        Customer result = customerService.registerCustomer(c);
        assertSame(c, result);
    }

    // --- upgradeToPremium ---

    @Test
    void upgradeToPremium_setsPremiumWhenPointsAtThreshold() {
        Customer c = new Customer();
        Customer result = customerService.upgradeToPremium(c, 1000);
        assertTrue(result.isPremium());
    }

    @Test
    void upgradeToPremium_setsPremiumWhenPointsAboveThreshold() {
        Customer c = new Customer();
        Customer result = customerService.upgradeToPremium(c, 5000);
        assertTrue(result.isPremium());
    }

    @Test
    void upgradeToPremium_throwsWhenPointsBelowThreshold() {
        Customer c = new Customer();
        assertThrows(IllegalArgumentException.class,
                () -> customerService.upgradeToPremium(c, 999));
    }

    @Test
    void upgradeToPremium_throwsWhenPointsAreZero() {
        Customer c = new Customer();
        assertThrows(IllegalArgumentException.class,
                () -> customerService.upgradeToPremium(c, 0));
    }

    // --- getLoyaltyTier ---

    @Test
    void getLoyaltyTier_returnsGoldWhenPointsAtOrAbove5000() {
        Customer c = new Customer();
        c.setLoyaltyPoints(5000);
        assertEquals("GOLD", customerService.getLoyaltyTier(c));
    }

    @Test
    void getLoyaltyTier_returnsGoldWhenPointsWellAbove5000() {
        Customer c = new Customer();
        c.setLoyaltyPoints(9999);
        assertEquals("GOLD", customerService.getLoyaltyTier(c));
    }

    @Test
    void getLoyaltyTier_returnsSilverWhenPointsAtOrAbove2000() {
        Customer c = new Customer();
        c.setLoyaltyPoints(2000);
        assertEquals("SILVER", customerService.getLoyaltyTier(c));
    }

    @Test
    void getLoyaltyTier_returnsSilverJustBelow5000() {
        Customer c = new Customer();
        c.setLoyaltyPoints(4999);
        assertEquals("SILVER", customerService.getLoyaltyTier(c));
    }

    @Test
    void getLoyaltyTier_returnsBronzeWhenPointsAtOrAbove500() {
        Customer c = new Customer();
        c.setLoyaltyPoints(500);
        assertEquals("BRONZE", customerService.getLoyaltyTier(c));
    }

    @Test
    void getLoyaltyTier_returnsBronzeJustBelow2000() {
        Customer c = new Customer();
        c.setLoyaltyPoints(1999);
        assertEquals("BRONZE", customerService.getLoyaltyTier(c));
    }

    @Test
    void getLoyaltyTier_returnsStandardWhenPointsBelow500() {
        Customer c = new Customer();
        c.setLoyaltyPoints(499);
        assertEquals("STANDARD", customerService.getLoyaltyTier(c));
    }

    @Test
    void getLoyaltyTier_returnsStandardWhenPointsAreZero() {
        Customer c = new Customer();
        c.setLoyaltyPoints(0);
        assertEquals("STANDARD", customerService.getLoyaltyTier(c));
    }
}
