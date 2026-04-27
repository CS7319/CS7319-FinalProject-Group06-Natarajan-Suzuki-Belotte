package com.CS7319.Group06.eventual.notificationservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RsvpCancelledMessage - rsvp cancelled message.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RsvpCancelledMessage {
    private int eventId;
    private String eventTitle;
    private String organizerEmail;
    private String userEmail;
}
