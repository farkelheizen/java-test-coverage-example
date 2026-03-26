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

    // ── parseCSVLine ───────────────────────────────────────────────────────

    @Test
    void parseCSVLine_null_returnsEmptyList() {
        assertTrue(service.parseCSVLine(null).isEmpty());
    }

    @Test
    void parseCSVLine_emptyString_returnsSingleEmptyField() {
        // line is empty → returns empty list per the guard
        assertTrue(service.parseCSVLine("").isEmpty());
    }

    @Test
    void parseCSVLine_singleField_returnsOneElement() {
        List<String> result = service.parseCSVLine("hello");
        assertEquals(1, result.size());
        assertEquals("hello", result.get(0));
    }

    @Test
    void parseCSVLine_multipleFields_returnsAllFields() {
        List<String> result = service.parseCSVLine("one,two,three");
        assertEquals(List.of("one", "two", "three"), result);
    }

    @Test
    void parseCSVLine_fieldsWithSpaces_trimmed() {
        List<String> result = service.parseCSVLine(" alpha , beta , gamma ");
        assertEquals(List.of("alpha", "beta", "gamma"), result);
    }

    @Test
    void parseCSVLine_quotedFieldWithComma_treatedAsOneField() {
        List<String> result = service.parseCSVLine("\"hello, world\",other");
        assertEquals(2, result.size());
        assertEquals("hello, world", result.get(0));
        assertEquals("other", result.get(1));
    }

    @Test
    void parseCSVLine_quotedFieldInMiddle_parsedCorrectly() {
        List<String> result = service.parseCSVLine("first,\"second,value\",third");
        assertEquals(3, result.size());
        assertEquals("second,value", result.get(1));
    }

    @Test
    void parseCSVLine_emptyFields_returnedAsEmptyStrings() {
        List<String> result = service.parseCSVLine("a,,c");
        assertEquals(3, result.size());
        assertEquals("", result.get(1));
    }

    // ── validateFileExtension ──────────────────────────────────────────────

    @Test
    void validateFileExtension_null_returnsFalse() {
        assertFalse(service.validateFileExtension(null, List.of("pdf", "txt")));
    }

    @Test
    void validateFileExtension_emptyFilename_returnsFalse() {
        assertFalse(service.validateFileExtension("", List.of("pdf")));
    }

    @Test
    void validateFileExtension_noExtension_returnsFalse() {
        assertFalse(service.validateFileExtension("filename", List.of("pdf")));
    }

    @Test
    void validateFileExtension_matchingExtension_returnsTrue() {
        assertTrue(service.validateFileExtension("report.pdf", List.of("pdf", "docx")));
    }

    @Test
    void validateFileExtension_extensionCaseInsensitive_returnsTrue() {
        assertTrue(service.validateFileExtension("image.PNG", List.of("png", "jpg")));
    }

    @Test
    void validateFileExtension_nonMatchingExtension_returnsFalse() {
        assertFalse(service.validateFileExtension("script.exe", List.of("pdf", "txt")));
    }

    @Test
    void validateFileExtension_dotAtStart_noRealExtension_returnsFalse() {
        // ".gitignore" → dotIndex=0 → ext="gitignore" → should not match "pdf"
        assertFalse(service.validateFileExtension(".gitignore", List.of("pdf")));
    }

    // ── formatFileSize ─────────────────────────────────────────────────────

    @Test
    void formatFileSize_bytes_returnsBytes() {
        assertEquals("512 B", service.formatFileSize(512));
    }

    @Test
    void formatFileSize_exactlyOneKB_returnsKB() {
        assertEquals("1.0 KB", service.formatFileSize(1024));
    }

    @Test
    void formatFileSize_kilobytes_returnsKB() {
        assertEquals("1.5 KB", service.formatFileSize(1536));
    }

    @Test
    void formatFileSize_exactlyOneMB_returnsMB() {
        assertEquals("1.0 MB", service.formatFileSize(1_048_576));
    }

    @Test
    void formatFileSize_megabytes_returnsMB() {
        assertEquals("2.5 MB", service.formatFileSize(1_048_576 * 2 + 524288));
    }

    @Test
    void formatFileSize_exactlyOneGB_returnsGB() {
        assertEquals("1.0 GB", service.formatFileSize(1_073_741_824L));
    }

    @Test
    void formatFileSize_gigabytes_returnsGB() {
        assertEquals("2.0 GB", service.formatFileSize(1_073_741_824L * 2));
    }

    @Test
    void formatFileSize_zeroByte_returnsZeroBytes() {
        assertEquals("0 B", service.formatFileSize(0));
    }

    // ── sanitizeFilename ───────────────────────────────────────────────────

    @Test
    void sanitizeFilename_null_returnsEmptyString() {
        assertEquals("", service.sanitizeFilename(null));
    }

    @Test
    void sanitizeFilename_cleanFilename_returnsUnchanged() {
        assertEquals("report.pdf", service.sanitizeFilename("report.pdf"));
    }

    @Test
    void sanitizeFilename_backslash_replaced() {
        assertEquals("folder_file.txt", service.sanitizeFilename("folder\\file.txt"));
    }

    @Test
    void sanitizeFilename_colon_replaced() {
        assertEquals("file_name.txt", service.sanitizeFilename("file:name.txt"));
    }

    @Test
    void sanitizeFilename_asterisk_replaced() {
        assertEquals("file_name", service.sanitizeFilename("file*name"));
    }

    @Test
    void sanitizeFilename_questionMark_replaced() {
        assertEquals("file_name", service.sanitizeFilename("file?name"));
    }

    @Test
    void sanitizeFilename_angleBrackets_replaced() {
        assertEquals("_file_", service.sanitizeFilename("<file>"));
    }

    @Test
    void sanitizeFilename_pipe_replaced() {
        assertEquals("file_name", service.sanitizeFilename("file|name"));
    }

    @Test
    void sanitizeFilename_doubleQuote_replaced() {
        assertEquals("file_name", service.sanitizeFilename("file\"name"));
    }

    @Test
    void sanitizeFilename_slash_replaced() {
        assertEquals("path_to_file", service.sanitizeFilename("path/to/file"));
    }
}
