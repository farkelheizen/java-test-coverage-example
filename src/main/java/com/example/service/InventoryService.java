package com.example.service;

import com.example.model.Inventory;
import java.time.LocalDateTime;

public class InventoryService {

    public boolean checkAvailability(Inventory inventory, int requestedQty) {
        return inventory.getQuantity() >= requestedQty;
    }

    public void reserveStock(Inventory inventory, int quantity) {
        if (!checkAvailability(inventory, quantity)) {
            throw new IllegalStateException("Insufficient stock. Available: " + inventory.getQuantity() + ", requested: " + quantity);
        }
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventory.setLastUpdated(LocalDateTime.now());
    }

    public void releaseStock(Inventory inventory, int quantity) {
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.setLastUpdated(LocalDateTime.now());
    }
}
