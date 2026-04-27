package com.CS7319.Group06.eventual.dao.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.CS7319.Group06.eventual.config.ElasticsearchConfig;
import com.CS7319.Group06.eventual.dao.IngestionDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.search.EventDocument;
import com.CS7319.Group06.eventual.model.search.GroupDocument;
import org.springframework.stereotype.Repository;

import java.io.IOException;

/**
 * Elasticsearch implementation of IngestionDao.
 * Handles indexing and deletion of event and group documents.
 */
@Repository
public class ElasticsearchIngestionDao extends AbstractSearchDao implements IngestionDao {

    public ElasticsearchIngestionDao(ElasticsearchClient client, ElasticsearchConfig config) {
        super(client, config);
    }

    @Override
    public void indexEvent(String eventId, EventDocument document) {
        try {
            client.index(i -> i.index(config.getEventsIndex()).id(eventId).document(document));
        } catch (IOException e) {
            throw new DaoException("Failed to index event " + eventId + " in Elasticsearch", e);
        }
    }

    @Override
    public void deleteEvent(String eventId) {
        try {
            client.delete(d -> d.index(config.getEventsIndex()).id(eventId));
        } catch (IOException e) {
            throw new DaoException("Failed to delete event " + eventId + " from Elasticsearch", e);
        }
    }

    @Override
    public void indexGroup(String groupId, GroupDocument document) {
        try {
            client.index(i -> i.index(config.getGroupsIndex()).id(groupId).document(document));
        } catch (IOException e) {
            throw new DaoException("Failed to index group " + groupId + " in Elasticsearch", e);
        }
    }

    @Override
    public void deleteGroup(String groupId) {
        try {
            client.delete(d -> d.index(config.getGroupsIndex()).id(groupId));
        } catch (IOException e) {
            throw new DaoException("Failed to delete group " + groupId + " from Elasticsearch", e);
        }
    }
}
