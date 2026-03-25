package com.example.model;

public class Department {
    private String departmentId;
    private String name;
    private long managerId;
    private double budget;

    public Department() {}

    public Department(String departmentId, String name, long managerId, double budget) {
        this.departmentId = departmentId;
        this.name = name;
        this.managerId = managerId;
        this.budget = budget;
    }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getManagerId() { return managerId; }
    public void setManagerId(long managerId) { this.managerId = managerId; }
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
}
