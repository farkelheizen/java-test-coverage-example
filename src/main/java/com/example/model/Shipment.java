package com.example.model;

import java.time.LocalDate;

public class Shipment {
    private String shipmentId;
    private Order order;
    private String trackingNumber;
    private String carrier;
    private LocalDate estimatedDelivery;
    private LocalDate actualDelivery;
    private boolean isDelivered;

    public Shipment() {}

    public Shipment(String shipmentId, Order order, String trackingNumber, String carrier,
                    LocalDate estimatedDelivery, LocalDate actualDelivery, boolean isDelivered) {
        this.shipmentId = shipmentId;
        this.order = order;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.estimatedDelivery = estimatedDelivery;
        this.actualDelivery = actualDelivery;
        this.isDelivered = isDelivered;
    }

    public String getShipmentId() { return shipmentId; }
    public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public LocalDate getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(LocalDate estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
    public LocalDate getActualDelivery() { return actualDelivery; }
    public void setActualDelivery(LocalDate actualDelivery) { this.actualDelivery = actualDelivery; }
    public boolean isDelivered() { return isDelivered; }
    public void setDelivered(boolean delivered) { isDelivered = delivered; }
}
