package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MathServiceTest {

    private MathService service;

    @BeforeEach
    void setUp() {
        service = new MathService();
    }

    // ── isPrime ──────────────────────────────────────────────────────────────

    @Test
    void isPrime_zeroReturnsFalse() {
        assertFalse(service.isPrime(0));
    }

    @Test
    void isPrime_oneReturnsFalse() {
        assertFalse(service.isPrime(1));
    }

    @Test
    void isPrime_negativeReturnsFalse() {
        assertFalse(service.isPrime(-5));
    }

    @Test
    void isPrime_twoReturnsTrue() {
        assertTrue(service.isPrime(2));
    }

    @Test
    void isPrime_evenNumberReturnsFalse() {
        assertFalse(service.isPrime(4));
    }

    @Test
    void isPrime_compositeOddReturnsFalse() {
        assertFalse(service.isPrime(9));
    }

    @Test
    void isPrime_primeReturnsTrue() {
        assertTrue(service.isPrime(7));
    }

    @Test
    void isPrime_largerPrimeReturnsTrue() {
        assertTrue(service.isPrime(97));
    }

    // ── fibonacci ────────────────────────────────────────────────────────────

    @Test
    void fibonacci_zeroReturnsZero() {
        assertEquals(0, service.fibonacci(0));
    }

    @Test
    void fibonacci_negativeReturnsZero() {
        assertEquals(0, service.fibonacci(-3));
    }

    @Test
    void fibonacci_oneReturnsOne() {
        assertEquals(1, service.fibonacci(1));
    }

    @Test
    void fibonacci_twoReturnsOne() {
        assertEquals(1, service.fibonacci(2));
    }

    @Test
    void fibonacci_tenReturnsFiftyFive() {
        assertEquals(55, service.fibonacci(10));
    }

    // ── gcd ──────────────────────────────────────────────────────────────────

    @Test
    void gcd_positivePair() {
        assertEquals(4, service.gcd(12, 8));
    }

    @Test
    void gcd_withZeroReturnsOtherValue() {
        assertEquals(5, service.gcd(0, 5));
    }

    @Test
    void gcd_negativeValuesHandledByAbs() {
        assertEquals(4, service.gcd(-12, 8));
    }

    @Test
    void gcd_coprimesReturnOne() {
        assertEquals(1, service.gcd(7, 13));
    }

    // ── lcm ──────────────────────────────────────────────────────────────────

    @Test
    void lcm_eitherArgZeroReturnsZero() {
        assertEquals(0, service.lcm(0, 5));
    }

    @Test
    void lcm_otherArgZeroReturnsZero() {
        assertEquals(0, service.lcm(5, 0));
    }

    @Test
    void lcm_twoPairs() {
        assertEquals(12, service.lcm(4, 6));
    }

    @Test
    void lcm_negativeValuesHandled() {
        assertEquals(12, service.lcm(-4, 6));
    }

    // ── factorial ────────────────────────────────────────────────────────────

    @Test
    void factorial_negativeThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> service.factorial(-1));
    }

    @Test
    void factorial_greaterThan20ThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> service.factorial(21));
    }

    @Test
    void factorial_zeroReturnsOne() {
        assertEquals(1L, service.factorial(0));
    }

    @Test
    void factorial_oneReturnsOne() {
        assertEquals(1L, service.factorial(1));
    }

    @Test
    void factorial_fiveReturns120() {
        assertEquals(120L, service.factorial(5));
    }

    @Test
    void factorial_twentyReturnsCorrectValue() {
        assertEquals(2432902008176640000L, service.factorial(20));
    }

    // ── power ────────────────────────────────────────────────────────────────

    @Test
    void power_positiveExponent() {
        assertEquals(8.0, service.power(2.0, 3), 1e-9);
    }

    @Test
    void power_zeroExponentReturnsOne() {
        assertEquals(1.0, service.power(2.0, 0), 1e-9);
    }

    @Test
    void power_negativeExponentReturnsReciprocal() {
        assertEquals(0.25, service.power(2.0, -2), 1e-9);
    }

    @Test
    void power_baseOneAlwaysOne() {
        assertEquals(1.0, service.power(1.0, 100), 1e-9);
    }
}
