package com.CS7319.Group06.eventual.model;

import com.CS7319.Group06.eventual.model.constants.TicketStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a support ticket submitted by a user.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SupportTicket {

    private int ticketId;

    @NotEmpty
    private String subject;

    @NotEmpty
    private String content;

    private String submittedBy;

    private TicketStatus status;

    private LocalDateTime resolvedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
