package com.CS7319.Group06.eventual.dao;

import com.CS7319.Group06.eventual.model.search.EventDocument;
import com.CS7319.Group06.eventual.model.search.EventSearchRequest;
import com.CS7319.Group06.eventual.model.search.GroupDocument;
import com.CS7319.Group06.eventual.model.search.GroupSearchRequest;
import com.CS7319.Group06.eventual.model.search.SearchResult;

/**
 * DAO interface for Elasticsearch search operations.
 */
public interface SearchDao {

    /**
     * Allows to search for events
     *
     * @param request
     * @return
     */
    SearchResult<EventDocument> searchEvents(EventSearchRequest request);

    /**
     * Allows to search for groups
     *
     * @param request
     * @return
     */
    SearchResult<GroupDocument> searchGroups(GroupSearchRequest request);
}
