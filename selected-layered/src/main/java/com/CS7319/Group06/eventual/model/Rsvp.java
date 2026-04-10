package com.CS7319.Group06.eventual.model;

import com.CS7319.Group06.eventual.model.constants.RsvpStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a users RSVP to an event.
 *
 * @author harininatarajan
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Rsvp {

    private int id;

    private int eventId;

    @NotEmpty
    private String userEmail;

    private RsvpStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
