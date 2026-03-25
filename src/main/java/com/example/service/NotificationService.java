package com.example.service;

import com.example.model.Notification;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationService {

    public void sendNotification(Notification notification) {
        String channel = notification.getChannel();
        if ("EMAIL".equalsIgnoreCase(channel)) {
            sendViaEmail(notification);
        } else if ("SMS".equalsIgnoreCase(channel)) {
            sendViaSms(notification);
        } else if ("PUSH".equalsIgnoreCase(channel)) {
            sendViaPush(notification);
        } else {
            throw new IllegalArgumentException("Unknown channel: " + channel);
        }
    }

    private void sendViaEmail(Notification notification) {
        // Email delivery logic
    }

    private void sendViaSms(Notification notification) {
        // SMS delivery logic
    }

    private void sendViaPush(Notification notification) {
        // Push delivery logic
    }

    public void markAsRead(Notification notification) {
        notification.setRead(true);
    }

    public List<Notification> filterUnread(List<Notification> notifications) {
        return notifications.stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
    }
}
