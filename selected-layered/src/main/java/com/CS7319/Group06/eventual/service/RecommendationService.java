package com.CS7319.Group06.eventual.service;

import com.CS7319.Group06.eventual.model.search.EventDocument;
import com.CS7319.Group06.eventual.model.search.GroupDocument;
import com.CS7319.Group06.eventual.model.search.SearchResult;

/**
 * Service interface for personalized recommendations.
 *
 * @author harininatarajan
 */
public interface RecommendationService {

    /**
     * Recommend events to the user
     *
     * @param userEmail
     * @param page
     * @param size
     * @return
     */
    SearchResult<EventDocument> recommendEvents(String userEmail, int page, int size);

    /**
     * Recommend groups to the users
     *
     * @param userEmail
     * @param page
     * @param size
     * @return
     */
    SearchResult<GroupDocument> recommendGroups(String userEmail, int page, int size);
}
