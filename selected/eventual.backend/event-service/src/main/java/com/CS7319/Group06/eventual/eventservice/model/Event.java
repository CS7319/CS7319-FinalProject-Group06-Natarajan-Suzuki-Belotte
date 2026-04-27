package com.CS7319.Group06.eventual.eventservice.model;

import com.CS7319.Group06.eventual.eventservice.model.constants.EventType;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Event - event.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Event {
    private int eventId;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    @JsonAlias("createdBy")
    private String organizerEmail;
    private String organizerName;
    private int capacity;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer availableSpots;
    private int waitlistCount;
    private String eventPicture;
    private EventType eventType;
    private Integer groupId;
    private List<String> categoryTypes;
    private String modifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
