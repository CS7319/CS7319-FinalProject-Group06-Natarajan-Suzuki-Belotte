package com.CS7319.Group06.eventual.service;

import com.CS7319.Group06.eventual.model.Notification;

import java.util.List;

/**
 * Service for managing user notifications.
 *
 * @author harininatarajan
 */
public interface NotificationService {

    /**
     * Get paginated notifications for a user, newest first.
     *
     * @param email
     * @param page
     * @param size
     * @return
     */
    List<Notification> getNotificationsForUser(String email, int page, int size);

    /**
     * Count unread notifications for a user.
     *
     * @param email
     * @return
     */
    long countUnread(String email);

    /**
     * Mark a single notification as read.
     *
     * @param id
     * @param email
     * @return
     */
    Notification markAsRead(int id, String email);

    /**
     * Mark all the users notifications as read.
     *
     * @param email
     */
    void markAllAsRead(String email);

    /**
     * Delete a notification.
     *
     * @param id
     * @param email
     */
    void deleteNotification(int id, String email);
}
