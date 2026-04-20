package com.CS7319.Group06.eventual.searchservice.kafka;

import com.CS7319.Group06.eventual.searchservice.kafka.message.EventCreatedMessage;
import com.CS7319.Group06.eventual.searchservice.kafka.message.EventDeletedMessage;
import com.CS7319.Group06.eventual.searchservice.kafka.message.EventUpdatedMessage;
import com.CS7319.Group06.eventual.searchservice.kafka.message.GroupDeletedMessage;
import com.CS7319.Group06.eventual.searchservice.kafka.message.GroupIndexedMessage;
import com.CS7319.Group06.eventual.searchservice.model.EventDocument;
import com.CS7319.Group06.eventual.searchservice.model.GroupDocument;
import com.CS7319.Group06.eventual.searchservice.service.IngestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer that listens for event and group lifecycle messages
 * and drives Elasticsearch ingestion.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SearchServiceKafkaConsumer {

    private final IngestionService ingestionService;
    private final ObjectMapper objectMapper;

    // Listen for event-created messages and index them
    @KafkaListener(topics = "${kafka.topics.event-created}", groupId = "search-service")
    public void onEventCreated(Object payload) {
        try {
            EventCreatedMessage message = objectMapper.convertValue(payload, EventCreatedMessage.class);
            log.debug("Received event-created for eventId={}", message.getEventId());

            EventDocument doc = new EventDocument();
            doc.setTitle(message.getTitle());
            doc.setDescription(message.getDescription());
            doc.setLocation(message.getLocation());
            doc.setStartDatetime(message.getStartDatetime());
            doc.setEndDatetime(message.getEndDatetime());
            doc.setOrganizerEmail(message.getOrganizerEmail());
            doc.setOrganizerName(message.getOrganizerName());
            doc.setEventType(message.getEventType());
            doc.setGroupId(message.getGroupId());
            doc.setCategoryTypes(message.getCategoryTypes());
            doc.setCapacity(message.getCapacity());

            ingestionService.indexEventDocument(String.valueOf(message.getEventId()), doc);
        } catch (Exception e) {
            log.error("Failed to process event-created message: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.event-updated}", groupId = "search-service")
    public void onEventUpdated(Object payload) {
        try {
            EventUpdatedMessage message = objectMapper.convertValue(payload, EventUpdatedMessage.class);
            log.debug("Received event-updated for eventId={}", message.getEventId());

            EventDocument doc = new EventDocument();
            doc.setTitle(message.getTitle());
            doc.setDescription(message.getDescription());
            doc.setLocation(message.getLocation());
            doc.setStartDatetime(message.getStartDatetime());
            doc.setEndDatetime(message.getEndDatetime());
            doc.setOrganizerEmail(message.getOrganizerEmail());
            doc.setOrganizerName(message.getOrganizerName());
            doc.setEventType(message.getEventType());
            doc.setGroupId(message.getGroupId());
            doc.setCategoryTypes(message.getCategoryTypes());
            doc.setCapacity(message.getCapacity());

            ingestionService.indexEventDocument(String.valueOf(message.getEventId()), doc);
        } catch (Exception e) {
            log.error("Failed to process event-updated message: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.event-deleted}", groupId = "search-service")
    public void onEventDeleted(Object payload) {
        try {
            EventDeletedMessage message = objectMapper.convertValue(payload, EventDeletedMessage.class);
            log.debug("Received event-deleted for eventId={}", message.getEventId());
            ingestionService.deleteEventDocument(String.valueOf(message.getEventId()));
        } catch (Exception e) {
            log.error("Failed to process event-deleted message: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.group-indexed}", groupId = "search-service")
    public void onGroupIndexed(Object payload) {
        try {
            GroupIndexedMessage message = objectMapper.convertValue(payload, GroupIndexedMessage.class);
            log.debug("Received group-indexed for groupId={}", message.getGroupId());

            GroupDocument doc = new GroupDocument();
            doc.setName(message.getName());
            doc.setDescription(message.getDescription());
            doc.setOwnerEmail(message.getOwnerEmail());
            doc.setIsPublic(message.isPublic());
            doc.setMemberCount(message.getMemberCount());

            ingestionService.indexGroupDocument(String.valueOf(message.getGroupId()), doc);
        } catch (Exception e) {
            log.error("Failed to process group-indexed message: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.group-deleted}", groupId = "search-service")
    public void onGroupDeleted(Object payload) {
        try {
            GroupDeletedMessage message = objectMapper.convertValue(payload, GroupDeletedMessage.class);
            log.debug("Received group-deleted for groupId={}", message.getGroupId());
            ingestionService.deleteGroupDocument(String.valueOf(message.getGroupId()));
        } catch (Exception e) {
            log.error("Failed to process group-deleted message: {}", e.getMessage(), e);
        }
    }
}
