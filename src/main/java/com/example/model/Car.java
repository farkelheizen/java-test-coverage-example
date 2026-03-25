package com.example.model;

public class Car extends Vehicle {
    private int numDoors;
    private boolean hasSunroof;
    private String fuelType;

    public Car() {}

    public Car(String vehicleId, String make, String model, int year, String vin, String color,
               double mileage, int numDoors, boolean hasSunroof, String fuelType) {
        super(vehicleId, make, model, year, vin, color, mileage);
        this.numDoors = numDoors;
        this.hasSunroof = hasSunroof;
        this.fuelType = fuelType;
    }

    public int getNumDoors() { return numDoors; }
    public void setNumDoors(int numDoors) { this.numDoors = numDoors; }
    public boolean isHasSunroof() { return hasSunroof; }
    public void setHasSunroof(boolean hasSunroof) { this.hasSunroof = hasSunroof; }
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
}
