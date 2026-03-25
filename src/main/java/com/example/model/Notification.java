package com.example.model;

import java.time.LocalDateTime;

public class Notification {
    private String notificationId;
    private long recipientId;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private String channel;

    public Notification() {}

    public Notification(String notificationId, long recipientId, String title, String message,
                        boolean isRead, LocalDateTime createdAt, String channel) {
        this.notificationId = notificationId;
        this.recipientId = recipientId;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.channel = channel;
    }

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public long getRecipientId() { return recipientId; }
    public void setRecipientId(long recipientId) { this.recipientId = recipientId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}
