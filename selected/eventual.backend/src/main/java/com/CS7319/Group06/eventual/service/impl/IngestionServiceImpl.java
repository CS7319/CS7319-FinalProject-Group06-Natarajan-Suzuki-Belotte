package com.CS7319.Group06.eventual.service.impl;

import com.CS7319.Group06.eventual.dao.IngestionDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.Event;
import com.CS7319.Group06.eventual.model.Group;
import com.CS7319.Group06.eventual.model.search.EventDocument;
import com.CS7319.Group06.eventual.model.search.GroupDocument;
import com.CS7319.Group06.eventual.service.EmbeddingService;
import com.CS7319.Group06.eventual.service.IngestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation for IngestionService
 */
@Slf4j
@Service
public class IngestionServiceImpl implements IngestionService {

    private final IngestionDao ingestionDao;
    private final EmbeddingService embeddingService;

    public IngestionServiceImpl(IngestionDao ingestionDao, EmbeddingService embeddingService) {
        this.ingestionDao = ingestionDao;
        this.embeddingService = embeddingService;
    }

    @Async("ingestionExecutor")
    @Override
    public void indexEvent(Event event) {
        EventDocument doc = toEventDocument(event);
        doc.setEmbedding(tryGenerateEmbedding(buildEventText(event), "event", event.getEventId()));

        try {
            ingestionDao.indexEvent(String.valueOf(event.getEventId()), doc);
            log.debug("Indexed event {} in Elasticsearch", event.getEventId());
        } catch (DaoException e) {
            log.warn("Failed to index event {}: {}", event.getEventId(), e.getMessage());
        }
    }

    @Async("ingestionExecutor")
    @Override
    public void deleteEvent(int eventId) {
        try {
            ingestionDao.deleteEvent(String.valueOf(eventId));
            log.debug("Deleted event {} from Elasticsearch", eventId);
        } catch (DaoException e) {
            log.warn("Failed to delete event {} from Elasticsearch: {}", eventId, e.getMessage());
        }
    }

    @Async("ingestionExecutor")
    @Override
    public void indexGroup(Group group) {
        GroupDocument doc = toGroupDocument(group);
        doc.setEmbedding(tryGenerateEmbedding(buildGroupText(group), "group", group.getGroupId()));

        try {
            ingestionDao.indexGroup(String.valueOf(group.getGroupId()), doc);
            log.debug("Indexed group {} in Elasticsearch", group.getGroupId());
        } catch (DaoException e) {
            log.warn("Failed to index group {}: {}", group.getGroupId(), e.getMessage());
        }
    }

    @Async("ingestionExecutor")
    @Override
    public void deleteGroup(int groupId) {
        try {
            ingestionDao.deleteGroup(String.valueOf(groupId));
            log.debug("Deleted group {} from Elasticsearch", groupId);
        } catch (DaoException e) {
            log.warn("Failed to delete group {} from Elasticsearch: {}", groupId, e.getMessage());
        }
    }

    private EventDocument toEventDocument(Event event) {
        EventDocument doc = new EventDocument();
        doc.setTitle(event.getTitle());
        doc.setDescription(event.getDescription());
        doc.setLocation(event.getLocation());
        doc.setStartDatetime(event.getStartDateTime() != null ? event.getStartDateTime().toString() : null);
        doc.setEndDatetime(event.getEndDateTime() != null ? event.getEndDateTime().toString() : null);
        doc.setOrganizerEmail(event.getOrganizerEmail());
        doc.setOrganizerName(event.getOrganizerName());
        doc.setEventType(event.getEventType() != null ? event.getEventType().name() : null);
        doc.setGroupId(event.getGroupId());
        doc.setCategoryTypes(event.getCategoryTypes());
        doc.setCapacity(event.getCapacity());
        return doc;
    }

    private GroupDocument toGroupDocument(Group group) {
        GroupDocument doc = new GroupDocument();
        doc.setName(group.getName());
        doc.setDescription(group.getDescription());
        doc.setOwnerEmail(group.getOwnerEmail());
        doc.setIsPublic(group.getIsPublic());
        doc.setMemberCount(group.getMemberEmails() != null ? group.getMemberEmails().size() : 0);
        return doc;
    }

    //Builds a single string for semantic matches
    private String buildEventText(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.getTitle()).append(" ");
        sb.append(event.getDescription()).append(" ");
        sb.append(event.getLocation());
        List<String> categories = event.getCategoryTypes();
        if (categories != null && !categories.isEmpty()) {
            sb.append(" ").append(String.join(" ", categories));
        }
        return sb.toString().trim();
    }

    private String buildGroupText(Group group) {
        return group.getName() + " " + group.getDescription();
    }

    //Generates an embedding, returning null if Ollama is unavailable.
    private float[] tryGenerateEmbedding(String text, String entityType, int entityId) {
        try {
            return embeddingService.generateEmbedding(text);
        } catch (Exception e) {
            log.warn("Embedding unavailable for {} {}, indexing without vector: {}", entityType, entityId, e.getMessage());
            return null;
        }
    }
}
