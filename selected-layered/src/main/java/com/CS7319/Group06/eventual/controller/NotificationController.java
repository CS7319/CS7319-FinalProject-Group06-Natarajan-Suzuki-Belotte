package com.CS7319.Group06.eventual.controller;

import com.CS7319.Group06.eventual.model.Notification;
import com.CS7319.Group06.eventual.model.search.BaseSearchRequest;
import com.CS7319.Group06.eventual.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Notifications Controller
 *
 * @author harininatarajan
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Returns paginated notifications for the logged-in user, newest first.
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            @ModelAttribute BaseSearchRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(
                authentication.getName(), request.getPage(), request.getSize()));
    }

    /**
     * Returns the number of unread notifications for the logged-in user.
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> countUnread(Authentication authentication) {
        long count = notificationService.countUnread(authentication.getName());
        return ResponseEntity.ok(Map.of("unread_count", count));
    }

    /**
     * Mark a single notification as read.
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable int id,
            Authentication authentication) {
        return ResponseEntity.ok(notificationService.markAsRead(id, authentication.getName()));
    }

    /**
     * Mark all notifications as read.
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        notificationService.markAllAsRead(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a notification.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable int id,
            Authentication authentication) {
        notificationService.deleteNotification(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
