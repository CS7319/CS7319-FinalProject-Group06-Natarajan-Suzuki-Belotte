package com.CS7319.Group06.eventual.searchservice.kafka.message;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

/**
 * Kafka message received when an event is deleted by the Event Service.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EventDeletedMessage {

    private int eventId;
    private String title;
    private List<String> rsvpedEmails;
}
