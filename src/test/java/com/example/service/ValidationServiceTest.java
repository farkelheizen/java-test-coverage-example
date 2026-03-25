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

    // ---- validateEmail ----

    @Test
    void validateEmail_nullEmail_returnsFalse() {
        assertFalse(service.validateEmail(null));
    }

    @Test
    void validateEmail_tooShort_returnsFalse() {
        // length < 3
        assertFalse(service.validateEmail("a@"));
    }

    @Test
    void validateEmail_tooLong_returnsFalse() {
        // length > 254 (255 chars total)
        String longEmail = "a".repeat(251) + "@b.c";
        assertFalse(service.validateEmail(longEmail));
    }

    @Test
    void validateEmail_noAtSign_returnsFalse() {
        assertFalse(service.validateEmail("invalidemail.com"));
    }

    @Test
    void validateEmail_atSignAtIndexZero_returnsFalse() {
        assertFalse(service.validateEmail("@domain.com"));
    }

    @Test
    void validateEmail_domainMissingDot_returnsFalse() {
        assertFalse(service.validateEmail("user@domaincom"));
    }

    @Test
    void validateEmail_validEmail_returnsTrue() {
        assertTrue(service.validateEmail("user@example.com"));
    }

    @Test
    void validateEmail_validEmailMinLength_returnsTrue() {
        // exactly 3 chars: a@b — no dot in domain, false
        assertFalse(service.validateEmail("a@b"));
    }

    // ---- validatePhoneNumber ----

    @Test
    void validatePhoneNumber_nullPhone_returnsFalse() {
        assertFalse(service.validatePhoneNumber(null));
    }

    @Test
    void validatePhoneNumber_nonDigitAfterCleaning_returnsFalse() {
        assertFalse(service.validatePhoneNumber("123abc456"));
    }

    @Test
    void validatePhoneNumber_tooShort_returnsFalse() {
        // 9 digits after cleaning
        assertFalse(service.validatePhoneNumber("123456789"));
    }

    @Test
    void validatePhoneNumber_tooLong_returnsFalse() {
        // 16 digits
        assertFalse(service.validatePhoneNumber("1234567890123456"));
    }

    @Test
    void validatePhoneNumber_validWithSpacesAndDashes_returnsTrue() {
        assertTrue(service.validatePhoneNumber("+1 800-555-1234"));
    }

    @Test
    void validatePhoneNumber_validTenDigits_returnsTrue() {
        assertTrue(service.validatePhoneNumber("1234567890"));
    }

    @Test
    void validatePhoneNumber_validFifteenDigits_returnsTrue() {
        assertTrue(service.validatePhoneNumber("123456789012345"));
    }

    // ---- validateZipCode ----

    @Test
    void validateZipCode_nullZipCode_returnsFalse() {
        assertFalse(service.validateZipCode(null, "US"));
    }

    @Test
    void validateZipCode_nullCountry_returnsFalse() {
        assertFalse(service.validateZipCode("12345", null));
    }

    @Test
    void validateZipCode_usValidFiveDigit_returnsTrue() {
        assertTrue(service.validateZipCode("12345", "US"));
    }

    @Test
    void validateZipCode_usValidPlusFour_returnsTrue() {
        assertTrue(service.validateZipCode("12345-6789", "US"));
    }

    @Test
    void validateZipCode_usInvalid_returnsFalse() {
        assertFalse(service.validateZipCode("ABCDE", "US"));
    }

    @Test
    void validateZipCode_caValid_returnsTrue() {
        assertTrue(service.validateZipCode("A1A 1A1", "CA"));
    }

    @Test
    void validateZipCode_caInvalid_returnsFalse() {
        assertFalse(service.validateZipCode("12345", "CA"));
    }

    @Test
    void validateZipCode_ukValid_returnsTrue() {
        assertTrue(service.validateZipCode("SW1A 2AA", "UK"));
    }

    @Test
    void validateZipCode_gbValid_returnsTrue() {
        assertTrue(service.validateZipCode("EC1A 1BB", "GB"));
    }

    @Test
    void validateZipCode_defaultValidFiveChar_returnsTrue() {
        assertTrue(service.validateZipCode("12345", "DE"));
    }

    @Test
    void validateZipCode_defaultTooShort_returnsFalse() {
        assertFalse(service.validateZipCode("AB", "DE"));
    }

    @Test
    void validateZipCode_defaultTooLong_returnsFalse() {
        assertFalse(service.validateZipCode("12345678901", "DE"));
    }

    // ---- validatePassword ----

    @Test
    void validatePassword_nullPassword_returnsFalse() {
        assertFalse(service.validatePassword(null));
    }

    @Test
    void validatePassword_tooShort_returnsFalse() {
        assertFalse(service.validatePassword("Ab1@"));
    }

    @Test
    void validatePassword_missingUppercase_returnsFalse() {
        assertFalse(service.validatePassword("abc1@def"));
    }

    @Test
    void validatePassword_missingLowercase_returnsFalse() {
        assertFalse(service.validatePassword("ABC1@DEF"));
    }

    @Test
    void validatePassword_missingDigit_returnsFalse() {
        assertFalse(service.validatePassword("Abcd@efg"));
    }

    @Test
    void validatePassword_missingSpecialChar_returnsFalse() {
        assertFalse(service.validatePassword("Abcd1efg"));
    }

    @Test
    void validatePassword_validPassword_returnsTrue() {
        assertTrue(service.validatePassword("Abc1@def"));
    }
}
