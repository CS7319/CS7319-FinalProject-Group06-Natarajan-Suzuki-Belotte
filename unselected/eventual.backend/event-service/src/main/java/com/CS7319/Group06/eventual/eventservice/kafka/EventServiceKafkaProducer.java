package com.CS7319.Group06.eventual.eventservice.kafka;

import com.CS7319.Group06.eventual.eventservice.kafka.message.EventCreatedMessage;
import com.CS7319.Group06.eventual.eventservice.kafka.message.EventDeletedMessage;
import com.CS7319.Group06.eventual.eventservice.kafka.message.EventUpdatedMessage;
import com.CS7319.Group06.eventual.eventservice.kafka.message.RsvpCancelledMessage;
import com.CS7319.Group06.eventual.eventservice.kafka.message.RsvpCreatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * EventServiceKafkaProducer - event service kafka producer.
 */
@Slf4j
@Component
public class EventServiceKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventServiceKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEventCreated(EventCreatedMessage msg) {
        log.info("Publishing event-created for eventId={}", msg.getEventId());
        kafkaTemplate.send(KafkaTopics.EVENT_CREATED, String.valueOf(msg.getEventId()), msg);
    }

    public void publishEventUpdated(EventUpdatedMessage msg) {
        log.info("Publishing event-updated for eventId={}", msg.getEventId());
        kafkaTemplate.send(KafkaTopics.EVENT_UPDATED, String.valueOf(msg.getEventId()), msg);
    }

    public void publishEventDeleted(EventDeletedMessage msg) {
        log.info("Publishing event-deleted for eventId={}", msg.getEventId());
        kafkaTemplate.send(KafkaTopics.EVENT_DELETED, String.valueOf(msg.getEventId()), msg);
    }

    public void publishRsvpCreated(RsvpCreatedMessage msg) {
        log.info("Publishing rsvp-created for eventId={}, user={}", msg.getEventId(), msg.getUserEmail());
        kafkaTemplate.send(KafkaTopics.RSVP_CREATED, String.valueOf(msg.getEventId()), msg);
    }

    public void publishRsvpCancelled(RsvpCancelledMessage msg) {
        log.info("Publishing rsvp-cancelled for eventId={}, user={}", msg.getEventId(), msg.getUserEmail());
        kafkaTemplate.send(KafkaTopics.RSVP_CANCELLED, String.valueOf(msg.getEventId()), msg);
    }
}
