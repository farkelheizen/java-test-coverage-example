package com.example.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    // ── escapeJsonString ────────────────────────────────────────────────────

    @Test
    void escapeJsonString_nullReturnsLiteralNull() {
        assertEquals("null", JsonUtils.escapeJsonString(null));
    }

    @Test
    void escapeJsonString_emptyStringReturnsEmpty() {
        assertEquals("", JsonUtils.escapeJsonString(""));
    }

    @Test
    void escapeJsonString_plainStringUnchanged() {
        assertEquals("hello", JsonUtils.escapeJsonString("hello"));
    }

    @Test
    void escapeJsonString_escapesBackslash() {
        assertEquals("a\\\\b", JsonUtils.escapeJsonString("a\\b"));
    }

    @Test
    void escapeJsonString_escapesDoubleQuote() {
        assertEquals("a\\\"b", JsonUtils.escapeJsonString("a\"b"));
    }

    @Test
    void escapeJsonString_escapesNewline() {
        assertEquals("a\\nb", JsonUtils.escapeJsonString("a\nb"));
    }

    @Test
    void escapeJsonString_escapesCarriageReturn() {
        assertEquals("a\\rb", JsonUtils.escapeJsonString("a\rb"));
    }

    @Test
    void escapeJsonString_escapesTab() {
        assertEquals("a\\tb", JsonUtils.escapeJsonString("a\tb"));
    }

    // ── toJsonString(Map<String,Object>) ───────────────────────────────────

    @Test
    void toJsonString_nullMapReturnsLiteralNull() {
        assertEquals("null", JsonUtils.toJsonString((Map<String, Object>) null));
    }

    @Test
    void toJsonString_emptyMapReturnsEmptyObject() {
        assertEquals("{}", JsonUtils.toJsonString(Collections.emptyMap()));
    }

    @Test
    void toJsonString_stringValueIsQuoted() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "Alice");
        String result = JsonUtils.toJsonString(map);
        assertEquals("{\"name\":\"Alice\"}", result);
    }

    @Test
    void toJsonString_nullValueIsUnquoted() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("key", null);
        String result = JsonUtils.toJsonString(map);
        assertEquals("{\"key\":null}", result);
    }

    @Test
    void toJsonString_integerValueIsUnquoted() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("count", 42);
        String result = JsonUtils.toJsonString(map);
        assertEquals("{\"count\":42}", result);
    }

    @Test
    void toJsonString_multipleEntriesSeparatedByComma() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", "x");
        map.put("b", 1);
        String result = JsonUtils.toJsonString(map);
        assertEquals("{\"a\":\"x\",\"b\":1}", result);
    }

    // ── buildJsonArray ──────────────────────────────────────────────────────

    @Test
    void buildJsonArray_nullReturnsLiteralNull() {
        assertEquals("null", JsonUtils.buildJsonArray(null));
    }

    @Test
    void buildJsonArray_emptyListReturnsEmptyArray() {
        assertEquals("[]", JsonUtils.buildJsonArray(Collections.emptyList()));
    }

    @Test
    void buildJsonArray_singleItem() {
        assertEquals("[\"hello\"]", JsonUtils.buildJsonArray(List.of("hello")));
    }

    @Test
    void buildJsonArray_multipleItemsSeparatedByComma() {
        List<String> items = Arrays.asList("a", "b", "c");
        assertEquals("[\"a\",\"b\",\"c\"]", JsonUtils.buildJsonArray(items));
    }

    @Test
    void buildJsonArray_itemWithSpecialCharsIsEscaped() {
        List<String> items = List.of("say \"hi\"");
        assertEquals("[\"say \\\"hi\\\"\"]", JsonUtils.buildJsonArray(items));
    }

    // ── buildJsonObject(Map<String,String>) ────────────────────────────────

    @Test
    void buildJsonObject_nullReturnsLiteralNull() {
        assertEquals("null", JsonUtils.buildJsonObject(null));
    }

    @Test
    void buildJsonObject_emptyMapReturnsEmptyObject() {
        assertEquals("{}", JsonUtils.buildJsonObject(Collections.emptyMap()));
    }

    @Test
    void buildJsonObject_singleEntry() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("key", "value");
        assertEquals("{\"key\":\"value\"}", JsonUtils.buildJsonObject(map));
    }

    @Test
    void buildJsonObject_multipleEntriesSeparatedByComma() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("a", "1");
        map.put("b", "2");
        assertEquals("{\"a\":\"1\",\"b\":\"2\"}", JsonUtils.buildJsonObject(map));
    }
}
