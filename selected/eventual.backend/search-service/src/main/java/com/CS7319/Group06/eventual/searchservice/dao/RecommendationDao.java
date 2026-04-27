package com.CS7319.Group06.eventual.searchservice.dao;

import com.CS7319.Group06.eventual.searchservice.model.EventDocument;
import com.CS7319.Group06.eventual.searchservice.model.EventRecommendationContext;
import com.CS7319.Group06.eventual.searchservice.model.GroupDocument;
import com.CS7319.Group06.eventual.searchservice.model.GroupRecommendationContext;
import com.CS7319.Group06.eventual.searchservice.model.SearchResult;

/**
 * DAO interface for Elasticsearch recommendation queries.
 */
public interface RecommendationDao {

    /**
     * Recommend events to the users based on their profile.
     *
     * @param context
     * @return
     */
    SearchResult<EventDocument> recommendEvents(EventRecommendationContext context);

    /**
     * Recommend groups to the user based on their profile.
     *
     * @param context
     * @return
     */
    SearchResult<GroupDocument> recommendGroups(GroupRecommendationContext context);
}
