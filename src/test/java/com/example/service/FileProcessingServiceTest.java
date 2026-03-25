package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileProcessingServiceTest {

    private FileProcessingService service;

    @BeforeEach
    void setUp() {
        service = new FileProcessingService();
    }

    // ─── parseCSVLine ─────────────────────────────────────────────────────────

    @Test
    void parseCSVLine_nullInput_returnsEmptyList() {
        List<String> result = service.parseCSVLine(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void parseCSVLine_emptyString_returnsSingleEmptyField() {
        List<String> result = service.parseCSVLine("");
        assertTrue(result.isEmpty());
    }

    @Test
    void parseCSVLine_simpleCSV_returnsAllFields() {
        List<String> result = service.parseCSVLine("a,b,c");
        assertEquals(List.of("a", "b", "c"), result);
    }

    @Test
    void parseCSVLine_quotedFieldWithComma_treatsAsOneField() {
        List<String> result = service.parseCSVLine("\"a,b\",c");
        assertEquals(2, result.size());
        assertEquals("a,b", result.get(0));
        assertEquals("c", result.get(1));
    }

    @Test
    void parseCSVLine_mixedQuotedAndUnquoted_parsesCorrectly() {
        List<String> result = service.parseCSVLine("hello,\"world,earth\",foo");
        assertEquals(3, result.size());
        assertEquals("hello", result.get(0));
        assertEquals("world,earth", result.get(1));
        assertEquals("foo", result.get(2));
    }

    @Test
    void parseCSVLine_singleValue_returnsSingleElement() {
        List<String> result = service.parseCSVLine("onlyvalue");
        assertEquals(1, result.size());
        assertEquals("onlyvalue", result.get(0));
    }

    @Test
    void parseCSVLine_fieldsWithSpaces_trims() {
        List<String> result = service.parseCSVLine(" a , b , c ");
        assertEquals(List.of("a", "b", "c"), result);
    }

    // ─── validateFileExtension ────────────────────────────────────────────────

    @Test
    void validateFileExtension_nullFilename_returnsFalse() {
        assertFalse(service.validateFileExtension(null, List.of("jpg", "png")));
    }

    @Test
    void validateFileExtension_emptyFilename_returnsFalse() {
        assertFalse(service.validateFileExtension("", List.of("jpg", "png")));
    }

    @Test
    void validateFileExtension_noDotInFilename_returnsFalse() {
        assertFalse(service.validateFileExtension("filename", List.of("jpg", "png")));
    }

    @Test
    void validateFileExtension_extensionNotInList_returnsFalse() {
        assertFalse(service.validateFileExtension("file.gif", List.of("jpg", "png")));
    }

    @Test
    void validateFileExtension_extensionInList_returnsTrue() {
        assertTrue(service.validateFileExtension("photo.jpg", List.of("jpg", "png")));
    }

    @Test
    void validateFileExtension_extensionCaseInsensitive_returnsTrue() {
        assertTrue(service.validateFileExtension("photo.JPG", List.of("jpg", "png")));
    }

    @Test
    void validateFileExtension_allowedListCaseInsensitive_returnsTrue() {
        assertTrue(service.validateFileExtension("photo.jpg", List.of("JPG", "PNG")));
    }

    // ─── formatFileSize ───────────────────────────────────────────────────────

    @Test
    void formatFileSize_gigabytes_formatsAsGB() {
        String result = service.formatFileSize(1_073_741_824L);
        assertEquals("1.0 GB", result);
    }

    @Test
    void formatFileSize_megabytes_formatsAsMB() {
        String result = service.formatFileSize(1_048_576L);
        assertEquals("1.0 MB", result);
    }

    @Test
    void formatFileSize_kilobytes_formatsAsKB() {
        String result = service.formatFileSize(1024L);
        assertEquals("1.0 KB", result);
    }

    @Test
    void formatFileSize_bytes_formatsAsB() {
        String result = service.formatFileSize(500L);
        assertEquals("500 B", result);
    }

    @Test
    void formatFileSize_zero_formatsAsZeroB() {
        String result = service.formatFileSize(0L);
        assertEquals("0 B", result);
    }

    @Test
    void formatFileSize_largerThan1GB_formatsAsGB() {
        String result = service.formatFileSize(2_147_483_648L);
        assertEquals("2.0 GB", result);
    }

    // ─── sanitizeFilename ─────────────────────────────────────────────────────

    @Test
    void sanitizeFilename_nullInput_returnsEmptyString() {
        assertEquals("", service.sanitizeFilename(null));
    }

    @Test
    void sanitizeFilename_noSpecialChars_returnsUnchanged() {
        assertEquals("myfile.txt", service.sanitizeFilename("myfile.txt"));
    }

    @Test
    void sanitizeFilename_specialChars_replacedWithUnderscore() {
        String result = service.sanitizeFilename("file/:*?\"<>|.txt");
        assertEquals("file________.txt", result);
    }

    @Test
    void sanitizeFilename_backslash_replacedWithUnderscore() {
        String result = service.sanitizeFilename("file\\name.txt");
        assertEquals("file_name.txt", result);
    }
}
