package com.example.service;

import com.example.model.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    // ------------------------------------------------------------------ sendNotification

    @Test
    void sendNotification_emailChannelDoesNotThrow() {
        Notification n = buildNotification("EMAIL", false);
        assertDoesNotThrow(() -> notificationService.sendNotification(n));
    }

    @Test
    void sendNotification_emailChannelCaseInsensitive() {
        Notification n = buildNotification("email", false);
        assertDoesNotThrow(() -> notificationService.sendNotification(n));
    }

    @Test
    void sendNotification_smsChannelDoesNotThrow() {
        Notification n = buildNotification("SMS", false);
        assertDoesNotThrow(() -> notificationService.sendNotification(n));
    }

    @Test
    void sendNotification_smsCaseInsensitive() {
        Notification n = buildNotification("sms", false);
        assertDoesNotThrow(() -> notificationService.sendNotification(n));
    }

    @Test
    void sendNotification_pushChannelDoesNotThrow() {
        Notification n = buildNotification("PUSH", false);
        assertDoesNotThrow(() -> notificationService.sendNotification(n));
    }

    @Test
    void sendNotification_pushCaseInsensitive() {
        Notification n = buildNotification("push", false);
        assertDoesNotThrow(() -> notificationService.sendNotification(n));
    }

    @Test
    void sendNotification_unknownChannelThrowsIllegalArgumentException() {
        Notification n = buildNotification("TELEGRAM", false);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> notificationService.sendNotification(n)
        );
        assertTrue(ex.getMessage().contains("TELEGRAM"));
    }

    @Test
    void sendNotification_nullChannelThrowsException() {
        Notification n = buildNotification(null, false);
        // channel is null, will fall through to else branch attempting equalsIgnoreCase on null
        // The equals is called on string literal so it won't NPE, but it won't match
        // and will throw IllegalArgumentException with "Unknown channel: null"
        assertThrows(Exception.class, () -> notificationService.sendNotification(n));
    }

    // ------------------------------------------------------------------ markAsRead

    @Test
    void markAsRead_setsReadToTrue() {
        Notification n = buildNotification("EMAIL", false);
        assertFalse(n.isRead());
        notificationService.markAsRead(n);
        assertTrue(n.isRead());
    }

    @Test
    void markAsRead_alreadyReadRemainsTrue() {
        Notification n = buildNotification("EMAIL", true);
        notificationService.markAsRead(n);
        assertTrue(n.isRead());
    }

    // ------------------------------------------------------------------ filterUnread

    @Test
    void filterUnread_returnsOnlyUnreadNotifications() {
        Notification unread1 = buildNotification("EMAIL", false);
        Notification read1   = buildNotification("SMS", true);
        Notification unread2 = buildNotification("PUSH", false);

        List<Notification> result = notificationService.filterUnread(
                Arrays.asList(unread1, read1, unread2));

        assertEquals(2, result.size());
        assertTrue(result.contains(unread1));
        assertTrue(result.contains(unread2));
        assertFalse(result.contains(read1));
    }

    @Test
    void filterUnread_allReadReturnsEmptyList() {
        Notification r1 = buildNotification("EMAIL", true);
        Notification r2 = buildNotification("SMS", true);

        List<Notification> result = notificationService.filterUnread(Arrays.asList(r1, r2));
        assertTrue(result.isEmpty());
    }

    @Test
    void filterUnread_emptyInputReturnsEmpty() {
        List<Notification> result = notificationService.filterUnread(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void filterUnread_allUnreadReturnsAll() {
        Notification u1 = buildNotification("EMAIL", false);
        Notification u2 = buildNotification("PUSH", false);

        List<Notification> result = notificationService.filterUnread(Arrays.asList(u1, u2));
        assertEquals(2, result.size());
    }

    // ------------------------------------------------------------------ helpers

    private Notification buildNotification(String channel, boolean isRead) {
        Notification n = new Notification();
        n.setChannel(channel);
        n.setRead(isRead);
        return n;
    }
}
