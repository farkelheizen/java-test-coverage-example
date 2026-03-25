package com.example.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MathUtilsTest {

    // ── roundUp ─────────────────────────────────────────────────────────────

    @Test
    void roundUp_returnsNextWholeNumber() {
        assertEquals(3.0, MathUtils.roundUp(2.1));
    }

    @Test
    void roundUp_integerValueUnchanged() {
        assertEquals(3.0, MathUtils.roundUp(3.0));
    }

    // ── roundDown ───────────────────────────────────────────────────────────

    @Test
    void roundDown_truncatesToFloor() {
        assertEquals(2.0, MathUtils.roundDown(2.9));
    }

    @Test
    void roundDown_integerValueUnchanged() {
        assertEquals(2.0, MathUtils.roundDown(2.0));
    }

    // ── average ─────────────────────────────────────────────────────────────

    @Test
    void average_nullReturnsZero() {
        assertEquals(0.0, MathUtils.average((double[]) null));
    }

    @Test
    void average_emptyReturnsZero() {
        assertEquals(0.0, MathUtils.average());
    }

    @Test
    void average_singleValue() {
        assertEquals(5.0, MathUtils.average(5.0));
    }

    @Test
    void average_multipleValues() {
        assertEquals(2.0, MathUtils.average(1.0, 2.0, 3.0));
    }

    // ── max ─────────────────────────────────────────────────────────────────

    @Test
    void max_nullThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> MathUtils.max((double[]) null));
    }

    @Test
    void max_emptyThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, MathUtils::max);
    }

    @Test
    void max_singleValue() {
        assertEquals(7.0, MathUtils.max(7.0));
    }

    @Test
    void max_returnsLargest() {
        assertEquals(3.0, MathUtils.max(1.0, 3.0, 2.0));
    }

    // ── min ─────────────────────────────────────────────────────────────────

    @Test
    void min_nullThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> MathUtils.min((double[]) null));
    }

    @Test
    void min_emptyThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, MathUtils::min);
    }

    @Test
    void min_singleValue() {
        assertEquals(4.0, MathUtils.min(4.0));
    }

    @Test
    void min_returnsSmallest() {
        assertEquals(1.0, MathUtils.min(3.0, 1.0, 2.0));
    }

    // ── sumOf ────────────────────────────────────────────────────────────────

    @Test
    void sumOf_nullReturnsZero() {
        assertEquals(0.0, MathUtils.sumOf((double[]) null));
    }

    @Test
    void sumOf_emptyReturnsZero() {
        assertEquals(0.0, MathUtils.sumOf());
    }

    @Test
    void sumOf_multipleValues() {
        assertEquals(6.0, MathUtils.sumOf(1.0, 2.0, 3.0));
    }

    // ── standardDeviation ───────────────────────────────────────────────────

    @Test
    void standardDeviation_nullReturnsZero() {
        assertEquals(0.0, MathUtils.standardDeviation((double[]) null));
    }

    @Test
    void standardDeviation_singleValueReturnsZero() {
        assertEquals(0.0, MathUtils.standardDeviation(5.0));
    }

    @Test
    void standardDeviation_identicalValuesReturnsZero() {
        assertEquals(0.0, MathUtils.standardDeviation(3.0, 3.0, 3.0));
    }

    @Test
    void standardDeviation_knownValues() {
        // mean=2, sumSq=(1+0+1)=2, sd=sqrt(2/3)≈0.8165
        double sd = MathUtils.standardDeviation(1.0, 2.0, 3.0);
        assertEquals(Math.sqrt(2.0 / 3.0), sd, 1e-9);
    }
}
