package com.CS7319.Group06.eventual.notificationservice.service;

import com.CS7319.Group06.eventual.notificationservice.model.Notification;

import java.util.List;

/**
 * Service for managing user notifications.
 */
public interface NotificationService {
    List<Notification> getNotificationsForUser(String email, int page, int size);
    long countUnread(String email);
    Notification markAsRead(int id, String email);
    void markAllAsRead(String email);
    void deleteNotification(int id, String email);
}
