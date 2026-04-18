package com.CS7319.Group06.eventual.userservice.kafka;

/**
 * KafkaTopics - kafka topics.
 */
public final class KafkaTopics {

    public static final String GROUP_INDEXED = "group-indexed";
    public static final String GROUP_DELETED = "group-deleted";
    public static final String JOIN_REQUEST_SUBMITTED = "join-request-submitted";
    public static final String JOIN_REQUEST_APPROVED = "join-request-approved";
    public static final String JOIN_REQUEST_REJECTED = "join-request-rejected";

    private KafkaTopics() {}
}
