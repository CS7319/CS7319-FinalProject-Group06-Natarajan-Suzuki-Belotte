package com.CS7319.Group06.eventual.notificationservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RsvpCreatedMessage - rsvp created message.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RsvpCreatedMessage {
    private int eventId;
    private String eventTitle;
    private String organizerEmail;
    private String userEmail;
    private String status;
}
