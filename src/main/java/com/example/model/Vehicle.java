package com.example.model;

public class Vehicle {
    private String vehicleId;
    private String make;
    private String model;
    private int year;
    private String vin;
    private String color;
    private double mileage;

    public Vehicle() {}

    public Vehicle(String vehicleId, String make, String model, int year,
                   String vin, String color, double mileage) {
        this.vehicleId = vehicleId;
        this.make = make;
        this.model = model;
        this.year = year;
        this.vin = vin;
        this.color = color;
        this.mileage = mileage;
    }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public double getMileage() { return mileage; }
    public void setMileage(double mileage) { this.mileage = mileage; }
}
