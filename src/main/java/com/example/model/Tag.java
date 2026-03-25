package com.example.model;

public class Tag {
    private String tagId;
    private String name;
    private String color;

    public Tag() {}

    public Tag(String tagId, String name, String color) {
        this.tagId = tagId;
        this.name = name;
        this.color = color;
    }

    public String getTagId() { return tagId; }
    public void setTagId(String tagId) { this.tagId = tagId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
