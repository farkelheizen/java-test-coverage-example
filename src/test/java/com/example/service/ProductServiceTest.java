package com.example.service;

import com.example.enums.ProductCategory;
import com.example.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService();
    }

    private Product buildProduct(String name, double price, int stock) {
        return new Product("P1", name, "desc", price, stock, ProductCategory.ELECTRONICS, "SKU1");
    }

    // ── addProduct ────────────────────────────────────────────────────────────

    @Test
    void addProduct_validProduct_returnsProduct() {
        Product product = buildProduct("Widget", 9.99, 10);
        assertSame(product, productService.addProduct(product));
    }

    @Test
    void addProduct_nullName_throwsIllegalArgumentException() {
        Product product = buildProduct(null, 9.99, 10);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addProduct(product));
        assertTrue(ex.getMessage().contains("name"));
    }

    @Test
    void addProduct_emptyName_throwsIllegalArgumentException() {
        Product product = buildProduct("", 9.99, 10);
        assertThrows(IllegalArgumentException.class,
                () -> productService.addProduct(product));
    }

    @Test
    void addProduct_zeroPrice_throwsIllegalArgumentException() {
        Product product = buildProduct("Widget", 0.0, 10);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addProduct(product));
        assertTrue(ex.getMessage().contains("price"));
    }

    @Test
    void addProduct_negativePrice_throwsIllegalArgumentException() {
        Product product = buildProduct("Widget", -1.0, 10);
        assertThrows(IllegalArgumentException.class,
                () -> productService.addProduct(product));
    }

    @Test
    void addProduct_negativeStock_throwsIllegalArgumentException() {
        Product product = buildProduct("Widget", 9.99, -1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addProduct(product));
        assertTrue(ex.getMessage().contains("Stock"));
    }

    @Test
    void addProduct_zeroStock_succeeds() {
        Product product = buildProduct("Widget", 9.99, 0);
        assertDoesNotThrow(() -> productService.addProduct(product));
    }

    // ── applyDiscount ─────────────────────────────────────────────────────────

    @Test
    void applyDiscount_twentyPercent_returnsCorrectPrice() {
        Product product = buildProduct("Widget", 100.0, 5);
        double newPrice = productService.applyDiscount(product, 20.0);
        assertEquals(80.0, newPrice, 0.0001);
    }

    @Test
    void applyDiscount_zeroPercent_returnsOriginalPrice() {
        Product product = buildProduct("Widget", 50.0, 5);
        double newPrice = productService.applyDiscount(product, 0.0);
        assertEquals(50.0, newPrice, 0.0001);
    }

    @Test
    void applyDiscount_hundredPercent_returnZero() {
        Product product = buildProduct("Widget", 50.0, 5);
        double newPrice = productService.applyDiscount(product, 100.0);
        assertEquals(0.0, newPrice, 0.0001);
    }

    @Test
    void applyDiscount_updatesProductPrice() {
        Product product = buildProduct("Widget", 100.0, 5);
        productService.applyDiscount(product, 10.0);
        assertEquals(90.0, product.getPrice(), 0.0001);
    }

    @Test
    void applyDiscount_negativePercent_throwsIllegalArgumentException() {
        Product product = buildProduct("Widget", 100.0, 5);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.applyDiscount(product, -5.0));
        assertTrue(ex.getMessage().contains("Discount"));
    }

    @Test
    void applyDiscount_moreThanHundredPercent_throwsIllegalArgumentException() {
        Product product = buildProduct("Widget", 100.0, 5);
        assertThrows(IllegalArgumentException.class,
                () -> productService.applyDiscount(product, 101.0));
    }

    // ── isInStock ─────────────────────────────────────────────────────────────

    @Test
    void isInStock_positiveQuantity_returnsTrue() {
        Product product = buildProduct("Widget", 9.99, 1);
        assertTrue(productService.isInStock(product));
    }

    @Test
    void isInStock_zeroQuantity_returnsFalse() {
        Product product = buildProduct("Widget", 9.99, 0);
        assertFalse(productService.isInStock(product));
    }

    // ── restockProduct ────────────────────────────────────────────────────────

    @Test
    void restockProduct_validQuantity_increasesStock() {
        Product product = buildProduct("Widget", 9.99, 5);
        productService.restockProduct(product, 10);
        assertEquals(15, product.getStockQuantity());
    }

    @Test
    void restockProduct_fromZero_setsCorrectQuantity() {
        Product product = buildProduct("Widget", 9.99, 0);
        productService.restockProduct(product, 20);
        assertEquals(20, product.getStockQuantity());
    }

    @Test
    void restockProduct_zeroQuantity_throwsIllegalArgumentException() {
        Product product = buildProduct("Widget", 9.99, 5);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.restockProduct(product, 0));
        assertTrue(ex.getMessage().contains("Restock"));
    }

    @Test
    void restockProduct_negativeQuantity_throwsIllegalArgumentException() {
        Product product = buildProduct("Widget", 9.99, 5);
        assertThrows(IllegalArgumentException.class,
                () -> productService.restockProduct(product, -3));
    }
}
