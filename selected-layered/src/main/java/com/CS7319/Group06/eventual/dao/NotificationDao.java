package com.CS7319.Group06.eventual.dao;

import com.CS7319.Group06.eventual.model.Notification;

import java.util.List;

/**
 * Data access for notifications.
 *
 * @author harininatarajan
 */
public interface NotificationDao {

    /**
     * Persist a new notification row.
     *
     * @param notification
     * @return
     */
    Notification createNotification(Notification notification);

    /**
     * Fetch all notifications for a user, newest first, with pagination.
     *
     * @param recipientEmail
     * @param page
     * @param size
     * @return
     */
    List<Notification> getNotificationsForUser(String recipientEmail, int page, int size);

    /**
     * Count unread notifications for a user.
     *
     * @param
     * @return
     */
    long countUnread(String recipientEmail);

    /**
     * Mark a single notification as read for the given user.
     *
     * @param id
     * @param recipientEmail
     * @return
     */
    Notification markAsRead(int id, String recipientEmail);

    /**
     * Mark all unread notifications for a user as read.
     *
     * @param recipientEmail
     * @return
     */
    int markAllAsRead(String recipientEmail);

    /**
     * Delete a notification by id. Only deletes if it belongs to the given user.
     *
     * @param id
     * @param recipientEmail
     * @return
     */
    int deleteById(int id, String recipientEmail);
}
