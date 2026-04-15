package com.CS7319.Group06.eventual.dao.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.json.JsonData;
import com.CS7319.Group06.eventual.config.ElasticsearchConfig;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.search.EventDocument;
import com.CS7319.Group06.eventual.model.search.EventSearchRequest;
import com.CS7319.Group06.eventual.model.search.GroupDocument;
import com.CS7319.Group06.eventual.model.search.GroupSearchRequest;
import com.CS7319.Group06.eventual.model.search.SearchResult;
import com.CS7319.Group06.eventual.service.EmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Elasticsearch implementation of SearchDao.
 *
 * @author harininatarajan
 */
@Slf4j
@Repository
public class ElasticSearchDao extends AbstractSearchDao implements com.CS7319.Group06.eventual.dao.SearchDao {

    private final EmbeddingService embeddingService;

    public ElasticSearchDao(ElasticsearchClient client,
                            ElasticsearchConfig config,
                            EmbeddingService embeddingService) {
        super(client, config);
        this.embeddingService = embeddingService;
    }

    @Override
    public SearchResult<EventDocument> searchEvents(EventSearchRequest request) {
        List<Query> filters = buildEventFilters(request.getLocation(), request.getEventType(),
                request.getStartAfter(), request.getStartBefore(), request.getCategoryTypes());

        boolean hasQuery = request.getQuery() != null && !request.getQuery().isBlank();

        // title is boosted because a text match in the title is a stronger signal than in the description
        BoolQuery lexicalQuery = buildLexicalQuery(request.getQuery(), hasQuery,
                List.of("title^2", "description"), "title", filters);

        // Only generate an embedding when there is a query string — no point in kNN for filter-only searches
        float[] embedding = hasQuery ? tryGenerateEmbedding(request.getQuery()) : null;

        try {
            return executeSearch(lexicalQuery, filters, embedding,
                    config.getEventsIndex(), request.getPage(), request.getSize(), EventDocument.class);
        } catch (ElasticsearchException e) {
            if (e.status() == 404) return emptyResult(request.getPage(), request.getSize());
            throw new DaoException("Elasticsearch query failed for events: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new DaoException("Unable to connect to Elasticsearch", e);
        }
    }

    @Override
    public SearchResult<GroupDocument> searchGroups(GroupSearchRequest request) {
        List<Query> filters = buildGroupFilters(request.getIsPublic());

        boolean hasQuery = request.getQuery() != null && !request.getQuery().isBlank();

        // name is boosted because a text match in the name is a stronger signal than in the description
        BoolQuery lexicalQuery = buildLexicalQuery(request.getQuery(), hasQuery,
                List.of("name^2", "description"), "name", filters);

        float[] embedding = hasQuery ? tryGenerateEmbedding(request.getQuery()) : null;

        try {
            return executeSearch(lexicalQuery, filters, embedding,
                    config.getGroupsIndex(), request.getPage(), request.getSize(), GroupDocument.class);
        } catch (ElasticsearchException e) {
            if (e.status() == 404) return emptyResult(request.getPage(), request.getSize());
            throw new DaoException("Elasticsearch query failed for groups: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new DaoException("Unable to connect to Elasticsearch", e);
        }
    }

    /**
     * Builds the lexical bool query.
     * When a query string is present:
     * - match_phrase on the primary field with ×10 boost → exact matches always win
     * - multi_match fuzzy across all text fields → partial matches rank second
     * Filters are always applied regardless of whether there is a query string.
     */
    private BoolQuery buildLexicalQuery(String query,
                                        boolean hasQuery,
                                        List<String> multiMatchFields,
                                        String exactField,
                                        List<Query> filters) {
        BoolQuery.Builder builder = new BoolQuery.Builder().filter(filters);

        if (hasQuery) {
            // Exact phrase match — high boost guarantees it ranks above semantic results
            builder.should(Query.of(q -> q.matchPhrase(m -> m
                    .field(exactField)
                    .query(query)
                    .boost(10.0f))));

            // Fuzzy text match across all relevant fields
            builder.should(Query.of(q -> q.multiMatch(m -> m
                    .fields(multiMatchFields)
                    .query(query)
                    .type(TextQueryType.BestFields)
                    .fuzziness("AUTO"))));

            builder.minimumShouldMatch("1");
        }

        return builder.build();
    }

    //Build filters
    private List<Query> buildEventFilters(String location,
                                          String eventType,
                                          String startAfter,
                                          String startBefore,
                                          List<String> categoryTypes) {
        List<Query> filters = new ArrayList<>();

        if (location != null && !location.isBlank()) {
            filters.add(Query.of(q -> q.match(m -> m
                    .field("location")
                    .query(location)
                    .fuzziness("AUTO"))));
        }

        if (eventType != null && !eventType.isBlank()) {
            filters.add(Query.of(q -> q.term(t -> t
                    .field("event_type")
                    .value(eventType.toUpperCase()))));
        }

        if (categoryTypes != null && !categoryTypes.isEmpty()) {
            List<FieldValue> values = categoryTypes.stream().map(FieldValue::of).toList();
            filters.add(Query.of(q -> q.terms(t -> t
                    .field("category_types")
                    .terms(tv -> tv.value(values)))));
        }

        // Date range — applied to start_datetime (JsonData wraps the ISO-8601 string)
        if (startAfter != null && startBefore != null) {
            final String after = startAfter, before = startBefore;
            filters.add(Query.of(q -> q.range(r -> r
                    .field("start_datetime")
                    .gte(JsonData.of(after))
                    .lte(JsonData.of(before)))));
        } else if (startAfter != null) {
            final String after = startAfter;
            filters.add(Query.of(q -> q.range(r -> r
                    .field("start_datetime")
                    .gte(JsonData.of(after)))));
        } else if (startBefore != null) {
            final String before = startBefore;
            filters.add(Query.of(q -> q.range(r -> r
                    .field("start_datetime")
                    .lte(JsonData.of(before)))));
        }

        return filters;
    }

    private List<Query> buildGroupFilters(Boolean isPublic) {
        List<Query> filters = new ArrayList<>();

        if (isPublic != null) {
            filters.add(Query.of(q -> q.term(t -> t
                    .field("is_public")
                    .value(isPublic))));
        }

        return filters;
    }

    //Generate an embedding
    private float[] tryGenerateEmbedding(String query) {
        try {
            return embeddingService.generateEmbedding(query);
        } catch (Exception e) {
            log.warn("Embedding unavailable ({}), falling back to lexical-only search", e.getMessage());
            return null;
        }
    }
}
