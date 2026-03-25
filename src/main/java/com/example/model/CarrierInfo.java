package com.example.model;

public class CarrierInfo {
    private String carrierId;
    private String name;
    private PhoneNumber phone;
    private String trackingUrlTemplate;

    public CarrierInfo() {}

    public CarrierInfo(String carrierId, String name, PhoneNumber phone, String trackingUrlTemplate) {
        this.carrierId = carrierId;
        this.name = name;
        this.phone = phone;
        this.trackingUrlTemplate = trackingUrlTemplate;
    }

    public String getTrackingUrl(String trackingNumber) {
        return trackingUrlTemplate.replace("{tracking}", trackingNumber);
    }

    public String getCarrierId() { return carrierId; }
    public void setCarrierId(String carrierId) { this.carrierId = carrierId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public PhoneNumber getPhone() { return phone; }
    public void setPhone(PhoneNumber phone) { this.phone = phone; }
    public String getTrackingUrlTemplate() { return trackingUrlTemplate; }
    public void setTrackingUrlTemplate(String trackingUrlTemplate) { this.trackingUrlTemplate = trackingUrlTemplate; }
}
