package com.example.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColorUtilsTest {

    // ── isValidHexColor ──────────────────────────────────────────────────────

    @Test
    void isValidHexColor_nullReturnsFalse() {
        assertFalse(ColorUtils.isValidHexColor(null));
    }

    @Test
    void isValidHexColor_withHashReturnsTrueForValidColor() {
        assertTrue(ColorUtils.isValidHexColor("#FF0000"));
    }

    @Test
    void isValidHexColor_withoutHashReturnsTrueForValidColor() {
        assertTrue(ColorUtils.isValidHexColor("FF0000"));
    }

    @Test
    void isValidHexColor_invalidCharsReturnsFalse() {
        assertFalse(ColorUtils.isValidHexColor("#ZZZZZZ"));
    }

    @Test
    void isValidHexColor_tooShortReturnsFalse() {
        assertFalse(ColorUtils.isValidHexColor("#FFF"));
    }

    @Test
    void isValidHexColor_lowercaseHexReturnsTure() {
        assertTrue(ColorUtils.isValidHexColor("#aabbcc"));
    }

    // ── hexToRgb ────────────────────────────────────────────────────────────

    @Test
    void hexToRgb_nullReturnsNull() {
        assertNull(ColorUtils.hexToRgb(null));
    }

    @Test
    void hexToRgb_invalidHexReturnsNull() {
        assertNull(ColorUtils.hexToRgb("ZZZZZZ"));
    }

    @Test
    void hexToRgb_withHashParsesCorrectly() {
        int[] rgb = ColorUtils.hexToRgb("#FF0000");
        assertNotNull(rgb);
        assertArrayEquals(new int[]{255, 0, 0}, rgb);
    }

    @Test
    void hexToRgb_withoutHashParsesCorrectly() {
        int[] rgb = ColorUtils.hexToRgb("00FF00");
        assertNotNull(rgb);
        assertArrayEquals(new int[]{0, 255, 0}, rgb);
    }

    @Test
    void hexToRgb_mixedCaseParsesCorrectly() {
        int[] rgb = ColorUtils.hexToRgb("#0000ff");
        assertNotNull(rgb);
        assertArrayEquals(new int[]{0, 0, 255}, rgb);
    }

    // ── rgbToHex ────────────────────────────────────────────────────────────

    @Test
    void rgbToHex_redProducesCorrectHex() {
        assertEquals("#FF0000", ColorUtils.rgbToHex(255, 0, 0));
    }

    @Test
    void rgbToHex_blackProducesAllZeros() {
        assertEquals("#000000", ColorUtils.rgbToHex(0, 0, 0));
    }

    @Test
    void rgbToHex_whiteProducesAllFs() {
        assertEquals("#FFFFFF", ColorUtils.rgbToHex(255, 255, 255));
    }

    // ── blendColors ─────────────────────────────────────────────────────────

    @Test
    void blendColors_firstArgNullReturnsNull() {
        assertNull(ColorUtils.blendColors(null, "#FF0000"));
    }

    @Test
    void blendColors_secondArgNullReturnsNull() {
        assertNull(ColorUtils.blendColors("#FF0000", null));
    }

    @Test
    void blendColors_blendRedAndBlueGivesPurple() {
        // r=(255+0)/2=127, g=0, b=(0+255)/2=127 → #7F007F
        assertEquals("#7F007F", ColorUtils.blendColors("#FF0000", "#0000FF"));
    }

    @Test
    void blendColors_blendIdenticalColorsReturnsSame() {
        assertEquals("#808080", ColorUtils.blendColors("#808080", "#808080"));
    }

    @Test
    void blendColors_firstArgInvalidReturnsNull() {
        assertNull(ColorUtils.blendColors("INVALID", "#FF0000"));
    }

    // ── isDarkColor ──────────────────────────────────────────────────────────

    @Test
    void isDarkColor_nullReturnsFalse() {
        assertFalse(ColorUtils.isDarkColor(null));
    }

    @Test
    void isDarkColor_blackIsDetectedAsDark() {
        assertTrue(ColorUtils.isDarkColor("#000000"));
    }

    @Test
    void isDarkColor_whiteIsDetectedAsLight() {
        assertFalse(ColorUtils.isDarkColor("#FFFFFF"));
    }

    @Test
    void isDarkColor_darkBlueIsDetectedAsDark() {
        // luminance ≈ 0.2126*0 + 0.7152*0 + 0.0722*139 ≈ 10.0 < 128
        assertTrue(ColorUtils.isDarkColor("#00008B"));
    }
}
