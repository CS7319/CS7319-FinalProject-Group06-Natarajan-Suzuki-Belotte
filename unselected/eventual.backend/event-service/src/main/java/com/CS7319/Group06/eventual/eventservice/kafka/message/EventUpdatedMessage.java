package com.CS7319.Group06.eventual.eventservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Kafka message published when an event is updated.
 * Contains full event data for ES re-indexing + rsvpedEmails for notifications.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdatedMessage {
    // Core fields for notifications
    private int eventId;
    private String title;
    private List<String> rsvpedEmails;

    // Full event data for Elasticsearch re-indexing
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
