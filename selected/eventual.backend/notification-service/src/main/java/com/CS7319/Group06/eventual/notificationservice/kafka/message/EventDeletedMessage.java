package com.CS7319.Group06.eventual.notificationservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * EventDeletedMessage - event deleted message.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDeletedMessage {
    private int eventId;
    private String title;
    private List<String> rsvpedEmails;
}
