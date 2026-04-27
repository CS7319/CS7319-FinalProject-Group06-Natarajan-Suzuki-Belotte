package com.CS7319.Group06.eventual.eventservice.kafka;

import com.CS7319.Group06.eventual.eventservice.kafka.message.EventCreatedMessage;
import com.CS7319.Group06.eventual.eventservice.kafka.message.EventDeletedMessage;
import com.CS7319.Group06.eventual.eventservice.kafka.message.EventUpdatedMessage;
import com.CS7319.Group06.eventual.eventservice.kafka.message.RsvpCancelledMessage;
import com.CS7319.Group06.eventual.eventservice.kafka.message.RsvpCreatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * EventServiceKafkaProducer - event service kafka producer.
 */
@Slf4j
@Component
public class EventServiceKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String topicEventCreated;
    private final String topicEventUpdated;
    private final String topicEventDeleted;
    private final String topicRsvpCreated;
    private final String topicRsvpCancelled;

    public EventServiceKafkaProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.event-created}") String topicEventCreated,
            @Value("${kafka.topics.event-updated}") String topicEventUpdated,
            @Value("${kafka.topics.event-deleted}") String topicEventDeleted,
            @Value("${kafka.topics.rsvp-created}") String topicRsvpCreated,
            @Value("${kafka.topics.rsvp-cancelled}") String topicRsvpCancelled) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicEventCreated = topicEventCreated;
        this.topicEventUpdated = topicEventUpdated;
        this.topicEventDeleted = topicEventDeleted;
        this.topicRsvpCreated = topicRsvpCreated;
        this.topicRsvpCancelled = topicRsvpCancelled;
    }

    public void publishEventCreated(EventCreatedMessage msg) {
        log.info("Publishing event-created for eventId={}", msg.getEventId());
        kafkaTemplate.send(topicEventCreated, String.valueOf(msg.getEventId()), msg);
    }

    public void publishEventUpdated(EventUpdatedMessage msg) {
        log.info("Publishing event-updated for eventId={}", msg.getEventId());
        kafkaTemplate.send(topicEventUpdated, String.valueOf(msg.getEventId()), msg);
    }

    public void publishEventDeleted(EventDeletedMessage msg) {
        log.info("Publishing event-deleted for eventId={}", msg.getEventId());
        kafkaTemplate.send(topicEventDeleted, String.valueOf(msg.getEventId()), msg);
    }

    public void publishRsvpCreated(RsvpCreatedMessage msg) {
        log.info("Publishing rsvp-created for eventId={}, user={}", msg.getEventId(), msg.getUserEmail());
        kafkaTemplate.send(topicRsvpCreated, String.valueOf(msg.getEventId()), msg);
    }

    public void publishRsvpCancelled(RsvpCancelledMessage msg) {
        log.info("Publishing rsvp-cancelled for eventId={}, user={}", msg.getEventId(), msg.getUserEmail());
        kafkaTemplate.send(topicRsvpCancelled, String.valueOf(msg.getEventId()), msg);
    }
}
