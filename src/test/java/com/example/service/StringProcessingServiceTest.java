package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringProcessingServiceTest {

    private StringProcessingService service;

    @BeforeEach
    void setUp() {
        service = new StringProcessingService();
    }

    // ── toCamelCase ────────────────────────────────────────────────────────

    @Test
    void toCamelCase_null_returnsNull() {
        assertNull(service.toCamelCase(null));
    }

    @Test
    void toCamelCase_emptyString_returnsEmpty() {
        assertEquals("", service.toCamelCase(""));
    }

    @Test
    void toCamelCase_singleWord_returnsLowerCase() {
        assertEquals("hello", service.toCamelCase("HELLO"));
    }

    @Test
    void toCamelCase_snakeCaseInput_returnsCamelCase() {
        assertEquals("helloWorld", service.toCamelCase("hello_world"));
    }

    @Test
    void toCamelCase_multipleUnderscores_returnsCamelCase() {
        assertEquals("myVariableName", service.toCamelCase("my_variable_name"));
    }

    @Test
    void toCamelCase_consecutiveUnderscores_skipsEmptyParts() {
        // double underscore produces an empty part that should be skipped
        String result = service.toCamelCase("hello__world");
        // parts: ["hello", "", "world"] → "hello" + (skip empty) + "World" = "helloWorld"
        assertEquals("helloWorld", result);
    }

    // ── toSnakeCase ────────────────────────────────────────────────────────

    @Test
    void toSnakeCase_null_returnsNull() {
        assertNull(service.toSnakeCase(null));
    }

    @Test
    void toSnakeCase_emptyString_returnsEmpty() {
        assertEquals("", service.toSnakeCase(""));
    }

    @Test
    void toSnakeCase_camelCase_returnsSnakeCase() {
        assertEquals("hello_world", service.toSnakeCase("helloWorld"));
    }

    @Test
    void toSnakeCase_allLowerCase_returnsUnchanged() {
        assertEquals("hello", service.toSnakeCase("hello"));
    }

    @Test
    void toSnakeCase_capitalizedFirstLetter_noLeadingUnderscore() {
        // "MyVar" → "_my_var" via regex, then leading underscore stripped → "my_var"
        assertEquals("my_var", service.toSnakeCase("MyVar"));
    }

    @Test
    void toSnakeCase_multipleUpperCase_returnsSnakeCase() {
        assertEquals("my_variable_name", service.toSnakeCase("myVariableName"));
    }

    // ── truncate ───────────────────────────────────────────────────────────

    @Test
    void truncate_nullText_returnsNull() {
        assertNull(service.truncate(null, 10, "..."));
    }

    @Test
    void truncate_shortText_returnsOriginal() {
        assertEquals("Hi", service.truncate("Hi", 10, "..."));
    }

    @Test
    void truncate_exactLength_returnsOriginal() {
        assertEquals("Hello", service.truncate("Hello", 5, "..."));
    }

    @Test
    void truncate_longText_returnsTruncatedWithSuffix() {
        assertEquals("Hello...", service.truncate("Hello, World!", 8, "..."));
    }

    @Test
    void truncate_emptySuffix_returnsTruncatedWithoutSuffix() {
        assertEquals("Hello", service.truncate("Hello, World!", 5, ""));
    }

    // ── countWords ─────────────────────────────────────────────────────────

    @Test
    void countWords_null_returnsZero() {
        assertEquals(0, service.countWords(null));
    }

    @Test
    void countWords_blankString_returnsZero() {
        assertEquals(0, service.countWords("   "));
    }

    @Test
    void countWords_emptyString_returnsZero() {
        assertEquals(0, service.countWords(""));
    }

    @Test
    void countWords_singleWord_returnsOne() {
        assertEquals(1, service.countWords("Hello"));
    }

    @Test
    void countWords_multipleWords_returnsCorrectCount() {
        assertEquals(4, service.countWords("the quick brown fox"));
    }

    @Test
    void countWords_extraSpacesBetweenWords_returnsCorrectCount() {
        assertEquals(2, service.countWords("hello   world"));
    }

    // ── isPalindrome ───────────────────────────────────────────────────────

    @Test
    void isPalindrome_null_returnsFalse() {
        assertFalse(service.isPalindrome(null));
    }

    @Test
    void isPalindrome_emptyString_returnsTrue() {
        assertTrue(service.isPalindrome(""));
    }

    @Test
    void isPalindrome_singleCharacter_returnsTrue() {
        assertTrue(service.isPalindrome("a"));
    }

    @Test
    void isPalindrome_simplePalindrome_returnsTrue() {
        assertTrue(service.isPalindrome("racecar"));
    }

    @Test
    void isPalindrome_notPalindrome_returnsFalse() {
        assertFalse(service.isPalindrome("hello"));
    }

    @Test
    void isPalindrome_mixedCasePalindrome_returnsTrue() {
        assertTrue(service.isPalindrome("RaceCar"));
    }

    @Test
    void isPalindrome_phraseWithSpacesAndPunctuation_returnsTrue() {
        // "A man, a plan, a canal: Panama"
        assertTrue(service.isPalindrome("A man, a plan, a canal: Panama"));
    }

    @Test
    void isPalindrome_numericPalindrome_returnsTrue() {
        assertTrue(service.isPalindrome("12321"));
    }

    @Test
    void isPalindrome_numericNonPalindrome_returnsFalse() {
        assertFalse(service.isPalindrome("12345"));
    }
}
