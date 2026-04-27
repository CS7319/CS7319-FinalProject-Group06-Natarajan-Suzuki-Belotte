package com.CS7319.Group06.eventual.notificationservice.kafka.message;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Kafka message received when an event is updated.
 * The notification service only uses eventId, title, and rsvpedEmails.
 * Additional fields are present in the payload (for ES re-indexing by search-service) but ignored here.
 */
@Data
@NoArgsConstructor
public class EventUpdatedMessage {
    private int eventId;
    private String title;
    private List<String> rsvpedEmails;

    // Additional fields present in payload (ignored by notification service)
    private String description;
    private String location;
    private String startDatetime;
    private String endDatetime;
    private String organizerEmail;
    private String organizerName;
    private String eventType;
    private Integer groupId;
    private List<String> categoryTypes;
    private int capacity;
}
