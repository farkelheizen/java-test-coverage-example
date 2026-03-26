package com.example.service;

import com.example.enums.ProductCategory;
import com.example.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaxCalculatorTest {

    private TaxCalculator taxCalculator;

    @BeforeEach
    void setUp() {
        taxCalculator = new TaxCalculator();
    }

    // ── calculateTax ──────────────────────────────────────────────────────────

    @Test
    void calculateTax_California_appliesSevenPointTwoFivePercent() {
        double tax = taxCalculator.calculateTax(100.0, "CA");
        assertEquals(7.25, tax, 0.0001);
    }

    @Test
    void calculateTax_CaliforniaLowerCase_appliesCorrectRate() {
        double tax = taxCalculator.calculateTax(100.0, "ca");
        assertEquals(7.25, tax, 0.0001);
    }

    @Test
    void calculateTax_NewYork_appliesEightPercent() {
        double tax = taxCalculator.calculateTax(200.0, "NY");
        assertEquals(16.0, tax, 0.0001);
    }

    @Test
    void calculateTax_Texas_appliesSixPointTwoFivePercent() {
        double tax = taxCalculator.calculateTax(100.0, "TX");
        assertEquals(6.25, tax, 0.0001);
    }

    @Test
    void calculateTax_Florida_appliesSixPercent() {
        double tax = taxCalculator.calculateTax(100.0, "FL");
        assertEquals(6.0, tax, 0.0001);
    }

    @Test
    void calculateTax_unknownState_appliesFivePercentDefault() {
        double tax = taxCalculator.calculateTax(100.0, "OH");
        assertEquals(5.0, tax, 0.0001);
    }

    @Test
    void calculateTax_zeroAmount_returnsZero() {
        double tax = taxCalculator.calculateTax(0.0, "CA");
        assertEquals(0.0, tax, 0.0001);
    }

    // ── calculateVAT ──────────────────────────────────────────────────────────

    @Test
    void calculateVAT_UK_appliesTwentyPercent() {
        double vat = taxCalculator.calculateVAT(100.0, "UK");
        assertEquals(20.0, vat, 0.0001);
    }

    @Test
    void calculateVAT_GB_appliesTwentyPercent() {
        double vat = taxCalculator.calculateVAT(100.0, "GB");
        assertEquals(20.0, vat, 0.0001);
    }

    @Test
    void calculateVAT_Germany_appliesNineteenPercent() {
        double vat = taxCalculator.calculateVAT(100.0, "DE");
        assertEquals(19.0, vat, 0.0001);
    }

    @Test
    void calculateVAT_France_appliesTwentyPercent() {
        double vat = taxCalculator.calculateVAT(100.0, "FR");
        assertEquals(20.0, vat, 0.0001);
    }

    @Test
    void calculateVAT_US_appliesZeroPercent() {
        double vat = taxCalculator.calculateVAT(100.0, "US");
        assertEquals(0.0, vat, 0.0001);
    }

    @Test
    void calculateVAT_unknownCountry_appliesFifteenPercentDefault() {
        double vat = taxCalculator.calculateVAT(100.0, "JP");
        assertEquals(15.0, vat, 0.0001);
    }

    @Test
    void calculateVAT_lowercaseCountryCode_normalised() {
        double vat = taxCalculator.calculateVAT(100.0, "uk");
        assertEquals(20.0, vat, 0.0001);
    }

    // ── isTaxExempt ───────────────────────────────────────────────────────────

    @Test
    void isTaxExempt_foodProduct_returnsTrue() {
        Product product = new Product("1", "Apple", "", 1.0, 10, ProductCategory.FOOD, "SKU1");
        assertTrue(taxCalculator.isTaxExempt(product));
    }

    @Test
    void isTaxExempt_electronicsProduct_returnsFalse() {
        Product product = new Product("2", "Phone", "", 500.0, 5, ProductCategory.ELECTRONICS, "SKU2");
        assertFalse(taxCalculator.isTaxExempt(product));
    }

    @Test
    void isTaxExempt_clothingProduct_returnsFalse() {
        Product product = new Product("3", "Shirt", "", 30.0, 20, ProductCategory.CLOTHING, "SKU3");
        assertFalse(taxCalculator.isTaxExempt(product));
    }

    // ── calculateTotalWithTax ─────────────────────────────────────────────────

    @Test
    void calculateTotalWithTax_CAandUS_addsOnlyStateTax() {
        // CA=7.25%, US=0% VAT  =>  100 + 7.25 + 0 = 107.25
        double total = taxCalculator.calculateTotalWithTax(100.0, "CA", "US");
        assertEquals(107.25, total, 0.0001);
    }

    @Test
    void calculateTotalWithTax_NYandUK_addsBothTaxes() {
        // NY=8%, UK=20%  =>  100 + 8 + 20 = 128.0
        double total = taxCalculator.calculateTotalWithTax(100.0, "NY", "UK");
        assertEquals(128.0, total, 0.0001);
    }

    @Test
    void calculateTotalWithTax_defaultStateAndCountry_appliesDefaultRates() {
        // OH=5%, JP=15%  =>  100 + 5 + 15 = 120.0
        double total = taxCalculator.calculateTotalWithTax(100.0, "OH", "JP");
        assertEquals(120.0, total, 0.0001);
    }
}
