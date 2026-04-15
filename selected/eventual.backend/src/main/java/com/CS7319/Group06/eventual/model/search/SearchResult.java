package com.CS7319.Group06.eventual.model.search;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Generic paginated search result
 *
 * @param <T> EventDocument or GroupDocument
 * @author harininatarajan
 */
@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SearchResult<T> {

    private List<T> hits; //results from search

    private long totalHits; //total matched documents

    private int page;

    private int size;
}
