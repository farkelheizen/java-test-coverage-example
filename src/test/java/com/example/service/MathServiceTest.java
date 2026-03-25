package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MathServiceTest {

    private MathService mathService;

    @BeforeEach
    void setUp() {
        mathService = new MathService();
    }

    // ─── isPrime ──────────────────────────────────────────────────────────────

    @ParameterizedTest
    @ValueSource(ints = {0, 1, -1})
    void isPrime_lessThan2_returnsFalse(int n) {
        assertFalse(mathService.isPrime(n));
    }

    @Test
    void isPrime_2_returnsTrue() {
        assertTrue(mathService.isPrime(2));
    }

    @Test
    void isPrime_evenNumberGreaterThan2_returnsFalse() {
        assertFalse(mathService.isPrime(4));
    }

    @Test
    void isPrime_oddComposite_returnsFalse() {
        assertFalse(mathService.isPrime(9));
    }

    @ParameterizedTest
    @ValueSource(ints = {7, 13, 17})
    void isPrime_primeNumbers_returnTrue(int n) {
        assertTrue(mathService.isPrime(n));
    }

    // ─── fibonacci ────────────────────────────────────────────────────────────

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void fibonacci_zeroOrNegative_returns0(int n) {
        assertEquals(0L, mathService.fibonacci(n));
    }

    @Test
    void fibonacci_1_returns1() {
        assertEquals(1L, mathService.fibonacci(1));
    }

    @Test
    void fibonacci_2_returns1() {
        assertEquals(1L, mathService.fibonacci(2));
    }

    @Test
    void fibonacci_10_returns55() {
        assertEquals(55L, mathService.fibonacci(10));
    }

    // ─── gcd ──────────────────────────────────────────────────────────────────

    @Test
    void gcd_secondArgZero_returnsFirst() {
        assertEquals(5, mathService.gcd(5, 0));
    }

    @Test
    void gcd_firstArgZero_returnsSecond() {
        assertEquals(5, mathService.gcd(0, 5));
    }

    @Test
    void gcd_normalValues_returnsGcd() {
        assertEquals(4, mathService.gcd(12, 8));
    }

    @Test
    void gcd_anotherNormal_returnsGcd() {
        assertEquals(6, mathService.gcd(48, 18));
    }

    @Test
    void gcd_negativeInputs_returnsPositiveGcd() {
        assertEquals(4, mathService.gcd(-12, 8));
    }

    // ─── lcm ──────────────────────────────────────────────────────────────────

    @Test
    void lcm_firstArgZero_returns0() {
        assertEquals(0L, mathService.lcm(0, 5));
    }

    @Test
    void lcm_secondArgZero_returns0() {
        assertEquals(0L, mathService.lcm(4, 0));
    }

    @Test
    void lcm_normalValues_returnsLcm() {
        assertEquals(12L, mathService.lcm(4, 6));
    }

    @Test
    void lcm_coprimeValues_returnsProduct() {
        assertEquals(21L, mathService.lcm(7, 3));
    }

    // ─── factorial ────────────────────────────────────────────────────────────

    @Test
    void factorial_negativeInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> mathService.factorial(-1));
    }

    @Test
    void factorial_zero_returns1() {
        assertEquals(1L, mathService.factorial(0));
    }

    @Test
    void factorial_one_returns1() {
        assertEquals(1L, mathService.factorial(1));
    }

    @Test
    void factorial_five_returns120() {
        assertEquals(120L, mathService.factorial(5));
    }

    @Test
    void factorial_twenty_returnsCorrectValue() {
        assertEquals(2432902008176640000L, mathService.factorial(20));
    }

    @Test
    void factorial_greaterThan20_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> mathService.factorial(21));
    }

    // ─── power ────────────────────────────────────────────────────────────────

    @Test
    void power_negativeExponent_returnsReciprocal() {
        assertEquals(0.25, mathService.power(2.0, -2), 1e-9);
    }

    @Test
    void power_zeroExponent_returns1() {
        assertEquals(1.0, mathService.power(2.0, 0), 1e-9);
    }

    @Test
    void power_positiveExponent_returnsPower() {
        assertEquals(8.0, mathService.power(2.0, 3), 1e-9);
    }
}
