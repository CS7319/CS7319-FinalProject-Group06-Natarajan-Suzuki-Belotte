package com.CS7319.Group06.eventual.service.impl;

import com.CS7319.Group06.eventual.dao.EventDao;
import com.CS7319.Group06.eventual.dao.GroupDao;
import com.CS7319.Group06.eventual.dao.IngestionDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.Event;
import com.CS7319.Group06.eventual.model.Group;
import com.CS7319.Group06.eventual.model.search.EventDocument;
import com.CS7319.Group06.eventual.model.search.GroupDocument;
import com.CS7319.Group06.eventual.model.search.ReindexStatus;
import com.CS7319.Group06.eventual.service.EmbeddingService;
import com.CS7319.Group06.eventual.service.ReindexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Runs a full paginated reindex of all events and groups from PostgreSQL into Elasticsearch.
 * Runs in a background thread; exposes status for polling.
 */
@Slf4j
@Service
public class ReindexServiceImpl implements ReindexService {

    private static final int PAGE_SIZE = 100;

    private final EventDao eventDao;
    private final GroupDao groupDao;
    private final IngestionDao ingestionDao;
    private final EmbeddingService embeddingService;
    private final Executor reindexExecutor;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicReference<ReindexStatus> statusRef = new AtomicReference<>(new ReindexStatus());

    public ReindexServiceImpl(
            EventDao eventDao,
            GroupDao groupDao,
            IngestionDao ingestionDao,
            EmbeddingService embeddingService,
            @Qualifier("reindexExecutor") Executor reindexExecutor) {
        this.eventDao = eventDao;
        this.groupDao = groupDao;
        this.ingestionDao = ingestionDao;
        this.embeddingService = embeddingService;
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
        int total = eventDao.countEvents();
        status.setTotalEvents(total);
        log.info("Reindexing {} events", total);

        int page = 0;
        while ((long) page * PAGE_SIZE < total) {
            List<Event> events = eventDao.getEventsPaginated(page, PAGE_SIZE);
            if (events.isEmpty()) break;

            for (Event event : events) {
                try {
                    EventDocument doc = toEventDocument(event);
                    doc.setEmbedding(tryGenerateEmbedding(buildEventText(event), "event", event.getEventId()));
                    ingestionDao.indexEvent(String.valueOf(event.getEventId()), doc);
                    status.setEventsIndexed(status.getEventsIndexed() + 1);
                } catch (DaoException e) {
                    status.setEventsFailed(status.getEventsFailed() + 1);
                    log.warn("Failed to index event {}: {}", event.getEventId(), e.getMessage());
                }
            }
            page++;
        }
    }

    private void reindexGroups(ReindexStatus status) {
        int total = groupDao.countGroups();
        status.setTotalGroups(total);
        log.info("Reindexing {} groups", total);

        int page = 0;
        while ((long) page * PAGE_SIZE < total) {
            List<Group> groups = groupDao.getGroupsPaginated(page, PAGE_SIZE);
            if (groups.isEmpty()) break;

            for (Group group : groups) {
                try {
                    GroupDocument doc = toGroupDocument(group);
                    doc.setEmbedding(tryGenerateEmbedding(buildGroupText(group), "group", group.getGroupId()));
                    ingestionDao.indexGroup(String.valueOf(group.getGroupId()), doc);
                    status.setGroupsIndexed(status.getGroupsIndexed() + 1);
                } catch (DaoException e) {
                    status.setGroupsFailed(status.getGroupsFailed() + 1);
                    log.warn("Failed to index group {}: {}", group.getGroupId(), e.getMessage());
                }
            }
            page++;
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

    private String buildEventText(Event event) {
        StringBuilder sb = new StringBuilder();
        if (event.getTitle() != null) sb.append(event.getTitle()).append(" ");
        if (event.getDescription() != null) sb.append(event.getDescription()).append(" ");
        if (event.getLocation() != null) sb.append(event.getLocation());
        List<String> categories = event.getCategoryTypes();
        if (categories != null && !categories.isEmpty()) {
            sb.append(" ").append(String.join(" ", categories));
        }
        return sb.toString().trim();
    }

    private String buildGroupText(Group group) {
        return (group.getName() != null ? group.getName() : "") + " " +
               (group.getDescription() != null ? group.getDescription() : "");
    }

    private float[] tryGenerateEmbedding(String text, String entityType, int entityId) {
        try {
            return embeddingService.generateEmbedding(text);
        } catch (Exception e) {
            log.warn("Embedding unavailable for {} {}: {}", entityType, entityId, e.getMessage());
            return null;
        }
    }
}
