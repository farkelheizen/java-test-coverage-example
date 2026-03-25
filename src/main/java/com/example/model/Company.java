package com.example.model;

public class Company {
    private String companyId;
    private String name;
    private Address address;
    private PhoneNumber phone;
    private Email email;
    private int foundedYear;

    public Company() {}

    public Company(String companyId, String name, Address address, PhoneNumber phone,
                   Email email, int foundedYear) {
        this.companyId = companyId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.foundedYear = foundedYear;
    }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public PhoneNumber getPhone() { return phone; }
    public void setPhone(PhoneNumber phone) { this.phone = phone; }
    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }
    public int getFoundedYear() { return foundedYear; }
    public void setFoundedYear(int foundedYear) { this.foundedYear = foundedYear; }
}
