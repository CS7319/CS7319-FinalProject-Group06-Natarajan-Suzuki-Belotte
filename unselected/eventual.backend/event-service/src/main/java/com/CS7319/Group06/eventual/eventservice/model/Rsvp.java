package com.CS7319.Group06.eventual.eventservice.model;

import com.CS7319.Group06.eventual.eventservice.model.constants.RsvpStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Rsvp - rsvp.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Rsvp {
    private int id;
    private int eventId;
    private String userEmail;
    private RsvpStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
