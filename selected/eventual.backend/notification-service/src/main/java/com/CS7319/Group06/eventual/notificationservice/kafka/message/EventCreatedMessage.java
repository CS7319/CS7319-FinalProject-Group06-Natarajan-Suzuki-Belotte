package com.CS7319.Group06.eventual.notificationservice.kafka.message;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EventCreatedMessage - event created message.
 */
@Data
@NoArgsConstructor
public class EventCreatedMessage {
    private int eventId;
    private String title;
    private String eventType;      // "PUBLIC" or "GROUP"
    private Integer groupId;
    private String organizerEmail;
}
