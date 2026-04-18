package com.CS7319.Group06.eventual.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Published when a user RSVPs to an event.
 */
@Data
@AllArgsConstructor
public class RsvpCreatedNotification {

    private int eventId;

    private String eventTitle;

    private String organizerEmail;

    private String userEmail;

    private String status;
}
