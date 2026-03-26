package com.example.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompressionUtilsTest {

    // ── estimateCompressionRatio ─────────────────────────────────────────────

    @Test
    void estimateCompressionRatio_nullReturnsOne() {
        assertEquals(1.0, CompressionUtils.estimateCompressionRatio(null));
    }

    @Test
    void estimateCompressionRatio_emptyReturnsOne() {
        assertEquals(1.0, CompressionUtils.estimateCompressionRatio(""));
    }

    @Test
    void estimateCompressionRatio_allSameCharsReturnsLowRatio() {
        // 1 unique char / 4 length = 0.25
        assertEquals(0.25, CompressionUtils.estimateCompressionRatio("aaaa"), 1e-9);
    }

    @Test
    void estimateCompressionRatio_allUniqueCharsReturnsOne() {
        // 4 unique / 4 length = 1.0
        assertEquals(1.0, CompressionUtils.estimateCompressionRatio("abcd"), 1e-9);
    }

    @Test
    void estimateCompressionRatio_mixedChars() {
        // "aabb" → 2 unique / 4 length = 0.5
        assertEquals(0.5, CompressionUtils.estimateCompressionRatio("aabb"), 1e-9);
    }

    // ── countRedundancy ──────────────────────────────────────────────────────

    @Test
    void countRedundancy_nullReturnsZero() {
        assertEquals(0, CompressionUtils.countRedundancy(null));
    }

    @Test
    void countRedundancy_emptyReturnsZero() {
        assertEquals(0, CompressionUtils.countRedundancy(""));
    }

    @Test
    void countRedundancy_allUniqueCharsReturnsZero() {
        assertEquals(0, CompressionUtils.countRedundancy("abcd"));
    }

    @Test
    void countRedundancy_repeatedCharsReturnsCorrectCount() {
        // "aabb" → length=4, unique=2, redundancy=2
        assertEquals(2, CompressionUtils.countRedundancy("aabb"));
    }

    @Test
    void countRedundancy_allSameChar() {
        // "aaaa" → length=4, unique=1, redundancy=3
        assertEquals(3, CompressionUtils.countRedundancy("aaaa"));
    }

    // ── runLengthEncode ──────────────────────────────────────────────────────

    @Test
    void runLengthEncode_nullReturnsNull() {
        assertNull(CompressionUtils.runLengthEncode(null));
    }

    @Test
    void runLengthEncode_emptyReturnsEmpty() {
        assertEquals("", CompressionUtils.runLengthEncode(""));
    }

    @Test
    void runLengthEncode_allSameChars() {
        assertEquals("a3", CompressionUtils.runLengthEncode("aaa"));
    }

    @Test
    void runLengthEncode_noRepetition() {
        assertEquals("abcd", CompressionUtils.runLengthEncode("abcd"));
    }

    @Test
    void runLengthEncode_mixedRuns() {
        assertEquals("a3b2c", CompressionUtils.runLengthEncode("aaabbc"));
    }

    @Test
    void runLengthEncode_singleChar() {
        assertEquals("z", CompressionUtils.runLengthEncode("z"));
    }

    // ── runLengthDecode ──────────────────────────────────────────────────────

    @Test
    void runLengthDecode_nullReturnsNull() {
        assertNull(CompressionUtils.runLengthDecode(null));
    }

    @Test
    void runLengthDecode_emptyReturnsEmpty() {
        assertEquals("", CompressionUtils.runLengthDecode(""));
    }

    @Test
    void runLengthDecode_encodedRunExpands() {
        assertEquals("aaa", CompressionUtils.runLengthDecode("a3"));
    }

    @Test
    void runLengthDecode_noNumbersReturnsSameString() {
        assertEquals("abcd", CompressionUtils.runLengthDecode("abcd"));
    }

    @Test
    void runLengthDecode_mixedRuns() {
        assertEquals("aaabbc", CompressionUtils.runLengthDecode("a3b2c"));
    }

    @Test
    void runLengthDecode_roundTrip() {
        String original = "aaabbbcccc";
        assertEquals(original, CompressionUtils.runLengthDecode(CompressionUtils.runLengthEncode(original)));
    }
}
