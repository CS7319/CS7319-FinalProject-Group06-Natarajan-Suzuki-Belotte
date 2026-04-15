package com.CS7319.Group06.eventual.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Published when a user cancels their RSVP.
 *
 * @author harininatarajan
 */
@Data
@AllArgsConstructor
public class RsvpCancelledNotification {

    private int eventId;

    private String eventTitle;

    private String organizerEmail;

    private String userEmail;
}
