package com.CS7319.Group06.eventual.searchservice.dao;

import com.CS7319.Group06.eventual.searchservice.model.EventDocument;
import com.CS7319.Group06.eventual.searchservice.model.EventSearchRequest;
import com.CS7319.Group06.eventual.searchservice.model.GroupDocument;
import com.CS7319.Group06.eventual.searchservice.model.GroupSearchRequest;
import com.CS7319.Group06.eventual.searchservice.model.SearchResult;

/**
 * DAO interface for Elasticsearch search operations.
 */
public interface SearchDao {

    /**
     * Allows to search for events.
     *
     * @param request
     * @return
     */
    SearchResult<EventDocument> searchEvents(EventSearchRequest request);

    /**
     * Allows to search for groups.
     *
     * @param request
     * @return
     */
    SearchResult<GroupDocument> searchGroups(GroupSearchRequest request);
}
