package com.example.model;

import com.example.enums.EmployeeStatus;
import java.time.LocalDate;

public class Employee extends Person {
    private String employeeId;
    private String department;
    private double salary;
    private EmployeeStatus status;
    private LocalDate hireDate;

    public Employee() {}

    public Employee(long id, String firstName, String lastName, LocalDate dateOfBirth,
                    Email email, PhoneNumber phone, Address address,
                    String employeeId, String department, double salary,
                    EmployeeStatus status, LocalDate hireDate) {
        super(id, firstName, lastName, dateOfBirth, email, phone, address);
        this.employeeId = employeeId;
        this.department = department;
        this.salary = salary;
        this.status = status;
        this.hireDate = hireDate;
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public EmployeeStatus getStatus() { return status; }
    public void setStatus(EmployeeStatus status) { this.status = status; }
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
}
