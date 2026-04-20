package com.CS7319.Group06.eventual.searchservice.service.impl;

import com.CS7319.Group06.eventual.searchservice.client.EventServiceClient;
import com.CS7319.Group06.eventual.searchservice.client.UserServiceClient;
import com.CS7319.Group06.eventual.searchservice.dao.IngestionDao;
import com.CS7319.Group06.eventual.searchservice.model.EventDocument;
import com.CS7319.Group06.eventual.searchservice.model.GroupDocument;
import com.CS7319.Group06.eventual.searchservice.model.ReindexStatus;
import com.CS7319.Group06.eventual.searchservice.service.EmbeddingService;
import com.CS7319.Group06.eventual.searchservice.service.ReindexService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Runs a full paginated reindex of events and groups from their respective services
 * into Elasticsearch. Runs in a background thread; exposes status for polling.
 */
@Slf4j
@Service
public class ReindexServiceImpl implements ReindexService {

    private static final int PAGE_SIZE = 100;

    private final EventServiceClient eventServiceClient;
    private final UserServiceClient userServiceClient;
    private final IngestionDao ingestionDao;
    private final EmbeddingService embeddingService;
    private final ObjectMapper objectMapper;
    private final Executor reindexExecutor;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicReference<ReindexStatus> statusRef = new AtomicReference<>(new ReindexStatus());

    public ReindexServiceImpl(
            EventServiceClient eventServiceClient,
            UserServiceClient userServiceClient,
            IngestionDao ingestionDao,
            EmbeddingService embeddingService,
            ObjectMapper objectMapper,
            @Qualifier("reindexExecutor") Executor reindexExecutor) {
        this.eventServiceClient = eventServiceClient;
        this.userServiceClient = userServiceClient;
        this.ingestionDao = ingestionDao;
        this.embeddingService = embeddingService;
        this.objectMapper = objectMapper;
        this.reindexExecutor = reindexExecutor;
    }

    @Override
    public void startReindex() {
        if (!running.compareAndSet(false, true)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A reindex is already in progress");
        }
        ReindexStatus initial = new ReindexStatus();
        initial.setState(ReindexStatus.State.RUNNING);
        initial.setStartedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        statusRef.set(initial);

        reindexExecutor.execute(this::doReindex);
    }

    @Override
    public ReindexStatus getStatus() {
        return statusRef.get();
    }

    // -------------------------------------------------------------------------
    // Background reindex logic
    // -------------------------------------------------------------------------

    private void doReindex() {
        ReindexStatus status = statusRef.get();
        try {
            reindexEvents(status);
            reindexGroups(status);

            status.setState(ReindexStatus.State.COMPLETED);
            status.setCompletedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            log.info("Reindex complete — events: {}, groups: {}", status.getEventsIndexed(), status.getGroupsIndexed());
        } catch (Exception e) {
            status.setState(ReindexStatus.State.FAILED);
            status.setError(e.getMessage());
            status.setCompletedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            log.error("Reindex failed: {}", e.getMessage(), e);
        } finally {
            running.set(false);
        }
    }

    private void reindexEvents(ReindexStatus status) {
        int page = 0;
        int total = Integer.MAX_VALUE;

        while ((long) page * PAGE_SIZE < total) {
            Map<String, Object> response = eventServiceClient.getEventsPaginated(page, PAGE_SIZE);
            if (response == null || response.isEmpty()) {
                log.warn("Empty response from event-service on page {}", page);
                break;
            }

            if (page == 0) {
                total = (int) response.getOrDefault("total", 0);
                status.setTotalEvents(total);
                log.info("Reindexing {} events", total);
            }

            List<?> rawEvents = (List<?>) response.get("events");
            if (rawEvents == null || rawEvents.isEmpty()) break;

            for (Object raw : rawEvents) {
                try {
                    var event = objectMapper.convertValue(raw, com.CS7319.Group06.eventual.searchservice.client.dto.EventDto.class);
                    EventDocument doc = toEventDocument(event);
                    doc.setEmbedding(tryGenerateEmbedding(buildEventText(doc), "event", String.valueOf(event.getEventId())));
                    ingestionDao.indexEvent(String.valueOf(event.getEventId()), doc);
                    status.setEventsIndexed(status.getEventsIndexed() + 1);
                } catch (Exception e) {
                    status.setEventsFailed(status.getEventsFailed() + 1);
                    log.warn("Failed to index event: {}", e.getMessage());
                }
            }

            page++;
        }
    }

    private void reindexGroups(ReindexStatus status) {
        int page = 0;
        int total = Integer.MAX_VALUE;

        while ((long) page * PAGE_SIZE < total) {
            Map<String, Object> response = userServiceClient.getGroupsPaginated(page, PAGE_SIZE);
            if (response == null || response.isEmpty()) {
                log.warn("Empty response from user-service on page {}", page);
                break;
            }

            if (page == 0) {
                total = (int) response.getOrDefault("total", 0);
                status.setTotalGroups(total);
                log.info("Reindexing {} groups", total);
            }

            List<?> rawGroups = (List<?>) response.get("groups");
            if (rawGroups == null || rawGroups.isEmpty()) break;

            for (Object raw : rawGroups) {
                try {
                    var group = objectMapper.convertValue(raw, com.CS7319.Group06.eventual.searchservice.client.dto.GroupDto.class);
                    GroupDocument doc = toGroupDocument(group);
                    String text = (doc.getName() != null ? doc.getName() : "") + " " +
                                  (doc.getDescription() != null ? doc.getDescription() : "");
                    doc.setEmbedding(tryGenerateEmbedding(text.trim(), "group", String.valueOf(group.getGroupId())));
                    ingestionDao.indexGroup(String.valueOf(group.getGroupId()), doc);
                    status.setGroupsIndexed(status.getGroupsIndexed() + 1);
                } catch (Exception e) {
                    status.setGroupsFailed(status.getGroupsFailed() + 1);
                    log.warn("Failed to index group: {}", e.getMessage());
                }
            }

            page++;
        }
    }

    // -------------------------------------------------------------------------
    // Mapping helpers
    // -------------------------------------------------------------------------

    private EventDocument toEventDocument(com.CS7319.Group06.eventual.searchservice.client.dto.EventDto event) {
        EventDocument doc = new EventDocument();
        doc.setTitle(event.getTitle());
        doc.setDescription(event.getDescription());
        doc.setLocation(event.getLocation());
        doc.setStartDatetime(event.getStartDateTime() != null ? event.getStartDateTime().toString() : null);
        doc.setEndDatetime(event.getEndDateTime() != null ? event.getEndDateTime().toString() : null);
        doc.setOrganizerEmail(event.getOrganizerEmail());
        doc.setOrganizerName(event.getOrganizerName());
        doc.setEventType(event.getEventType());
        doc.setGroupId(event.getGroupId());
        doc.setCategoryTypes(event.getCategoryTypes());
        doc.setCapacity(event.getCapacity());
        return doc;
    }

    private GroupDocument toGroupDocument(com.CS7319.Group06.eventual.searchservice.client.dto.GroupDto group) {
        GroupDocument doc = new GroupDocument();
        doc.setName(group.getName());
        doc.setDescription(group.getDescription());
        doc.setOwnerEmail(group.getOwnerEmail());
        doc.setIsPublic(Boolean.TRUE.equals(group.getIsPublic()));
        doc.setMemberCount(group.getMemberEmails() != null ? group.getMemberEmails().size() : 0);
        return doc;
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
