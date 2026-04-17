package com.CS7319.Group06.eventual.dao;

import com.CS7319.Group06.eventual.model.search.EventDocument;
import com.CS7319.Group06.eventual.model.search.EventRecommendationContext;
import com.CS7319.Group06.eventual.model.search.GroupDocument;
import com.CS7319.Group06.eventual.model.search.GroupRecommendationContext;
import com.CS7319.Group06.eventual.model.search.SearchResult;

/**
 * DAO interface for Elasticsearch recommendation queries.
 */
public interface RecommendationDao {

    /**
     * Recommend events to the users based on their profile
     *
     * @param context
     * @return
     */
    SearchResult<EventDocument> recommendEvents(EventRecommendationContext context);

    /**
     * Recommend groups to the user based on their profile
     *
     * @param context
     * @return
     */
    SearchResult<GroupDocument> recommendGroups(GroupRecommendationContext context);
}
