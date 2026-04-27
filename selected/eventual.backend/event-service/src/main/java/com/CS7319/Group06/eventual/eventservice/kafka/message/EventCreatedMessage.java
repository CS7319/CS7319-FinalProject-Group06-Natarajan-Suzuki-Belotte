package com.CS7319.Group06.eventual.eventservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * EventCreatedMessage - event created message.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCreatedMessage {
    private int eventId;
    private String title;
    private String description;
    private String location;
    private String startDatetime;
    private String endDatetime;
    private String organizerEmail;
    private String organizerName;
    private String eventType;
    private Integer groupId;
    private List<String> categoryTypes;
    private int capacity;
}
