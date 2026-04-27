package com.CS7319.Group06.eventual.eventservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * EventDeletedMessage - event deleted message.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDeletedMessage {
    private int eventId;
    private String title;
    private List<String> rsvpedEmails;
}
