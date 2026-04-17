package com.CS7319.Group06.eventual.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Published when a new GROUP-type event is created.
 */
@Data
@AllArgsConstructor
public class NewGroupEventCreatedNotification {

    private int eventId;

    private String eventTitle;

    private int groupId;
    
    private String organizerEmail;
}
