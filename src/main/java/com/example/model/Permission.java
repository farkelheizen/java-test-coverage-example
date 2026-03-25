package com.example.model;

public class Permission {
    private String permissionId;
    private String name;
    private String description;
    private String resource;

    public Permission() {}

    public Permission(String permissionId, String name, String description, String resource) {
        this.permissionId = permissionId;
        this.name = name;
        this.description = description;
        this.resource = resource;
    }

    public String getPermissionId() { return permissionId; }
    public void setPermissionId(String permissionId) { this.permissionId = permissionId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
}
