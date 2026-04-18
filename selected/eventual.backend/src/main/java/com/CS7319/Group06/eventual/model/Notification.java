package com.CS7319.Group06.eventual.model;

import com.CS7319.Group06.eventual.model.constants.NotificationReferenceType;
import com.CS7319.Group06.eventual.model.constants.NotificationType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a notification sent to a user.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Notification {

    private int id;

    private String recipientEmail;

    private NotificationType type;

    private String title;

    private String message;

    private String referenceId;  //the event_id or group_id this notification is about

    private NotificationReferenceType referenceType;

    private boolean isRead;

    private LocalDateTime createdAt;
}
