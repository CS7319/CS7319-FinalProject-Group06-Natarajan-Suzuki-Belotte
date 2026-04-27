package com.CS7319.Group06.eventual.dao.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.CS7319.Group06.eventual.config.ElasticsearchConfig;
import com.CS7319.Group06.eventual.model.search.EventDocument;
import com.CS7319.Group06.eventual.model.search.GroupDocument;
import com.CS7319.Group06.eventual.model.search.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Base class for Search/Recommendation DAOs.
 */
public abstract class AbstractSearchDao {

    protected final ElasticsearchClient client;
    protected final ElasticsearchConfig config;

    protected AbstractSearchDao(ElasticsearchClient client, ElasticsearchConfig config) {
        this.client = client;
        this.config = config;
    }

    //Execute the search request
    protected <T> SearchResult<T> executeSearch(BoolQuery query, List<Query> filters, float[] embedding,
                                                String index, int page, int size, Class<T> docClass) throws IOException {
        if (embedding != null) {
            List<Float> vector = toFloatList(embedding);
            SearchResponse<T> response = client.search(s -> s
                            .index(index)
                            .query(q -> q.bool(query))
                            .knn(k -> k
                                    .field("embedding")
                                    .queryVector(vector)
                                    .k((long) Math.max(size * 3, 30))
                                    .numCandidates((long) Math.max(size * 10, 100))
                                    .filter(filters))
                            .from(page * size)
                            .size(size),
                    docClass);
            return buildResult(response, page, size);
        }

        // If no embedding available then perform lexical / filter-only
        SearchResponse<T> response = client.search(s -> s
                        .index(index)
                        .query(q -> q.bool(query))
                        .from(page * size)
                        .size(size),
                docClass);
        return buildResult(response, page, size);
    }

    //Build the search results
    protected <T> SearchResult<T> buildResult(SearchResponse<T> response, int page, int size) {
        List<T> hits = response.hits().hits().stream()
                .map(hit -> {
                    T doc = hit.source();
                    if (doc instanceof EventDocument e) e.setId(hit.id());
                    if (doc instanceof GroupDocument g) g.setId(hit.id());
                    return doc;
                })
                .filter(Objects::nonNull)
                .toList();

        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        return new SearchResult<>(hits, total, page, size);
    }

    protected <T> SearchResult<T> emptyResult(int page, int size) {
        return new SearchResult<>(List.of(), 0, page, size);
    }

    //Converts primitive float[] to List<Float> required by the ES kNN query vector.
    protected List<Float> toFloatList(float[] array) {
        List<Float> list = new ArrayList<>(array.length);
        for (float f : array) list.add(f);
        return list;
    }
}
