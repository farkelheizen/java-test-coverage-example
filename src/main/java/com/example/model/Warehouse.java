package com.example.model;

public class Warehouse {
    private String warehouseId;
    private String name;
    private Address address;
    private int capacity;
    private int currentLoad;

    public Warehouse() {}

    public Warehouse(String warehouseId, String name, Address address, int capacity, int currentLoad) {
        this.warehouseId = warehouseId;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.currentLoad = currentLoad;
    }

    public int getAvailableCapacity() {
        return capacity - currentLoad;
    }

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getCurrentLoad() { return currentLoad; }
    public void setCurrentLoad(int currentLoad) { this.currentLoad = currentLoad; }
}
