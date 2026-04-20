package com.CS7319.Group06.eventual.notificationservice.model;

import com.CS7319.Group06.eventual.notificationservice.model.constants.NotificationReferenceType;
import com.CS7319.Group06.eventual.notificationservice.model.constants.NotificationType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Notification - notification.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Notification {
    private int id;
    private String recipientEmail;
    private NotificationType type;
    private String title;
    private String message;
    private String referenceId;
    private NotificationReferenceType referenceType;
    private boolean isRead;
    private LocalDateTime createdAt;
}
