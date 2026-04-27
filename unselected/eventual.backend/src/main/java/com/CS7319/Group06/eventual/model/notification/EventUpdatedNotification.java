package com.CS7319.Group06.eventual.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Published when an events details are updated.
 * The listener fetches all RSVPed users and notifies each of them.
 */
@Data
@AllArgsConstructor
public class EventUpdatedNotification {

    private int eventId;

    private String eventTitle;
}
