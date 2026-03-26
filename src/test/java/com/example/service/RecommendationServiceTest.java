package com.example.service;

import com.example.enums.ProductCategory;
import com.example.model.Customer;
import com.example.model.Order;
import com.example.model.OrderItem;
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
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    // ------------------------------------------------------------------ getRelatedProducts

    @Test
    void getRelatedProducts_returnsProductsInSameCategoryAndPriceRange() {
        Product source = buildProduct("P1", ProductCategory.ELECTRONICS, 100.0);
        Product match  = buildProduct("P2", ProductCategory.ELECTRONICS, 110.0); // within ±30%
        Product tooExp = buildProduct("P3", ProductCategory.ELECTRONICS, 200.0); // too expensive
        Product diffCat= buildProduct("P4", ProductCategory.BOOKS, 100.0);       // different category

        List<Product> result = recommendationService.getRelatedProducts(
                source, Arrays.asList(source, match, tooExp, diffCat));

        assertEquals(1, result.size());
        assertEquals("P2", result.get(0).getProductId());
    }

    @Test
    void getRelatedProducts_excludesSourceProduct() {
        Product source = buildProduct("P1", ProductCategory.ELECTRONICS, 100.0);
        List<Product> result = recommendationService.getRelatedProducts(
                source, Collections.singletonList(source));
        assertTrue(result.isEmpty());
    }

    @Test
    void getRelatedProducts_emptyProductCatalogReturnsEmpty() {
        Product source = buildProduct("P1", ProductCategory.BOOKS, 50.0);
        List<Product> result = recommendationService.getRelatedProducts(
                source, Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void getRelatedProducts_lowerBoundaryPrice_included() {
        Product source = buildProduct("P1", ProductCategory.CLOTHING, 100.0);
        Product boundary = buildProduct("P2", ProductCategory.CLOTHING, 70.0); // exactly 70% of 100
        List<Product> result = recommendationService.getRelatedProducts(
                source, Arrays.asList(source, boundary));
        assertEquals(1, result.size());
    }

    @Test
    void getRelatedProducts_upperBoundaryPrice_included() {
        Product source = buildProduct("P1", ProductCategory.CLOTHING, 100.0);
        Product boundary = buildProduct("P2", ProductCategory.CLOTHING, 130.0); // exactly 130% of 100
        List<Product> result = recommendationService.getRelatedProducts(
                source, Arrays.asList(source, boundary));
        assertEquals(1, result.size());
    }

    // ------------------------------------------------------------------ getPersonalizedRecommendations

    @Test
    void getPersonalizedRecommendations_excludesPreviouslyPurchasedProducts() {
        Product purchased = buildProduct("P1", ProductCategory.ELECTRONICS, 100.0);
        Product fresh     = buildProduct("P2", ProductCategory.ELECTRONICS, 100.0);

        Order order = new Order();
        OrderItem item = new OrderItem("OI1", purchased, 1, 100.0);
        order.setItems(Collections.singletonList(item));

        List<Product> result = recommendationService.getPersonalizedRecommendations(
                new Customer(), Collections.singletonList(order),
                Arrays.asList(purchased, fresh));

        assertEquals(1, result.size());
        assertEquals("P2", result.get(0).getProductId());
    }

    @Test
    void getPersonalizedRecommendations_emptyHistoryReturnsAllCatalogUpToLimit() {
        List<Product> catalog = Arrays.asList(
                buildProduct("P1", ProductCategory.BOOKS, 10.0),
                buildProduct("P2", ProductCategory.FOOD, 5.0)
        );

        List<Product> result = recommendationService.getPersonalizedRecommendations(
                new Customer(), Collections.emptyList(), catalog);

        // No top category -> all catalog products returned (up to 10)
        assertEquals(2, result.size());
    }

    @Test
    void getPersonalizedRecommendations_filtersToTopCategoryFromHistory() {
        Product electronics = buildProduct("E1", ProductCategory.ELECTRONICS, 100.0);
        Product books       = buildProduct("B1", ProductCategory.BOOKS, 20.0);

        // Two orders with ELECTRONICS items
        Order o1 = new Order();
        o1.setItems(Collections.singletonList(new OrderItem("OI1", buildProduct("X1", ProductCategory.ELECTRONICS, 50.0), 1, 50.0)));
        Order o2 = new Order();
        o2.setItems(Collections.singletonList(new OrderItem("OI2", buildProduct("X2", ProductCategory.ELECTRONICS, 60.0), 1, 60.0)));

        List<Product> result = recommendationService.getPersonalizedRecommendations(
                new Customer(), Arrays.asList(o1, o2),
                Arrays.asList(electronics, books));

        assertEquals(1, result.size());
        assertEquals("E1", result.get(0).getProductId());
    }

    @Test
    void getPersonalizedRecommendations_orderWithNullItemsIsSkipped() {
        Order orderWithNullItems = new Order();
        orderWithNullItems.setItems(null);

        Product p = buildProduct("P1", ProductCategory.SPORTS, 40.0);

        List<Product> result = recommendationService.getPersonalizedRecommendations(
                new Customer(), Collections.singletonList(orderWithNullItems),
                Collections.singletonList(p));

        // null items skipped, topCategory stays null -> all products included
        assertEquals(1, result.size());
    }

    @Test
    void getPersonalizedRecommendations_limitsResultsTo10() {
        List<Product> catalog = Arrays.asList(
                buildProduct("P1",  ProductCategory.SPORTS, 10.0),
                buildProduct("P2",  ProductCategory.SPORTS, 10.0),
                buildProduct("P3",  ProductCategory.SPORTS, 10.0),
                buildProduct("P4",  ProductCategory.SPORTS, 10.0),
                buildProduct("P5",  ProductCategory.SPORTS, 10.0),
                buildProduct("P6",  ProductCategory.SPORTS, 10.0),
                buildProduct("P7",  ProductCategory.SPORTS, 10.0),
                buildProduct("P8",  ProductCategory.SPORTS, 10.0),
                buildProduct("P9",  ProductCategory.SPORTS, 10.0),
                buildProduct("P10", ProductCategory.SPORTS, 10.0),
                buildProduct("P11", ProductCategory.SPORTS, 10.0)
        );

        List<Product> result = recommendationService.getPersonalizedRecommendations(
                new Customer(), Collections.emptyList(), catalog);

        assertEquals(10, result.size());
    }

    // ------------------------------------------------------------------ getBestSellers

    @Test
    void getBestSellers_returnsTopNByQuantity() {
        Product p1 = buildProduct("P1", ProductCategory.ELECTRONICS, 100.0);
        Product p2 = buildProduct("P2", ProductCategory.ELECTRONICS, 200.0);
        Product p3 = buildProduct("P3", ProductCategory.ELECTRONICS, 300.0);

        List<OrderItem> items = Arrays.asList(
                new OrderItem("OI1", p1, 5, 100.0),
                new OrderItem("OI2", p2, 10, 200.0),
                new OrderItem("OI3", p3, 2, 300.0),
                new OrderItem("OI4", p1, 3, 100.0)  // p1 now has qty 8
        );

        List<Product> result = recommendationService.getBestSellers(items, 2);

        assertEquals(2, result.size());
        assertEquals("P2", result.get(0).getProductId()); // qty 10
        assertEquals("P1", result.get(1).getProductId()); // qty 8
    }

    @Test
    void getBestSellers_emptyItemsReturnsEmpty() {
        List<Product> result = recommendationService.getBestSellers(Collections.emptyList(), 5);
        assertTrue(result.isEmpty());
    }

    @Test
    void getBestSellers_topNLargerThanAvailableReturnAll() {
        Product p1 = buildProduct("P1", ProductCategory.BOOKS, 20.0);
        List<OrderItem> items = Collections.singletonList(new OrderItem("OI1", p1, 3, 20.0));

        List<Product> result = recommendationService.getBestSellers(items, 10);
        assertEquals(1, result.size());
    }

    // ------------------------------------------------------------------ helpers

    private Product buildProduct(String id, ProductCategory category, double price) {
        Product p = new Product();
        p.setProductId(id);
        p.setName("Name-" + id);
        p.setDescription("Desc-" + id);
        p.setCategory(category);
        p.setPrice(price);
        return p;
    }
}
