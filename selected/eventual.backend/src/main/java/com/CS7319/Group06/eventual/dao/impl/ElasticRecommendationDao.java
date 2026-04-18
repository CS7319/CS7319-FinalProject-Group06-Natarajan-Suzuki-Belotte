package com.CS7319.Group06.eventual.dao.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.CS7319.Group06.eventual.config.ElasticsearchConfig;
import com.CS7319.Group06.eventual.dao.RecommendationDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.search.EventDocument;
import com.CS7319.Group06.eventual.model.search.EventRecommendationContext;
import com.CS7319.Group06.eventual.model.search.GroupDocument;
import com.CS7319.Group06.eventual.model.search.GroupRecommendationContext;
import com.CS7319.Group06.eventual.model.search.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Elasticsearch implementation of RecommendationDao.
 */
@Slf4j
@Repository
public class ElasticRecommendationDao extends AbstractSearchDao implements RecommendationDao {

    public ElasticRecommendationDao(ElasticsearchClient client, ElasticsearchConfig config) {
        super(client, config);
    }

    @Override
    public SearchResult<EventDocument> recommendEvents(EventRecommendationContext context) {
        List<Query> filters = buildEventRecommendationFilters(context);

        // Boost events in the users location
        BoolQuery.Builder boolQuery = new BoolQuery.Builder().filter(filters);
        if (context.getLocation() != null && !context.getLocation().isBlank()) {
            boolQuery.should(Query.of(q -> q.match(m -> m
                    .field("location")
                    .query(context.getLocation())
                    .boost(2.0f))));
        }

        try {
            return executeSearch(boolQuery.build(), filters, context.getEmbedding(),
                    config.getEventsIndex(), context.getPage(), context.getSize(), EventDocument.class);
        } catch (ElasticsearchException e) {
            if (e.status() == 404) return emptyResult(context.getPage(), context.getSize());
            throw new DaoException("Elasticsearch recommendation query failed for events: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new DaoException("Unable to connect to Elasticsearch", e);
        }
    }

    @Override
    public SearchResult<GroupDocument> recommendGroups(GroupRecommendationContext context) {
        List<Query> filters = buildGroupRecommendationFilters(context);
        BoolQuery boolQuery = new BoolQuery.Builder().filter(filters).build();

        try {
            return executeSearch(boolQuery, filters, context.getEmbedding(),
                    config.getGroupsIndex(), context.getPage(), context.getSize(), GroupDocument.class);
        } catch (ElasticsearchException e) {
            if (e.status() == 404) return emptyResult(context.getPage(), context.getSize());
            throw new DaoException("Elasticsearch recommendation query failed for groups: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new DaoException("Unable to connect to Elasticsearch", e);
        }
    }

    private List<Query> buildEventRecommendationFilters(EventRecommendationContext context) {
        List<Query> filters = new ArrayList<>();

        // Only recommend future events
        filters.add(Query.of(q -> q.range(r -> r
                .field("start_datetime")
                .gte(JsonData.of("now")))));

        // Match users interests
        if (context.getCategoryTypes() != null && !context.getCategoryTypes().isEmpty()) {
            List<FieldValue> values = context.getCategoryTypes().stream().map(FieldValue::of).toList();
            filters.add(Query.of(q -> q.terms(t -> t
                    .field("category_types")
                    .terms(tv -> tv.value(values)))));
        }

        // Exclude events the user already RSVPed to
        if (context.getExcludeEventIds() != null && !context.getExcludeEventIds().isEmpty()) {
            List<String> ids = context.getExcludeEventIds();
            filters.add(Query.of(q -> q.bool(b -> b
                    .mustNot(Query.of(q2 -> q2.ids(i -> i.values(ids)))))));
        }

        return filters;
    }

    private List<Query> buildGroupRecommendationFilters(GroupRecommendationContext context) {
        List<Query> filters = new ArrayList<>();

        // Exclude groups the user is already a member of
        if (context.getExcludeGroupIds() != null && !context.getExcludeGroupIds().isEmpty()) {
            List<String> ids = context.getExcludeGroupIds();
            filters.add(Query.of(q -> q.bool(b -> b
                    .mustNot(Query.of(q2 -> q2.ids(i -> i.values(ids)))))));
        }

        return filters;
    }
}
