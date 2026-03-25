package com.example.model;

public class Truck extends Vehicle {
    private double payloadCapacity;
    private boolean hasTowHitch;

    public Truck() {}

    public Truck(String vehicleId, String make, String model, int year, String vin, String color,
                 double mileage, double payloadCapacity, boolean hasTowHitch) {
        super(vehicleId, make, model, year, vin, color, mileage);
        this.payloadCapacity = payloadCapacity;
        this.hasTowHitch = hasTowHitch;
    }

    public double getPayloadCapacity() { return payloadCapacity; }
    public void setPayloadCapacity(double payloadCapacity) { this.payloadCapacity = payloadCapacity; }
    public boolean isHasTowHitch() { return hasTowHitch; }
    public void setHasTowHitch(boolean hasTowHitch) { this.hasTowHitch = hasTowHitch; }
}
