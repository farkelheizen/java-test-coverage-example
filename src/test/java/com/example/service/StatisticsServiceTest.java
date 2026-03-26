package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsServiceTest {

    private StatisticsService service;

    @BeforeEach
    void setUp() {
        service = new StatisticsService();
    }

    // ── calculateMean ────────────────────────────────────────────────────────

    @Test
    void calculateMean_nullReturnsZero() {
        assertEquals(0.0, service.calculateMean(null));
    }

    @Test
    void calculateMean_emptyReturnsZero() {
        assertEquals(0.0, service.calculateMean(Collections.emptyList()));
    }

    @Test
    void calculateMean_singleValueReturnsThatValue() {
        assertEquals(7.0, service.calculateMean(List.of(7.0)));
    }

    @Test
    void calculateMean_multipleValues() {
        assertEquals(2.0, service.calculateMean(Arrays.asList(1.0, 2.0, 3.0)));
    }

    // ── calculateMedian ──────────────────────────────────────────────────────

    @Test
    void calculateMedian_nullReturnsZero() {
        assertEquals(0.0, service.calculateMedian(null));
    }

    @Test
    void calculateMedian_emptyReturnsZero() {
        assertEquals(0.0, service.calculateMedian(Collections.emptyList()));
    }

    @Test
    void calculateMedian_oddCountReturnsMiddleElement() {
        // sorted: [1, 3, 5] → middle index 1 → 3.0
        assertEquals(3.0, service.calculateMedian(Arrays.asList(5.0, 1.0, 3.0)));
    }

    @Test
    void calculateMedian_evenCountReturnsAverageOfMiddleTwo() {
        // sorted: [1, 2, 3, 4] → (2+3)/2 = 2.5
        assertEquals(2.5, service.calculateMedian(Arrays.asList(3.0, 1.0, 4.0, 2.0)));
    }

    @Test
    void calculateMedian_singleElement() {
        assertEquals(42.0, service.calculateMedian(List.of(42.0)));
    }

    // ── calculateStandardDeviation ───────────────────────────────────────────

    @Test
    void calculateStandardDeviation_nullReturnsZero() {
        assertEquals(0.0, service.calculateStandardDeviation(null));
    }

    @Test
    void calculateStandardDeviation_singleElementReturnsZero() {
        assertEquals(0.0, service.calculateStandardDeviation(List.of(5.0)));
    }

    @Test
    void calculateStandardDeviation_identicalValuesReturnsZero() {
        assertEquals(0.0, service.calculateStandardDeviation(Arrays.asList(3.0, 3.0, 3.0)));
    }

    @Test
    void calculateStandardDeviation_knownValues() {
        // mean=2, sumSqDiff=(1+0+1)=2, sd=sqrt(2/3)≈0.8165
        double sd = service.calculateStandardDeviation(Arrays.asList(1.0, 2.0, 3.0));
        assertEquals(Math.sqrt(2.0 / 3.0), sd, 1e-9);
    }

    // ── calculatePercentile ──────────────────────────────────────────────────

    @Test
    void calculatePercentile_nullReturnsZero() {
        assertEquals(0.0, service.calculatePercentile(null, 50));
    }

    @Test
    void calculatePercentile_emptyReturnsZero() {
        assertEquals(0.0, service.calculatePercentile(Collections.emptyList(), 50));
    }

    @Test
    void calculatePercentile_zerothPercentileReturnsMin() {
        List<Double> values = Arrays.asList(30.0, 10.0, 20.0);
        assertEquals(10.0, service.calculatePercentile(values, 0));
    }

    @Test
    void calculatePercentile_hundredthPercentileReturnsMax() {
        List<Double> values = Arrays.asList(30.0, 10.0, 20.0);
        assertEquals(30.0, service.calculatePercentile(values, 100));
    }

    @Test
    void calculatePercentile_50thPercentileOddCount() {
        // sorted [10, 20, 30], index=1.0, lower==upper → 20.0
        List<Double> values = Arrays.asList(30.0, 10.0, 20.0);
        assertEquals(20.0, service.calculatePercentile(values, 50));
    }

    @Test
    void calculatePercentile_50thPercentileEvenCount_interpolates() {
        // sorted [10, 20, 30, 40], index=1.5, fraction=0.5 → 20*0.5 + 30*0.5 = 25
        List<Double> values = Arrays.asList(40.0, 10.0, 30.0, 20.0);
        assertEquals(25.0, service.calculatePercentile(values, 50), 1e-9);
    }
}
