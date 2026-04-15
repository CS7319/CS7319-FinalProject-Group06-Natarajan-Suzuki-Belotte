package com.CS7319.Group06.eventual.service;

import com.CS7319.Group06.eventual.model.Event;
import com.CS7319.Group06.eventual.model.Group;

/**
 * Service for keeping the Elasticsearch index in sync with the PostgreSQL database.
 *
 * @author harininatarajan
 */
public interface IngestionService {

    /**
     * Index or update an event document in Elasticsearch.
     *
     * @param event
     */
    void indexEvent(Event event);

    /**
     * Delete an event document from Elasticsearch.
     *
     * @param eventId
     */
    void deleteEvent(int eventId);

    /**
     * Index or update a group document in Elasticsearch.
     *
     * @param group
     */
    void indexGroup(Group group);

    /**
     * Delete a group document from Elasticsearch.
     *
     * @param groupId
     */
    void deleteGroup(int groupId);
}
