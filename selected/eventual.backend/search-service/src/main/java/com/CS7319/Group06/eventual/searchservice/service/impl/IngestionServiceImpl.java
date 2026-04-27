package com.CS7319.Group06.eventual.searchservice.service.impl;

import com.CS7319.Group06.eventual.searchservice.dao.IngestionDao;
import com.CS7319.Group06.eventual.searchservice.exception.DaoException;
import com.CS7319.Group06.eventual.searchservice.model.EventDocument;
import com.CS7319.Group06.eventual.searchservice.model.GroupDocument;
import com.CS7319.Group06.eventual.searchservice.service.EmbeddingService;
import com.CS7319.Group06.eventual.searchservice.service.IngestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
    public void indexEventDocument(String eventId, EventDocument doc) {
        String text = buildEventText(doc);
        float[] embedding = tryGenerateEmbedding(text, "event", eventId);
        doc.setEmbedding(embedding);
        try {
            ingestionDao.indexEvent(eventId, doc);
            log.debug("Indexed event {} in Elasticsearch", eventId);
        } catch (DaoException e) {
            log.warn("Failed to index event {}: {}", eventId, e.getMessage());
        }
    }

    @Async("ingestionExecutor")
    @Override
    public void deleteEventDocument(String eventId) {
        try {
            ingestionDao.deleteEvent(eventId);
            log.debug("Deleted event {} from Elasticsearch", eventId);
        } catch (DaoException e) {
            log.warn("Failed to delete event {}: {}", eventId, e.getMessage());
        }
    }

    @Async("ingestionExecutor")
    @Override
    public void indexGroupDocument(String groupId, GroupDocument doc) {
        String text = doc.getName() + " " + doc.getDescription();
        float[] embedding = tryGenerateEmbedding(text, "group", groupId);
        doc.setEmbedding(embedding);
        try {
            ingestionDao.indexGroup(groupId, doc);
            log.debug("Indexed group {} in Elasticsearch", groupId);
        } catch (DaoException e) {
            log.warn("Failed to index group {}: {}", groupId, e.getMessage());
        }
    }

    @Async("ingestionExecutor")
    @Override
    public void deleteGroupDocument(String groupId) {
        try {
            ingestionDao.deleteGroup(groupId);
            log.debug("Deleted group {} from Elasticsearch", groupId);
        } catch (DaoException e) {
            log.warn("Failed to delete group {}: {}", groupId, e.getMessage());
        }
    }

    private String buildEventText(EventDocument doc) {
        StringBuilder sb = new StringBuilder();
        if (doc.getTitle() != null) sb.append(doc.getTitle()).append(" ");
        if (doc.getDescription() != null) sb.append(doc.getDescription()).append(" ");
        if (doc.getLocation() != null) sb.append(doc.getLocation());
        if (doc.getCategoryTypes() != null && !doc.getCategoryTypes().isEmpty()) {
            sb.append(" ").append(String.join(" ", doc.getCategoryTypes()));
        }
        return sb.toString().trim();
    }

    private float[] tryGenerateEmbedding(String text, String entityType, String entityId) {
        try {
            return embeddingService.generateEmbedding(text);
        } catch (Exception e) {
            log.warn("Embedding unavailable for {} {}: {}", entityType, entityId, e.getMessage());
            return null;
        }
    }
}
