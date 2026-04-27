package com.CS7319.Group06.eventual.notificationservice.dao;

import com.CS7319.Group06.eventual.notificationservice.model.Notification;

import java.util.List;

/**
 * Data access for notifications.
 */
public interface NotificationDao {
    Notification createNotification(Notification notification);
    List<Notification> getNotificationsForUser(String recipientEmail, int page, int size);
    long countUnread(String recipientEmail);
    Notification markAsRead(int id, String recipientEmail);
    int markAllAsRead(String recipientEmail);
    int deleteById(int id, String recipientEmail);
}
