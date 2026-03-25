package com.example.service;

import com.example.model.Inventory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryManagementService {

    public int calculateReorderPoint(double dailyDemand, int leadTimeDays, int safetyStock) {
        return (int) Math.ceil(dailyDemand * leadTimeDays) + safetyStock;
    }

    public int suggestReorderQuantity(Inventory inventory, double dailyDemand, int leadTime) {
        double holdingCostFactor = 0.2;
        double orderingCost = 50.0;
        double annualDemand = dailyDemand * 365;
        // Economic Order Quantity formula
        double eoq = Math.sqrt((2 * annualDemand * orderingCost) / holdingCostFactor);
        return (int) Math.ceil(eoq);
    }

    public List<Inventory> identifyLowStockItems(List<Inventory> inventories,
                                                   Map<Long, Integer> reorderPoints) {
        List<Inventory> lowStock = new ArrayList<>();
        for (Inventory inv : inventories) {
            long warehouseId = inv.getWarehouseId();
            Integer reorderPoint = reorderPoints.get(warehouseId);
            if (reorderPoint != null && inv.getQuantity() <= reorderPoint) {
                lowStock.add(inv);
            }
        }
        return lowStock;
    }

    public double calculateTurnoverRate(int unitsSold, int averageInventory) {
        if (averageInventory <= 0) return 0.0;
        return (double) unitsSold / averageInventory;
    }
}
