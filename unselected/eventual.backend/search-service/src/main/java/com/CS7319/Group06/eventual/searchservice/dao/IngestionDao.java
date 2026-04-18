package com.CS7319.Group06.eventual.searchservice.dao;

import com.CS7319.Group06.eventual.searchservice.model.EventDocument;
import com.CS7319.Group06.eventual.searchservice.model.GroupDocument;

/**
 * Data access for Elasticsearch ingestion (index and delete operations).
 */
public interface IngestionDao {

    /**
     * Index (or upsert) an event document.
     *
     * @param eventId
     * @param document
     */
    void indexEvent(String eventId, EventDocument document);

    /**
     * Delete an event document from the index.
     *
     * @param eventId
     */
    void deleteEvent(String eventId);

    /**
     * Index (or upsert) a group document.
     *
     * @param groupId
     * @param document
     */
    void indexGroup(String groupId, GroupDocument document);

    /**
     * Delete a group document from the index.
     *
     * @param groupId
     */
    void deleteGroup(String groupId);
}
