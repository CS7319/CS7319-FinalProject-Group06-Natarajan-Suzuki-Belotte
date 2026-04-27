package com.CS7319.Group06.eventual.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Published just before an event is deleted.
 * RSVPed emails are fetched by the publisher before deletion so they remain available to the listener after the event is deleted
 */
@Data
@AllArgsConstructor
public class EventCancelledNotification {

    private int eventId;

    private String eventTitle;

    private List<String> rsvpedEmails;
}
