package com.example.service;

import com.example.model.Order;
import com.example.model.Shipment;
import java.time.LocalDate;

public class ShippingService {

    public double calculateShippingRate(double weight, String destination, boolean isExpress) {
        double baseRate;
        if (weight <= 1.0) {
            baseRate = 5.99;
        } else if (weight <= 5.0) {
            baseRate = 9.99;
        } else if (weight <= 20.0) {
            baseRate = 19.99;
        } else {
            baseRate = 39.99;
        }

        if ("INTERNATIONAL".equalsIgnoreCase(destination)) {
            baseRate *= 3.0;
        } else if ("REMOTE".equalsIgnoreCase(destination)) {
            baseRate *= 1.5;
        }

        if (isExpress) {
            baseRate *= 2.0;
        }
        return baseRate;
    }

    public Shipment createShipment(Order order, String carrier) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (carrier == null || carrier.isEmpty()) {
            throw new IllegalArgumentException("Carrier cannot be null or empty");
        }
        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setCarrier(carrier);
        shipment.setTrackingNumber("TRK" + System.currentTimeMillis());
        shipment.setEstimatedDelivery(LocalDate.now().plusDays(5));
        shipment.setDelivered(false);
        return shipment;
    }

    public int estimateDeliveryDays(String destination, boolean isExpress) {
        int days;
        if ("LOCAL".equalsIgnoreCase(destination)) {
            days = 1;
        } else if ("DOMESTIC".equalsIgnoreCase(destination)) {
            days = 3;
        } else if ("REGIONAL".equalsIgnoreCase(destination)) {
            days = 5;
        } else if ("INTERNATIONAL".equalsIgnoreCase(destination)) {
            days = 14;
        } else {
            days = 7;
        }
        return isExpress ? Math.max(1, days / 2) : days;
    }
}
