package com.CS7319.Group06.eventual.notificationservice.controller;

import com.CS7319.Group06.eventual.notificationservice.model.BaseSearchRequest;
import com.CS7319.Group06.eventual.notificationservice.model.Notification;
import com.CS7319.Group06.eventual.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Notifications Controller
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Returns paginated notifications for the authenticated user, newest first.
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            @ModelAttribute BaseSearchRequest request,
            @RequestHeader("X-Authenticated-User") String userEmail) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(
                userEmail, request.getPage(), request.getSize()));
    }

    /**
     * Returns the number of unread notifications for the authenticated user.
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> countUnread(
            @RequestHeader("X-Authenticated-User") String userEmail) {
        long count = notificationService.countUnread(userEmail);
        return ResponseEntity.ok(Map.of("unread_count", count));
    }

    /**
     * Mark a single notification as read.
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable int id,
            @RequestHeader("X-Authenticated-User") String userEmail) {
        return ResponseEntity.ok(notificationService.markAsRead(id, userEmail));
    }

    /**
     * Mark all notifications as read.
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @RequestHeader("X-Authenticated-User") String userEmail) {
        notificationService.markAllAsRead(userEmail);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a notification.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable int id,
            @RequestHeader("X-Authenticated-User") String userEmail) {
        notificationService.deleteNotification(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
