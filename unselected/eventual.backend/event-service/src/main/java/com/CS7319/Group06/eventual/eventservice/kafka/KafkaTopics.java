package com.CS7319.Group06.eventual.eventservice.kafka;

/**
 * KafkaTopics - kafka topics.
 */
public final class KafkaTopics {
    public static final String EVENT_CREATED = "event-created";
    public static final String EVENT_UPDATED = "event-updated";
    public static final String EVENT_DELETED = "event-deleted";
    public static final String RSVP_CREATED = "rsvp-created";
    public static final String RSVP_CANCELLED = "rsvp-cancelled";

    private KafkaTopics() {}
}
