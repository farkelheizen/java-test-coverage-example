package com.example.model;

import java.time.LocalDate;

public class Person {
    private long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Email email;
    private PhoneNumber phone;
    private Address address;

    public Person() {}

    public Person(long id, String firstName, String lastName, LocalDate dateOfBirth,
                  Email email, PhoneNumber phone, Address address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }
    public PhoneNumber getPhone() { return phone; }
    public void setPhone(PhoneNumber phone) { this.phone = phone; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}
