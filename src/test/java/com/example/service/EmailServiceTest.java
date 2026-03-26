package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService();
    }

    // ── sendEmail ──────────────────────────────────────────────────────────

    @Test
    void sendEmail_validEmailAndSubject_returnsTrue() {
        assertTrue(emailService.sendEmail("user@example.com", "Hello", "Body text"));
    }

    @Test
    void sendEmail_invalidEmail_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendEmail("not-an-email", "Subject", "Body"));
    }

    @Test
    void sendEmail_nullEmail_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendEmail(null, "Subject", "Body"));
    }

    @Test
    void sendEmail_emailWithoutAtSign_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendEmail("nodomain.com", "Subject", "Body"));
    }

    @Test
    void sendEmail_emailWithoutDomainDot_throwsIllegalArgumentException() {
        // e.g. user@localhost has no dot in domain
        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendEmail("user@localhost", "Subject", "Body"));
    }

    @Test
    void sendEmail_blankSubject_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendEmail("user@example.com", "   ", "Body"));
    }

    @Test
    void sendEmail_nullSubject_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendEmail("user@example.com", null, "Body"));
    }

    @Test
    void sendEmail_emptySubject_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendEmail("user@example.com", "", "Body"));
    }

    // ── sendBulkEmail ──────────────────────────────────────────────────────

    @Test
    void sendBulkEmail_allValidRecipients_returnsFullCount() {
        List<String> recipients = List.of("a@example.com", "b@example.com", "c@example.com");
        assertEquals(3, emailService.sendBulkEmail(recipients, "Subject", "Body"));
    }

    @Test
    void sendBulkEmail_mixedValidAndInvalid_countsOnlyValid() {
        List<String> recipients = List.of("valid@example.com", "INVALID", "also@example.com");
        assertEquals(2, emailService.sendBulkEmail(recipients, "Subject", "Body"));
    }

    @Test
    void sendBulkEmail_allInvalidRecipients_returnsZero() {
        List<String> recipients = List.of("bad1", "bad2", "bad3");
        assertEquals(0, emailService.sendBulkEmail(recipients, "Subject", "Body"));
    }

    @Test
    void sendBulkEmail_emptyList_returnsZero() {
        assertEquals(0, emailService.sendBulkEmail(List.of(), "Subject", "Body"));
    }

    // ── formatEmailBody ────────────────────────────────────────────────────

    @Test
    void formatEmailBody_replacesAllPlaceholders() {
        String template = "Hello {{name}}, your order {{orderId}} is ready.";
        Map<String, String> vars = Map.of("name", "Alice", "orderId", "12345");
        String result = emailService.formatEmailBody(template, vars);
        assertEquals("Hello Alice, your order 12345 is ready.", result);
    }

    @Test
    void formatEmailBody_noMatchingPlaceholder_returnsTemplateUnchanged() {
        String template = "No placeholders here.";
        Map<String, String> vars = Map.of("name", "Bob");
        assertEquals("No placeholders here.", emailService.formatEmailBody(template, vars));
    }

    @Test
    void formatEmailBody_emptyVariablesMap_returnsOriginalTemplate() {
        String template = "Hello {{name}}";
        assertEquals("Hello {{name}}", emailService.formatEmailBody(template, Map.of()));
    }

    @Test
    void formatEmailBody_multiplePlaceholdersSameKey_allReplaced() {
        String template = "{{greeting}} {{greeting}}";
        Map<String, String> vars = Map.of("greeting", "Hi");
        assertEquals("Hi Hi", emailService.formatEmailBody(template, vars));
    }
}
