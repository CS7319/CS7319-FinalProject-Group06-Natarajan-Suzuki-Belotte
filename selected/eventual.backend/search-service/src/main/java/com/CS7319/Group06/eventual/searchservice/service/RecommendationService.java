package com.CS7319.Group06.eventual.searchservice.service;

import com.CS7319.Group06.eventual.searchservice.model.EventDocument;
import com.CS7319.Group06.eventual.searchservice.model.GroupDocument;
import com.CS7319.Group06.eventual.searchservice.model.SearchResult;

/**
 * Service interface for personalized recommendations.
 */
public interface RecommendationService {

    /**
     * Recommend events to the user.
     *
     * @param userEmail
     * @param page
     * @param size
     * @return
     */
    SearchResult<EventDocument> recommendEvents(String userEmail, int page, int size);

    /**
     * Recommend groups to the user.
     *
     * @param userEmail
     * @param page
     * @param size
     * @return
     */
    SearchResult<GroupDocument> recommendGroups(String userEmail, int page, int size);
}
