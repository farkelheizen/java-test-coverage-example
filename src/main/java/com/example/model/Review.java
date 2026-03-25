package com.example.model;

import java.time.LocalDateTime;

public class Review {
    private String reviewId;
    private Product product;
    private Customer customer;
    private int rating;
    private String title;
    private String content;
    private LocalDateTime reviewDate;
    private boolean isVerifiedPurchase;

    public Review() {}

    public Review(String reviewId, Product product, Customer customer, int rating,
                  String title, String content, LocalDateTime reviewDate, boolean isVerifiedPurchase) {
        this.reviewId = reviewId;
        this.product = product;
        this.customer = customer;
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.reviewDate = reviewDate;
        this.isVerifiedPurchase = isVerifiedPurchase;
    }

    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }
    public boolean isVerifiedPurchase() { return isVerifiedPurchase; }
    public void setVerifiedPurchase(boolean verifiedPurchase) { isVerifiedPurchase = verifiedPurchase; }
}
