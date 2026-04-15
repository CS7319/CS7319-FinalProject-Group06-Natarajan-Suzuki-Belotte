package com.CS7319.Group06.eventual.service;

import com.CS7319.Group06.eventual.model.search.EventDocument;
import com.CS7319.Group06.eventual.model.search.EventSearchRequest;
import com.CS7319.Group06.eventual.model.search.GroupDocument;
import com.CS7319.Group06.eventual.model.search.GroupSearchRequest;
import com.CS7319.Group06.eventual.model.search.SearchResult;

/**
 * Service interface for search operations.
 *
 * @author harininatarajan
 */
public interface SearchService {

    /**
     * Search for events based on the request
     *
     * @param request
     * @return
     */
    SearchResult<EventDocument> searchEvents(EventSearchRequest request);

    /**
     * Search for groups based on the requests
     *
     * @param request
     * @return
     */
    SearchResult<GroupDocument> searchGroups(GroupSearchRequest request);
}
