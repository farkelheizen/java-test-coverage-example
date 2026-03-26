package com.example.service;

import com.example.enums.ProductCategory;
import com.example.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @InjectMocks
    private SearchService searchService;

    // ------------------------------------------------------------------ searchProducts

    @Test
    void searchProducts_queryMatchesName_returnsProduct() {
        Product p = buildProduct("P1", "Laptop Pro", "Fast laptop", ProductCategory.ELECTRONICS, 999.0);
        List<Product> result = searchService.searchProducts(
                Collections.singletonList(p), "laptop", null, null);
        assertEquals(1, result.size());
    }

    @Test
    void searchProducts_queryMatchesDescription_returnsProduct() {
        Product p = buildProduct("P1", "Notebook", "Great for students", ProductCategory.BOOKS, 25.0);
        List<Product> result = searchService.searchProducts(
                Collections.singletonList(p), "students", null, null);
        assertEquals(1, result.size());
    }

    @Test
    void searchProducts_queryCaseInsensitive_returnsProduct() {
        Product p = buildProduct("P1", "TABLET", "Touch screen", ProductCategory.ELECTRONICS, 500.0);
        List<Product> result = searchService.searchProducts(
                Collections.singletonList(p), "tablet", null, null);
        assertEquals(1, result.size());
    }

    @Test
    void searchProducts_queryNotInNameOrDescription_excludesProduct() {
        Product p = buildProduct("P1", "Chair", "Wooden chair", ProductCategory.FURNITURE, 150.0);
        List<Product> result = searchService.searchProducts(
                Collections.singletonList(p), "laptop", null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchProducts_nullQuery_returnsAllProducts() {
        Product p1 = buildProduct("P1", "Shoe", "Running shoe", ProductCategory.SPORTS, 80.0);
        Product p2 = buildProduct("P2", "Hat", "Summer hat", ProductCategory.CLOTHING, 30.0);
        List<Product> result = searchService.searchProducts(
                Arrays.asList(p1, p2), null, null, null);
        assertEquals(2, result.size());
    }

    @Test
    void searchProducts_emptyQuery_returnsAllProducts() {
        Product p = buildProduct("P1", "Book", "A great book", ProductCategory.BOOKS, 15.0);
        List<Product> result = searchService.searchProducts(
                Collections.singletonList(p), "", null, null);
        assertEquals(1, result.size());
    }

    @Test
    void searchProducts_categoryFilter_excludesWrongCategory() {
        Product electronics = buildProduct("P1", "Phone", "Smartphone", ProductCategory.ELECTRONICS, 700.0);
        Product books       = buildProduct("P2", "Novel", "Great novel", ProductCategory.BOOKS, 20.0);
        List<Product> result = searchService.searchProducts(
                Arrays.asList(electronics, books), null, ProductCategory.BOOKS, null);
        assertEquals(1, result.size());
        assertEquals("P2", result.get(0).getProductId());
    }

    @Test
    void searchProducts_maxPriceFilter_excludesExpensiveProducts() {
        Product cheap     = buildProduct("P1", "Pen", "Ballpoint", ProductCategory.BOOKS, 2.0);
        Product expensive = buildProduct("P2", "Camera", "DSLR", ProductCategory.ELECTRONICS, 800.0);
        List<Product> result = searchService.searchProducts(
                Arrays.asList(cheap, expensive), null, null, 100.0);
        assertEquals(1, result.size());
        assertEquals("P1", result.get(0).getProductId());
    }

    @Test
    void searchProducts_maxPriceBoundary_productAtExactPriceIncluded() {
        Product p = buildProduct("P1", "Widget", "A widget", ProductCategory.ELECTRONICS, 50.0);
        List<Product> result = searchService.searchProducts(
                Collections.singletonList(p), null, null, 50.0);
        assertEquals(1, result.size());
    }

    @Test
    void searchProducts_allFiltersApplied_returnsOnlyMatching() {
        Product match   = buildProduct("P1", "Laptop", "Fast laptop", ProductCategory.ELECTRONICS, 800.0);
        Product wrongCat= buildProduct("P2", "Laptop Bag", "For laptops", ProductCategory.CLOTHING, 50.0);
        Product tooExp  = buildProduct("P3", "Laptop Pro", "Top laptop", ProductCategory.ELECTRONICS, 2000.0);
        Product noMatch = buildProduct("P4", "Phone", "A phone", ProductCategory.ELECTRONICS, 700.0);

        List<Product> result = searchService.searchProducts(
                Arrays.asList(match, wrongCat, tooExp, noMatch),
                "laptop", ProductCategory.ELECTRONICS, 1000.0);

        assertEquals(1, result.size());
        assertEquals("P1", result.get(0).getProductId());
    }

    @Test
    void searchProducts_emptyProductList_returnsEmpty() {
        List<Product> result = searchService.searchProducts(
                Collections.emptyList(), "query", ProductCategory.BOOKS, 100.0);
        assertTrue(result.isEmpty());
    }

    // ------------------------------------------------------------------ sortResults

    @Test
    void sortResults_byPriceAscending() {
        Product cheap     = buildProduct("P1", "A", "desc", ProductCategory.BOOKS, 10.0);
        Product expensive = buildProduct("P2", "B", "desc", ProductCategory.BOOKS, 100.0);
        List<Product> result = searchService.sortResults(
                Arrays.asList(expensive, cheap), "price", true);
        assertEquals("P1", result.get(0).getProductId());
        assertEquals("P2", result.get(1).getProductId());
    }

    @Test
    void sortResults_byPriceDescending() {
        Product cheap     = buildProduct("P1", "A", "desc", ProductCategory.BOOKS, 10.0);
        Product expensive = buildProduct("P2", "B", "desc", ProductCategory.BOOKS, 100.0);
        List<Product> result = searchService.sortResults(
                Arrays.asList(cheap, expensive), "price", false);
        assertEquals("P2", result.get(0).getProductId());
        assertEquals("P1", result.get(1).getProductId());
    }

    @Test
    void sortResults_byNameAscending() {
        Product apple  = buildProduct("P1", "Apple", "desc", ProductCategory.FOOD, 1.0);
        Product banana = buildProduct("P2", "Banana", "desc", ProductCategory.FOOD, 2.0);
        List<Product> result = searchService.sortResults(
                Arrays.asList(banana, apple), "name", true);
        assertEquals("P1", result.get(0).getProductId());
    }

    @Test
    void sortResults_byNameDescending() {
        Product apple  = buildProduct("P1", "Apple", "desc", ProductCategory.FOOD, 1.0);
        Product banana = buildProduct("P2", "Banana", "desc", ProductCategory.FOOD, 2.0);
        List<Product> result = searchService.sortResults(
                Arrays.asList(apple, banana), "name", false);
        assertEquals("P2", result.get(0).getProductId());
    }

    @Test
    void sortResults_defaultSortByProductIdAscending() {
        Product p1 = buildProduct("AAA", "Z Product", "desc", ProductCategory.BOOKS, 5.0);
        Product p2 = buildProduct("BBB", "A Product", "desc", ProductCategory.BOOKS, 50.0);
        List<Product> result = searchService.sortResults(
                Arrays.asList(p2, p1), "unknown_field", true);
        assertEquals("AAA", result.get(0).getProductId());
    }

    @Test
    void sortResults_defaultSortDescending() {
        Product p1 = buildProduct("AAA", "Z Product", "desc", ProductCategory.BOOKS, 5.0);
        Product p2 = buildProduct("BBB", "A Product", "desc", ProductCategory.BOOKS, 50.0);
        List<Product> result = searchService.sortResults(
                Arrays.asList(p1, p2), "unknown_field", false);
        assertEquals("BBB", result.get(0).getProductId());
    }

    @Test
    void sortResults_emptyList_returnsEmpty() {
        List<Product> result = searchService.sortResults(Collections.emptyList(), "price", true);
        assertTrue(result.isEmpty());
    }

    // ------------------------------------------------------------------ filterByPriceRange

    @Test
    void filterByPriceRange_returnsProductsWithinRange() {
        Product p10  = buildProduct("P1", "A", "desc", ProductCategory.BOOKS, 10.0);
        Product p50  = buildProduct("P2", "B", "desc", ProductCategory.BOOKS, 50.0);
        Product p200 = buildProduct("P3", "C", "desc", ProductCategory.BOOKS, 200.0);

        List<Product> result = searchService.filterByPriceRange(
                Arrays.asList(p10, p50, p200), 20.0, 100.0);

        assertEquals(1, result.size());
        assertEquals("P2", result.get(0).getProductId());
    }

    @Test
    void filterByPriceRange_productAtMinBoundary_included() {
        Product p = buildProduct("P1", "Item", "desc", ProductCategory.SPORTS, 20.0);
        List<Product> result = searchService.filterByPriceRange(
                Collections.singletonList(p), 20.0, 100.0);
        assertEquals(1, result.size());
    }

    @Test
    void filterByPriceRange_productAtMaxBoundary_included() {
        Product p = buildProduct("P1", "Item", "desc", ProductCategory.SPORTS, 100.0);
        List<Product> result = searchService.filterByPriceRange(
                Collections.singletonList(p), 20.0, 100.0);
        assertEquals(1, result.size());
    }

    @Test
    void filterByPriceRange_noProductsInRange_returnsEmpty() {
        Product p = buildProduct("P1", "Item", "desc", ProductCategory.SPORTS, 500.0);
        List<Product> result = searchService.filterByPriceRange(
                Collections.singletonList(p), 20.0, 100.0);
        assertTrue(result.isEmpty());
    }

    @Test
    void filterByPriceRange_emptyList_returnsEmpty() {
        List<Product> result = searchService.filterByPriceRange(
                Collections.emptyList(), 0.0, 1000.0);
        assertTrue(result.isEmpty());
    }

    // ------------------------------------------------------------------ helpers

    private Product buildProduct(String id, String name, String description,
                                  ProductCategory category, double price) {
        Product p = new Product();
        p.setProductId(id);
        p.setName(name);
        p.setDescription(description);
        p.setCategory(category);
        p.setPrice(price);
        return p;
    }
}
