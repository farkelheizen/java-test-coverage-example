package com.example.model;

public class Email {
    private String localPart;
    private String domain;

    public Email() {}

    public Email(String localPart, String domain) {
        this.localPart = localPart;
        this.domain = domain;
    }

    public String getFullAddress() {
        return localPart + "@" + domain;
    }

    public String getLocalPart() { return localPart; }
    public void setLocalPart(String localPart) { this.localPart = localPart; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
}
