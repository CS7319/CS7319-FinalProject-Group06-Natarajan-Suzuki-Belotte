package com.CS7319.Group06.eventual.searchservice.service;

import com.CS7319.Group06.eventual.searchservice.model.EventDocument;
import com.CS7319.Group06.eventual.searchservice.model.GroupDocument;

/**
 * Service for keeping the Elasticsearch index in sync with the PostgreSQL database.
 */
public interface IngestionService {

    /**
     * Index or update an event document in Elasticsearch.
     *
     * @param eventId
     * @param doc
     */
    void indexEventDocument(String eventId, EventDocument doc);

    /**
     * Delete an event document from Elasticsearch.
     *
     * @param eventId
     */
    void deleteEventDocument(String eventId);

    /**
     * Index or update a group document in Elasticsearch.
     *
     * @param groupId
     * @param doc
     */
    void indexGroupDocument(String groupId, GroupDocument doc);

    /**
     * Delete a group document from Elasticsearch.
     *
     * @param groupId
     */
    void deleteGroupDocument(String groupId);
}
