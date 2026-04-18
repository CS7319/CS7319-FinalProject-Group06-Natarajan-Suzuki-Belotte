package com.CS7319.Group06.eventual.searchservice.kafka.message;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Kafka message received when an event is updated by the Event Service.
 * Contains full event data so the Search Service can re-index in Elasticsearch.
 */
@Data
@NoArgsConstructor
public class EventUpdatedMessage {
    private int eventId;
    private String title;
    private List<String> rsvpedEmails;

    // Full event data for ES re-indexing
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
