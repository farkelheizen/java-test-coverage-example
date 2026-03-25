package com.example.model;

public class Category {
    private String categoryId;
    private String name;
    private Long parentCategoryId;
    private String description;

    public Category() {}

    public Category(String categoryId, String name, Long parentCategoryId, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.parentCategoryId = parentCategoryId;
        this.description = description;
    }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getParentCategoryId() { return parentCategoryId; }
    public void setParentCategoryId(Long parentCategoryId) { this.parentCategoryId = parentCategoryId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
