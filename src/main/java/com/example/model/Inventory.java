package com.example.model;

import java.time.LocalDateTime;

public class Inventory {
    private String inventoryId;
    private Product product;
    private int quantity;
    private long warehouseId;
    private LocalDateTime lastUpdated;

    public Inventory() {}

    public Inventory(String inventoryId, Product product, int quantity,
                     long warehouseId, LocalDateTime lastUpdated) {
        this.inventoryId = inventoryId;
        this.product = product;
        this.quantity = quantity;
        this.warehouseId = warehouseId;
        this.lastUpdated = lastUpdated;
    }

    public String getInventoryId() { return inventoryId; }
    public void setInventoryId(String inventoryId) { this.inventoryId = inventoryId; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(long warehouseId) { this.warehouseId = warehouseId; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
