package com.example.model;

public class ProductVariant {
    private String variantId;
    private Product product;
    private String color;
    private String size;
    private double additionalPrice;

    public ProductVariant() {}

    public ProductVariant(String variantId, Product product, String color, String size, double additionalPrice) {
        this.variantId = variantId;
        this.product = product;
        this.color = color;
        this.size = size;
        this.additionalPrice = additionalPrice;
    }

    public String getVariantId() { return variantId; }
    public void setVariantId(String variantId) { this.variantId = variantId; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public double getAdditionalPrice() { return additionalPrice; }
    public void setAdditionalPrice(double additionalPrice) { this.additionalPrice = additionalPrice; }
}
