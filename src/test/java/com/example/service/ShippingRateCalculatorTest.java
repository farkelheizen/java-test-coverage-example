package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShippingRateCalculatorTest {

    private ShippingRateCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ShippingRateCalculator();
    }

    // ── calculateRate ─────────────────────────────────────────────────────────

    @Test
    void calculateRate_standardService_returnsBaseRate() {
        // baseRate = 2*0.5 + 100*0.01 = 1 + 1 = 2.0  →  STANDARD x1 = 2.0
        double rate = calculator.calculateRate(2.0, 100.0, "STANDARD");
        assertEquals(2.0, rate, 0.0001);
    }

    @Test
    void calculateRate_standardServiceLowerCase_treatedAsStandard() {
        double rate = calculator.calculateRate(2.0, 100.0, "standard");
        assertEquals(2.0, rate, 0.0001);
    }

    @Test
    void calculateRate_expressService_doublesBaseRate() {
        // baseRate = 2*0.5 + 100*0.01 = 2.0  →  EXPRESS x2 = 4.0
        double rate = calculator.calculateRate(2.0, 100.0, "EXPRESS");
        assertEquals(4.0, rate, 0.0001);
    }

    @Test
    void calculateRate_expressServiceLowerCase_doublesBaseRate() {
        double rate = calculator.calculateRate(2.0, 100.0, "express");
        assertEquals(4.0, rate, 0.0001);
    }

    @Test
    void calculateRate_overnightService_triplePointFiveBaseRate() {
        // baseRate = 2*0.5 + 100*0.01 = 2.0  →  OVERNIGHT x3.5 = 7.0
        double rate = calculator.calculateRate(2.0, 100.0, "OVERNIGHT");
        assertEquals(7.0, rate, 0.0001);
    }

    @Test
    void calculateRate_overnightServiceLowerCase_triplePointFiveBaseRate() {
        double rate = calculator.calculateRate(2.0, 100.0, "overnight");
        assertEquals(7.0, rate, 0.0001);
    }

    @Test
    void calculateRate_unknownServiceLevel_returnsBaseRate() {
        // Unknown service level falls through to default → baseRate unchanged
        double rate = calculator.calculateRate(2.0, 100.0, "PRIORITY");
        assertEquals(2.0, rate, 0.0001);
    }

    @Test
    void calculateRate_zeroWeightAndDistance_returnsZero() {
        double rate = calculator.calculateRate(0.0, 0.0, "STANDARD");
        assertEquals(0.0, rate, 0.0001);
    }

    @Test
    void calculateRate_heavyPackage_expressRateCalculatedCorrectly() {
        // weight=10, dist=500  baseRate=10*0.5+500*0.01=5+5=10  EXPRESS=20
        double rate = calculator.calculateRate(10.0, 500.0, "EXPRESS");
        assertEquals(20.0, rate, 0.0001);
    }

    // ── calculateDimensionalWeight ────────────────────────────────────────────

    @Test
    void calculateDimensionalWeight_standardBox_returnsCorrectValue() {
        // 50*40*30 / 5000 = 60000/5000 = 12.0
        double dimWeight = calculator.calculateDimensionalWeight(50.0, 40.0, 30.0);
        assertEquals(12.0, dimWeight, 0.0001);
    }

    @Test
    void calculateDimensionalWeight_unitCube_returnsFifthThousandth() {
        double dimWeight = calculator.calculateDimensionalWeight(1.0, 1.0, 1.0);
        assertEquals(0.0002, dimWeight, 0.000001);
    }

    @Test
    void calculateDimensionalWeight_zeroDimension_returnsZero() {
        double dimWeight = calculator.calculateDimensionalWeight(0.0, 40.0, 30.0);
        assertEquals(0.0, dimWeight, 0.0001);
    }

    // ── getBillableWeight ─────────────────────────────────────────────────────

    @Test
    void getBillableWeight_actualHeavier_returnsActual() {
        double billable = calculator.getBillableWeight(15.0, 10.0);
        assertEquals(15.0, billable, 0.0001);
    }

    @Test
    void getBillableWeight_dimensionalHeavier_returnsDimensional() {
        double billable = calculator.getBillableWeight(5.0, 20.0);
        assertEquals(20.0, billable, 0.0001);
    }

    @Test
    void getBillableWeight_equalWeights_returnsEitherValue() {
        double billable = calculator.getBillableWeight(10.0, 10.0);
        assertEquals(10.0, billable, 0.0001);
    }

    // ── applyFuelSurcharge ────────────────────────────────────────────────────

    @Test
    void applyFuelSurcharge_tenPercent_addsCorrectAmount() {
        // baseRate=100, surcharge=10%  =>  100 + 10 = 110
        double result = calculator.applyFuelSurcharge(100.0, 10.0);
        assertEquals(110.0, result, 0.0001);
    }

    @Test
    void applyFuelSurcharge_zeroPercent_returnsBaseRate() {
        double result = calculator.applyFuelSurcharge(50.0, 0.0);
        assertEquals(50.0, result, 0.0001);
    }

    @Test
    void applyFuelSurcharge_fiftyPercent_returnsOneAndHalfBaseRate() {
        double result = calculator.applyFuelSurcharge(200.0, 50.0);
        assertEquals(300.0, result, 0.0001);
    }

    @Test
    void applyFuelSurcharge_hundredPercent_doublesBaseRate() {
        double result = calculator.applyFuelSurcharge(80.0, 100.0);
        assertEquals(160.0, result, 0.0001);
    }
}
