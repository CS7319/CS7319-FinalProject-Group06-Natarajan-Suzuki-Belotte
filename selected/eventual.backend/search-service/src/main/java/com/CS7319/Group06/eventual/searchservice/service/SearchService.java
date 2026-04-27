package com.CS7319.Group06.eventual.searchservice.service;

import com.CS7319.Group06.eventual.searchservice.model.EventDocument;
import com.CS7319.Group06.eventual.searchservice.model.EventSearchRequest;
import com.CS7319.Group06.eventual.searchservice.model.GroupDocument;
import com.CS7319.Group06.eventual.searchservice.model.GroupSearchRequest;
import com.CS7319.Group06.eventual.searchservice.model.SearchResult;

/**
 * Service interface for search operations.
 */
public interface SearchService {

    /**
     * Search for events based on the request.
     *
     * @param request
     * @return
     */
    SearchResult<EventDocument> searchEvents(EventSearchRequest request);

    /**
     * Search for groups based on the request.
     *
     * @param request
     * @return
     */
    SearchResult<GroupDocument> searchGroups(GroupSearchRequest request);
}
