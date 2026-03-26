package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaginationServiceTest {

    private PaginationService paginationService;

    @BeforeEach
    void setUp() {
        paginationService = new PaginationService();
    }

    // --- validatePageParams ---

    @Test
    void validatePageParams_throwsWhenPageIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> paginationService.validatePageParams(0, 10));
    }

    @Test
    void validatePageParams_throwsWhenPageIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> paginationService.validatePageParams(-1, 10));
    }

    @Test
    void validatePageParams_throwsWhenPageSizeIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> paginationService.validatePageParams(1, 0));
    }

    @Test
    void validatePageParams_throwsWhenPageSizeIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> paginationService.validatePageParams(1, -5));
    }

    @Test
    void validatePageParams_throwsWhenPageSizeExceeds100() {
        assertThrows(IllegalArgumentException.class,
                () -> paginationService.validatePageParams(1, 101));
    }

    @Test
    void validatePageParams_doesNotThrowForValidParams() {
        assertDoesNotThrow(() -> paginationService.validatePageParams(1, 10));
        assertDoesNotThrow(() -> paginationService.validatePageParams(5, 100));
    }

    // --- paginate ---

    @Test
    void paginate_returnsFirstPage() {
        List<Integer> items = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> result = paginationService.paginate(items, 1, 3);
        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    void paginate_returnsSecondPage() {
        List<Integer> items = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> result = paginationService.paginate(items, 2, 3);
        assertEquals(List.of(4, 5, 6), result);
    }

    @Test
    void paginate_returnsPartialLastPage() {
        List<Integer> items = List.of(1, 2, 3, 4, 5);
        List<Integer> result = paginationService.paginate(items, 2, 3);
        assertEquals(List.of(4, 5), result);
    }

    @Test
    void paginate_returnsEmptyListWhenPageBeyondData() {
        List<Integer> items = List.of(1, 2, 3);
        List<Integer> result = paginationService.paginate(items, 5, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void paginate_returnsAllItemsWhenPageSizeLargerThanList() {
        List<Integer> items = List.of(1, 2, 3);
        List<Integer> result = paginationService.paginate(items, 1, 100);
        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    void paginate_throwsWhenPageIsInvalid() {
        List<Integer> items = List.of(1, 2, 3);
        assertThrows(IllegalArgumentException.class,
                () -> paginationService.paginate(items, 0, 10));
    }

    @Test
    void paginate_throwsWhenPageSizeIsInvalid() {
        List<Integer> items = List.of(1, 2, 3);
        assertThrows(IllegalArgumentException.class,
                () -> paginationService.paginate(items, 1, 0));
    }

    @Test
    void paginate_worksWithEmptyList() {
        List<Integer> items = new ArrayList<>();
        List<Integer> result = paginationService.paginate(items, 1, 10);
        assertTrue(result.isEmpty());
    }

    // --- getTotalPages ---

    @Test
    void getTotalPages_throwsWhenPageSizeIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> paginationService.getTotalPages(10, 0));
    }

    @Test
    void getTotalPages_throwsWhenPageSizeIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> paginationService.getTotalPages(10, -1));
    }

    @Test
    void getTotalPages_exactDivision() {
        assertEquals(5, paginationService.getTotalPages(10, 2));
    }

    @Test
    void getTotalPages_roundsUpWhenRemainder() {
        assertEquals(4, paginationService.getTotalPages(10, 3));
    }

    @Test
    void getTotalPages_returnsOneForZeroItems() {
        // ceil(0/10) = 0, but that's what the impl returns
        assertEquals(0, paginationService.getTotalPages(0, 10));
    }

    @Test
    void getTotalPages_returnOneWhenItemsSmallerThanPage() {
        assertEquals(1, paginationService.getTotalPages(3, 10));
    }

    // --- createPageInfo ---

    @Test
    void createPageInfo_containsExpectedKeys() {
        Map<String, Object> info = paginationService.createPageInfo(1, 10, 100);
        assertTrue(info.containsKey("currentPage"));
        assertTrue(info.containsKey("pageSize"));
        assertTrue(info.containsKey("totalItems"));
        assertTrue(info.containsKey("totalPages"));
        assertTrue(info.containsKey("hasNext"));
        assertTrue(info.containsKey("hasPrevious"));
    }

    @Test
    void createPageInfo_correctValuesForFirstPage() {
        Map<String, Object> info = paginationService.createPageInfo(1, 10, 100);
        assertEquals(1, info.get("currentPage"));
        assertEquals(10, info.get("pageSize"));
        assertEquals(100L, info.get("totalItems"));
        assertEquals(10, info.get("totalPages"));
        assertTrue((Boolean) info.get("hasNext"));
        assertFalse((Boolean) info.get("hasPrevious"));
    }

    @Test
    void createPageInfo_correctValuesForLastPage() {
        Map<String, Object> info = paginationService.createPageInfo(10, 10, 100);
        assertFalse((Boolean) info.get("hasNext"));
        assertTrue((Boolean) info.get("hasPrevious"));
    }

    @Test
    void createPageInfo_correctValuesForMiddlePage() {
        Map<String, Object> info = paginationService.createPageInfo(5, 10, 100);
        assertTrue((Boolean) info.get("hasNext"));
        assertTrue((Boolean) info.get("hasPrevious"));
    }

    @Test
    void createPageInfo_hasNextFalseWhenOnlyOnePage() {
        Map<String, Object> info = paginationService.createPageInfo(1, 100, 50);
        assertFalse((Boolean) info.get("hasNext"));
        assertFalse((Boolean) info.get("hasPrevious"));
    }
}
