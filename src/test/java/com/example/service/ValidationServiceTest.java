package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    private ValidationService service;

    @BeforeEach
    void setUp() {
        service = new ValidationService();
    }

    // ── validateEmail ────────────────────────────────────────────────────────

    @Test
    void validateEmail_nullReturnsFalse() {
        assertFalse(service.validateEmail(null));
    }

    @Test
    void validateEmail_tooShortReturnsFalse() {
        assertFalse(service.validateEmail("a@"));  // length 2 < 3
    }

    @Test
    void validateEmail_tooLongReturnsFalse() {
        String email = "a".repeat(250) + "@b.com"; // > 254 chars
        assertFalse(service.validateEmail(email));
    }

    @Test
    void validateEmail_noAtSignReturnsFalse() {
        assertFalse(service.validateEmail("userexample.com"));
    }

    @Test
    void validateEmail_atSignAtStartReturnsFalse() {
        assertFalse(service.validateEmail("@example.com"));
    }

    @Test
    void validateEmail_domainWithoutDotReturnsFalse() {
        assertFalse(service.validateEmail("user@localhost"));
    }

    @Test
    void validateEmail_validEmailReturnsTrue() {
        assertTrue(service.validateEmail("user@example.com"));
    }

    @Test
    void validateEmail_validEmailWithSubdomainReturnsTrue() {
        assertTrue(service.validateEmail("user@mail.example.co.uk"));
    }

    // ── validatePhoneNumber ──────────────────────────────────────────────────

    @Test
    void validatePhoneNumber_nullReturnsFalse() {
        assertFalse(service.validatePhoneNumber(null));
    }

    @Test
    void validatePhoneNumber_nonDigitOnlyReturnsFalse() {
        assertFalse(service.validatePhoneNumber("abcdefghij"));
    }

    @Test
    void validatePhoneNumber_tooFewDigitsReturnsFalse() {
        assertFalse(service.validatePhoneNumber("123456789")); // 9 digits
    }

    @Test
    void validatePhoneNumber_tooManyDigitsReturnsFalse() {
        assertFalse(service.validatePhoneNumber("1234567890123456")); // 16 digits
    }

    @Test
    void validatePhoneNumber_tenDigitNumberReturnsTrue() {
        assertTrue(service.validatePhoneNumber("1234567890"));
    }

    @Test
    void validatePhoneNumber_formattedWithDashesAndSpacesReturnsTrue() {
        assertTrue(service.validatePhoneNumber("+1 800-555-0199"));
    }

    // ── validateZipCode ──────────────────────────────────────────────────────

    @Test
    void validateZipCode_nullZipReturnsFalse() {
        assertFalse(service.validateZipCode(null, "US"));
    }

    @Test
    void validateZipCode_nullCountryReturnsFalse() {
        assertFalse(service.validateZipCode("12345", null));
    }

    @Test
    void validateZipCode_usValidFiveDigitReturnsTrue() {
        assertTrue(service.validateZipCode("12345", "US"));
    }

    @Test
    void validateZipCode_usValidZipPlusFourReturnsTrue() {
        assertTrue(service.validateZipCode("12345-6789", "US"));
    }

    @Test
    void validateZipCode_usInvalidFormatReturnsFalse() {
        assertFalse(service.validateZipCode("ABCDE", "US"));
    }

    @Test
    void validateZipCode_caValidFormatReturnsTrue() {
        assertTrue(service.validateZipCode("K1A 0B1", "CA"));
    }

    @Test
    void validateZipCode_caInvalidFormatReturnsFalse() {
        assertFalse(service.validateZipCode("12345", "CA"));
    }

    @Test
    void validateZipCode_ukValidFormatReturnsTrue() {
        assertTrue(service.validateZipCode("SW1A 2AA", "UK"));
    }

    @Test
    void validateZipCode_gbValidFormatReturnsTrue() {
        assertTrue(service.validateZipCode("SW1A 2AA", "GB"));
    }

    @Test
    void validateZipCode_defaultCountryValidLengthReturnsTrue() {
        assertTrue(service.validateZipCode("12345", "AU")); // 5 chars, 3<=x<=10
    }

    @Test
    void validateZipCode_defaultCountryTooShortReturnsFalse() {
        assertFalse(service.validateZipCode("AB", "AU")); // 2 chars < 3
    }

    // ── validatePassword ─────────────────────────────────────────────────────

    @Test
    void validatePassword_nullReturnsFalse() {
        assertFalse(service.validatePassword(null));
    }

    @Test
    void validatePassword_tooShortReturnsFalse() {
        assertFalse(service.validatePassword("Ab1!"));
    }

    @Test
    void validatePassword_missingUppercaseReturnsFalse() {
        assertFalse(service.validatePassword("abcde1!f"));
    }

    @Test
    void validatePassword_missingLowercaseReturnsFalse() {
        assertFalse(service.validatePassword("ABCDE1!F"));
    }

    @Test
    void validatePassword_missingDigitReturnsFalse() {
        assertFalse(service.validatePassword("Abcdefg!"));
    }

    @Test
    void validatePassword_missingSpecialCharReturnsFalse() {
        assertFalse(service.validatePassword("Abcde123"));
    }

    @Test
    void validatePassword_validPasswordReturnsTrue() {
        assertTrue(service.validatePassword("Abcde1!g"));
    }
}
