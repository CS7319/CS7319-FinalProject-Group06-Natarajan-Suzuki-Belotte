package com.CS7319.Group06.eventual.searchservice.client.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for deserializing Event responses from event-service.
 * Field names are snake_case (event-service uses @JsonNaming SnakeCaseStrategy).
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EventDto {
    private int eventId;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String organizerEmail;
    private String organizerName;
    private int capacity;
    private String eventType;
    private Integer groupId;
    private List<String> categoryTypes;
}
