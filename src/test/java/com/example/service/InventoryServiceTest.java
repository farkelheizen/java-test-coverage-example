package com.example.service;

import com.example.model.Inventory;
import com.example.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService();
    }

    private Inventory buildInventory(int quantity) {
        return new Inventory("INV-1", new Product(), quantity, 1L, LocalDateTime.now().minusDays(1));
    }

    // ── checkAvailability ─────────────────────────────────────────────────────

    @Test
    void checkAvailability_sufficientStock_returnsTrue() {
        Inventory inventory = buildInventory(10);
        assertTrue(inventoryService.checkAvailability(inventory, 5));
    }

    @Test
    void checkAvailability_exactStock_returnsTrue() {
        Inventory inventory = buildInventory(5);
        assertTrue(inventoryService.checkAvailability(inventory, 5));
    }

    @Test
    void checkAvailability_insufficientStock_returnsFalse() {
        Inventory inventory = buildInventory(3);
        assertFalse(inventoryService.checkAvailability(inventory, 5));
    }

    @Test
    void checkAvailability_zeroStock_returnsFalse() {
        Inventory inventory = buildInventory(0);
        assertFalse(inventoryService.checkAvailability(inventory, 1));
    }

    // ── reserveStock ──────────────────────────────────────────────────────────

    @Test
    void reserveStock_sufficientStock_reducesQuantity() {
        Inventory inventory = buildInventory(10);
        inventoryService.reserveStock(inventory, 3);
        assertEquals(7, inventory.getQuantity());
    }

    @Test
    void reserveStock_exactStock_reducesToZero() {
        Inventory inventory = buildInventory(5);
        inventoryService.reserveStock(inventory, 5);
        assertEquals(0, inventory.getQuantity());
    }

    @Test
    void reserveStock_sufficientStock_updatesLastUpdated() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        Inventory inventory = buildInventory(10);
        inventoryService.reserveStock(inventory, 2);
        assertNotNull(inventory.getLastUpdated());
        assertTrue(inventory.getLastUpdated().isAfter(before));
    }

    @Test
    void reserveStock_insufficientStock_throwsIllegalStateException() {
        Inventory inventory = buildInventory(2);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> inventoryService.reserveStock(inventory, 5));
        assertTrue(ex.getMessage().contains("Insufficient stock"));
    }

    @Test
    void reserveStock_zeroStock_throwsIllegalStateException() {
        Inventory inventory = buildInventory(0);
        assertThrows(IllegalStateException.class,
                () -> inventoryService.reserveStock(inventory, 1));
    }

    // ── releaseStock ──────────────────────────────────────────────────────────

    @Test
    void releaseStock_addsQuantityToInventory() {
        Inventory inventory = buildInventory(5);
        inventoryService.releaseStock(inventory, 3);
        assertEquals(8, inventory.getQuantity());
    }

    @Test
    void releaseStock_fromZero_setsCorrectQuantity() {
        Inventory inventory = buildInventory(0);
        inventoryService.releaseStock(inventory, 10);
        assertEquals(10, inventory.getQuantity());
    }

    @Test
    void releaseStock_updatesLastUpdated() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        Inventory inventory = buildInventory(5);
        inventoryService.releaseStock(inventory, 5);
        assertNotNull(inventory.getLastUpdated());
        assertTrue(inventory.getLastUpdated().isAfter(before));
    }
}
