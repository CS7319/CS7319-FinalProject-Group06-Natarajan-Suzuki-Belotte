package com.CS7319.Group06.eventual.model.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

/**
 * Elasticsearch document representing an indexed event.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EventDocument {

    private String id; //ES document id

    private String title;

    private String description;

    private String location;

    private String startDatetime;

    private String endDatetime;

    private String organizerEmail;

    private String organizerName;

    private String eventType;

    private Integer groupId;

    private List<String> categoryTypes;

    private int capacity;

    // For semantic search and not exposed in API responses
    @JsonIgnore
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private float[] embedding;
}
